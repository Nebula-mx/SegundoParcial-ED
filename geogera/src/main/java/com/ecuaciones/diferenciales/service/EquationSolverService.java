package com.ecuaciones.diferenciales.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;
import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver;

/**
 * Servicio de resolución de ecuaciones diferenciales que retorna JSON
 * 
 * USO:
 *   EquationSolverService solver = new EquationSolverService();
 *   String json = solver.solve("y'' - 5*y' + 6*y = 0");
 *   
 * O con opciones:
 *   String json = solver.solve("y'' + 4*y = sin(2*x)", "UC", Arrays.asList("y(0)=1"));
 */
public class EquationSolverService {
    
    private EcuationParser parser;
    private ObjectMapper objectMapper;
    
    public EquationSolverService() {
        this.parser = new EcuationParser();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Resuelve una ecuación diferencial y retorna un JSON
     * 
     * @param ecuacion La ecuación diferencial (ej: "y'' - 5*y' + 6*y = 0")
     * @return JSON con la solución completa
     */
    public String solve(String ecuacion) {
        return solve(ecuacion, "AUTO", new ArrayList<>());
    }
    
    /**
     * Resuelve una ecuación diferencial con método especificado
     * 
     * @param ecuacion La ecuación diferencial
     * @param metodo "UC", "VP" o "AUTO"
     * @return JSON con la solución completa
     */
    public String solve(String ecuacion, String metodo) {
        return solve(ecuacion, metodo, new ArrayList<>());
    }
    
    /**
     * Resuelve una ecuación diferencial con todas las opciones
     * 
     * @param ecuacion La ecuación diferencial
     * @param metodo "UC", "VP" o "AUTO"
     * @param condicionesIniciales Lista de condiciones iniciales (ej: ["y(0)=1", "y'(0)=2"])
     * @return JSON con la solución completa
     */
    public String solve(String ecuacion, String metodo, List<String> condicionesIniciales) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Validaciones iniciales
            if (ecuacion == null || ecuacion.trim().isEmpty()) {
                resultado.put("status", "ERROR");
                resultado.put("message", "La ecuación no puede estar vacía");
                resultado.put("code", 400);
                return toJson(resultado);
            }
            
            // Validar que sea una ecuación diferencial
            if (!esEcuacionDiferencial(ecuacion)) {
                resultado.put("status", "ERROR");
                resultado.put("message", "No es una ecuación diferencial válida. Debe contener: y, y', y'', etc.");
                resultado.put("code", 400);
                return toJson(resultado);
            }
            
            // Parsear la ecuación
            ExpressionData data = parser.parse(ecuacion);
            
            if (data == null) {
                resultado.put("status", "ERROR");
                resultado.put("message", "No se pudo parsear la ecuación");
                resultado.put("code", 400);
                return toJson(resultado);
            }
            
            Double[] coeffsArray = data.getCoefficients();
            int order = data.getOrder();
            
            if (coeffsArray == null || coeffsArray.length == 0 || order <= 0) {
                resultado.put("status", "ERROR");
                resultado.put("message", "No se pudo extraer el polinomio característico");
                resultado.put("code", 400);
                return toJson(resultado);
            }
            
            // Información de la ecuación
            Map<String, Object> ecuacionInfo = new HashMap<>();
            ecuacionInfo.put("original", ecuacion);
            ecuacionInfo.put("order", order);
            ecuacionInfo.put("coefficients", Arrays.asList(coeffsArray));
            ecuacionInfo.put("isHomogeneous", data.getIsHomogeneous());
            
            if (!data.getIsHomogeneous()) {
                ecuacionInfo.put("forcingTerm", data.getIndependentTerm().get("g(x)"));
            }
            
            resultado.put("equation", ecuacionInfo);
            
            // Resolver las raíces
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            
            // Raíces formateadas
            List<Map<String, Object>> rootsInfo = new ArrayList<>();
            for (int i = 0; i < finalRoots.size(); i++) {
                Root r = finalRoots.get(i);
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("index", i + 1);
                rootMap.put("real", r.getReal());
                rootMap.put("imaginary", r.getImaginary());
                rootMap.put("display", r.toString());
                rootsInfo.add(rootMap);
            }
            resultado.put("roots", rootsInfo);
            
            // Solución homogénea
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            
            Map<String, Object> solutionInfo = new HashMap<>();
            solutionInfo.put("homogeneous", solution_h);
            solutionInfo.put("homogeneousLatex", latexFormat(solution_h));
            
            // Solución particular (si no es homogénea)
            String solution_p = null;
            String method_used = metodo;
            Map<String, Object> particularInfo = new HashMap<>();
            
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                particularInfo.put("forcingTerm", g_x);
                
                // Determinar método automáticamente si es AUTO
                String metodoActual = metodo;
                if ("AUTO".equals(metodo)) {
                    metodoActual = "UC";
                }
                
                boolean metodoPrincipalFallo = false;
                List<String> steps = new ArrayList<>();
                
                // Intentar UC
                if ("UC".equals(metodoActual)) {
                    try {
                        steps.add("Resolviendo con Coeficientes Indeterminados...");
                        
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(finalRoots);
                        String ypForm = ucSolver.getParticularSolutionForm(g_x);
                        steps.add("Forma propuesta: y_p = " + ypForm);
                        
                        List<String> ypCoeffNames = ucSolver.getCoeffNames();
                        steps.add("Incógnitas: " + ypCoeffNames.toString());
                        
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
                        Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
                        steps.add("Coeficientes calculados: " + solvedCoeffs.toString());
                        
                        solution_p = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        steps.add("✓ UC exitoso");
                        method_used = "UC";

                        // Verificación: comprobar que L[y_p] = g(x). Si no se cumple, marcar fallo y forzar VP
                        try {
                            String cleanedYpForCheck = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                            StringBuilder lhsBuilder = new StringBuilder();
                            for (int k = 0; k <= order; k++) {
                                double a_k = coeffsArray[order - k];
                                String deriv = SymbolicDifferentiator.calculateDerivative(cleanedYpForCheck, k);
                                if (lhsBuilder.length() > 0) lhsBuilder.append(" + ");
                                lhsBuilder.append("(").append(formatDouble(a_k)).append(")*").append("(").append(deriv).append(")");
                            }
                            String lhsExpr = lhsBuilder.toString();
                            String residual = SymbolicDifferentiator.simplify("(" + lhsExpr + ") - (" + g_x + ")");
                            // Si el residual no es 0, marcar fallo
                            if (residual == null || !residual.equals("0")) {
                                steps.add("⚠️ UC no satisface la EDO tras sustitución (residual!=0): " + residual);
                                steps.add("Intentando Variación de Parámetros como fallback...");
                                metodoPrincipalFallo = true;
                                method_used = "UC (fallback -> VP)";
                            }
                        } catch (Exception exCheck) {
                            // Si la verificación falla por algún error de simplificación, permitir fallback
                            steps.add("⚠️ No se pudo verificar UC simbólicamente: " + exCheck.getMessage());
                            metodoPrincipalFallo = true;
                            method_used = "UC (fallback -> VP)";
                        }
                        
                    } catch (ArithmeticException e) {
                        // Sistema singular - resonancia
                        steps.add("⚠️ Sistema singular (RESONANCIA detectada)");
                        steps.add("Intentando Variación de Parámetros como fallback...");
                        metodoPrincipalFallo = true;  // CORRECCIÓN: Permite fallback a VP
                        method_used = "UC (con resonancia -> VP)";
                        
                    } catch (Exception e) {
                        metodoPrincipalFallo = true;
                        steps.add("✗ Error en UC: " + e.getMessage());
                        
                        if ("AUTO".equals(metodo)) {
                            steps.add("Switcheando a Variación de Parámetros...");
                        }
                    }
                }
                
                // Fallback a VP si UC falló o se seleccionó VP
                if (metodoPrincipalFallo || "VP".equals(metodoActual)) {
                    steps.add("Usando Variación de Parámetros (VP)...");
                    
                    if (order < 2) {
                        solution_p = "VP requiere orden >= 2";
                        steps.add("✗ Error: VP requiere orden >= 2");
                    } else {
                        try {
                            WronskianCalculator wc = new WronskianCalculator(finalRoots);
                            List<String> yFunctions = wc.generateFundamentalSet();
                            double leadingCoeff = coeffsArray[0];
                            
                            VariationOfParametersSolverV2 vpSolver = 
                                new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
                            
                            solution_p = vpSolver.getYpFormula();
                            steps.add("✓ VP exitoso");
                            method_used = "VP";
                            
                        } catch (Exception ex) {
                            solution_p = "Error: " + ex.getMessage();
                            steps.add("✗ Error en VP: " + ex.getMessage());
                        }
                    }
                }
                
                particularInfo.put("method", method_used);
                particularInfo.put("steps", steps);
                
                // Limpiar y_p
                if (solution_p != null) {
                    String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                    particularInfo.put("particular", cleanedYp);
                    particularInfo.put("particularLatex", latexFormat(cleanedYp));
                }
            }
            
            solutionInfo.put("particular", particularInfo);
            resultado.put("solution", solutionInfo);
            
            // Solución general
            String finalSolution;
            if (!data.getIsHomogeneous() && solution_p != null && !solution_p.startsWith("Error")) {
                String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                finalSolution = "y(x) = (" + solution_h + ") + (" + cleanedYp + ")";
            } else {
                finalSolution = "y(x) = " + solution_h;
            }
            
            resultado.put("finalSolution", finalSolution);
            resultado.put("finalSolutionLatex", latexFormat(finalSolution));
            
            // NUEVA SECCIÓN: Aplicar Condiciones Iniciales (PVI)
            Map<String, Object> pviInfo = new HashMap<>();
            if (condicionesIniciales != null && !condicionesIniciales.isEmpty()) {
                resultado.put("initialConditions", condicionesIniciales);
                
                try {
                    // Parsear condiciones iniciales
                    List<InitialConditionsSolver.InitialCondition> parsedConditions = 
                        InitialConditionsSolver.parseConditions(condicionesIniciales);
                    
                    // Crear solver con la solución general
                    InitialConditionsSolver pviSolver = 
                        new InitialConditionsSolver(finalSolution, order);
                    
                    // Resolver constantes C_i
                    Map<String, Double> constants = pviSolver.solveInitialConditions(parsedConditions);
                    
                    // Sustituir constantes en la solución
                    String particularSolution = substituteConstants(finalSolution, constants);
                    
                    pviInfo.put("particularSolution", particularSolution);
                    pviInfo.put("particularSolutionLatex", latexFormat(particularSolution));
                    pviInfo.put("constants", constants);
                    pviInfo.put("appliedInitialConditions", true);
                    pviInfo.put("status", "SOLVED");
                    
                    // Usar la solución particular como respuesta final
                    resultado.put("particularSolution", particularSolution);
                    resultado.put("particularSolutionLatex", latexFormat(particularSolution));
                    resultado.put("constants", constants);
                    
                } catch (Exception e) {
                    pviInfo.put("appliedInitialConditions", false);
                    pviInfo.put("status", "ERROR");
                    pviInfo.put("error", e.getMessage());
                    resultado.put("pviWarning", "No se pudieron aplicar todas las condiciones iniciales: " + e.getMessage());
                }
            } else {
                pviInfo.put("appliedInitialConditions", false);
                pviInfo.put("status", "NO_CONDITIONS");
            }
            
            resultado.put("pvi", pviInfo);
            
            // Status de éxito
            resultado.put("status", "SUCCESS");
            resultado.put("code", 200);
            
        } catch (Exception e) {
            resultado.put("status", "ERROR");
            resultado.put("message", "Error interno: " + e.getMessage());
            resultado.put("code", 500);
            resultado.put("error", e.getClass().getSimpleName());
        }
        
        return toJson(resultado);
    }
    
    /**
     * Verifica si es una ecuación diferencial válida
     */
    private boolean esEcuacionDiferencial(String ecuacion) {
        String[] derivativePatterns = { "dy/dx", "d2y/dx2", "y'", "y''", "y'''" };
        for (String pattern : derivativePatterns) {
            if (ecuacion.contains(pattern)) return true;
        }
        
        if (ecuacion.contains("y") && ecuacion.contains("=")) return true;
        if (ecuacion.contains("y^")) return true;
        
        return false;
    }
    
    /**
     * Convierte a LaTeX (simple, mejora si es necesario)
     */
    private String latexFormat(String expr) {
        if (expr == null) return "";
        
        String latex = expr;
        latex = latex.replace("e^", "e^{");
        latex = latex.replace("sin(", "\\sin(");
        latex = latex.replace("cos(", "\\cos(");
        latex = latex.replace("tan(", "\\tan(");
        
        return latex;
    }
    
    /**
     * Sustituye las constantes C_i en una solución general por sus valores numéricos
     * 
     * @param solution Solución general con C1, C2, ..., Cn
     * @param constants Mapa de valores: {C1: 3.5, C2: -2.1, ...}
     * @return Solución particular con valores numéricos
     */
    private String substituteConstants(String solution, Map<String, Double> constants) {
        String result = solution;
        
        // Sustituir cada constante por su valor
        for (Map.Entry<String, Double> entry : constants.entrySet()) {
            String constName = entry.getKey();
            Double constValue = entry.getValue();
            
            // Formatear el valor
            String valueStr;
            if (Math.abs(constValue - Math.round(constValue)) < 1e-9) {
                valueStr = String.valueOf((long)Math.round(constValue));
            } else {
                valueStr = String.format("%.4f", constValue);
            }
            
            // Reemplazar C_i por el valor (con manejo de signos)
            result = result.replace(constName + "*", valueStr + "*");
            result = result.replace(constName + "+", valueStr + "+");
            result = result.replace(constName + "-", valueStr + "-");
            result = result.replace("(" + constName, "(" + valueStr);
            result = result.replace(" " + constName, " " + valueStr);
        }
        
        // Limpiar paréntesis vacíos o innecesarios
        result = result.replaceAll("\\(\\s*\\)", "");
        result = result.replaceAll("\\s+", " ").trim();
        
        return result;
    }

    private String formatDouble(double value) {
        if (Math.abs(value - Math.round(value)) < 1e-9) {
            return String.valueOf((long)Math.round(value));
        }
        return String.format(java.util.Locale.US, "%s", value);
    }
    
    /**
     * Convierte el mapa a JSON string
     */
    private String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (Exception e) {
            // Fallback a toString simple si hay error
            return data.toString();
        }
    }
    
    /**
     * MÉTODO ALTERNATIVO: Retorna un objeto Map en lugar de JSON string
     * Útil para integración con frontends que prefieren objetos en lugar de strings
     */
    public Map<String, Object> solveAsMap(String ecuacion) {
        return solveAsMap(ecuacion, "AUTO", new ArrayList<>());
    }
    
    public Map<String, Object> solveAsMap(String ecuacion, String metodo) {
        return solveAsMap(ecuacion, metodo, new ArrayList<>());
    }
    
    public Map<String, Object> solveAsMap(String ecuacion, String metodo, List<String> condicionesIniciales) {
        try {
            String jsonResult = solve(ecuacion, metodo, condicionesIniciales);
            return objectMapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Error al convertir resultado: " + e.getMessage());
            error.put("code", 500);
            return error;
        }
    }
}
