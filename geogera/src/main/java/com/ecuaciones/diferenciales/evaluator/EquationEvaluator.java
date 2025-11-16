package com.ecuaciones.diferenciales.evaluator;

import java.util.Arrays;
import java.util.List;

import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse;
import com.ecuaciones.diferenciales.dto.DifferentialEquationResponse.RootInfo;
import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;

/**
 * EVALUADOR DE ECUACIONES DIFERENCIALES - Tipo Photomath
 * 
 * USO SIMPLE:
 *   DifferentialEquationResponse response = EquationEvaluator.evaluate("y'' - 5*y' + 6*y = 0");
 *   
 *   if (response.isSuccess()) {
 *       System.out.println(response.getFinalSolution());
 *   } else {
 *       System.out.println(response.getMessage());
 *   }
 * 
 * CON OPCIONES:
 *   response = EquationEvaluator.evaluate(
 *       "y'' + 4*y = sin(2*x)",
 *       "UC",
 *       Arrays.asList("y(0)=1", "y'(0)=2")
 *   );
 */
public class EquationEvaluator {
    
    private static EcuationParser parser = new EcuationParser();
    
    /**
     * Evalúa una ecuación diferencial (Opción 1: solo la ecuación)
     * 
     * @param ecuacion La ecuación diferencial
     * @return DifferentialEquationResponse con toda la información
     */
    public static DifferentialEquationResponse evaluate(String ecuacion) {
        return evaluate(ecuacion, "AUTO", new java.util.ArrayList<>());
    }
    
    /**
     * Evalúa una ecuación diferencial (Opción 2: con método especificado)
     * 
     * @param ecuacion La ecuación diferencial
     * @param method "UC", "VP" o "AUTO"
     * @return DifferentialEquationResponse con toda la información
     */
    public static DifferentialEquationResponse evaluate(String ecuacion, String method) {
        return evaluate(ecuacion, method, new java.util.ArrayList<>());
    }
    
    /**
     * Evalúa una ecuación diferencial (Opción 3: completa)
     * 
     * @param ecuacion La ecuación diferencial (ej: "y'' - 5*y' + 6*y = 0")
     * @param method Método de resolución: "UC", "VP" o "AUTO"
     * @param initialConditions Condiciones iniciales (ej: ["y(0)=1", "y'(0)=2"])
     * @return DifferentialEquationResponse con toda la información
     */
    public static DifferentialEquationResponse evaluate(String ecuacion, String method, 
                                                       List<String> initialConditions) {
        
        DifferentialEquationResponse response = new DifferentialEquationResponse();
        
        try {
            // ==================== VALIDACIONES ====================
            if (ecuacion == null || ecuacion.trim().isEmpty()) {
                response.setStatus("ERROR");
                response.setCode(400);
                response.setMessage("La ecuación no puede estar vacía");
                return response;
            }
            
            response.setEquation(ecuacion);
            response.setMethod(method);
            response.setInitialConditions(initialConditions);
            
            // Validar que sea ecuación diferencial
            if (!isValidDifferentialEquation(ecuacion)) {
                response.setStatus("ERROR");
                response.setCode(400);
                response.setMessage("No es una ecuación diferencial válida. Debe contener: y, y', y'', etc.");
                return response;
            }
            
            // ==================== PARSEAR ====================
            ExpressionData data = parser.parse(ecuacion);
            
            if (data == null) {
                response.setStatus("ERROR");
                response.setCode(400);
                response.setMessage("No se pudo parsear la ecuación");
                return response;
            }
            
            Double[] coeffsArray = data.getCoefficients();
            int order = data.getOrder();
            
            if (coeffsArray == null || coeffsArray.length == 0 || order <= 0) {
                response.setStatus("ERROR");
                response.setCode(400);
                response.setMessage("No se pudo extraer el polinomio característico");
                return response;
            }
            
            // Guardar información de la ecuación
            response.setOrder(order);
            response.setHomogeneous(data.getIsHomogeneous());
            response.setCoefficients(Arrays.asList(coeffsArray));
            
            if (!data.getIsHomogeneous()) {
                response.setForcingTerm(data.getIndependentTerm().get("g(x)"));
            }
            
            response.addResolutionStep("Ecuación parseada correctamente");
            response.addResolutionStep("Orden: " + order);
            response.addResolutionStep("Tipo: " + (data.getIsHomogeneous() ? "Homogénea" : "No-homogénea"));
            
            // ==================== RESOLVER RAÍCES ====================
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            
            response.addResolutionStep("Resolviendo ecuación característica...");
            
            for (int i = 0; i < finalRoots.size(); i++) {
                Root r = finalRoots.get(i);
                response.addRoot(new RootInfo(
                    i + 1,
                    r.getReal(),
                    r.getImaginary(),
                    r.toString()
                ));
            }
            
            response.addResolutionStep("Raíces encontradas: " + finalRoots.size());
            
            // ==================== SOLUCIÓN HOMOGÉNEA ====================
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            
            response.setHomogeneousSolution(solution_h);
            response.setHomogeneousSolutionLatex(toLatex(solution_h));
            response.addResolutionStep("Solución homogénea: y_h = " + solution_h);
            
            // ==================== SOLUCIÓN PARTICULAR (si no es homogénea) ====================
            String solution_p = null;
            String method_used = method;
            
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                response.addResolutionStep("Término de forzamiento: g(x) = " + g_x);
                
                String metodoActual = method;
                if ("AUTO".equals(method)) {
                    metodoActual = "UC";
                    response.addResolutionStep("Estrategia AUTO: Intentar UC primero");
                }
                
                boolean metodoPrincipalFallo = false;
                
                // Intentar UC
                if ("UC".equals(metodoActual)) {
                    response.addResolutionStep("Aplicando Coeficientes Indeterminados (UC)...");
                    
                    try {
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(finalRoots);
                        String ypForm = ucSolver.getParticularSolutionForm(g_x);
                        response.addResolutionStep("  • Forma propuesta: y_p = " + ypForm);
                        
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
                        java.util.Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
                        response.addResolutionStep("  • Coeficientes calculados: " + solvedCoeffs);
                        
                        solution_p = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        response.addResolutionStep("  ✓ UC exitoso");
                        method_used = "UC";
                        
                    } catch (ArithmeticException e) {
                        response.addResolutionStep("  ⚠ Sistema singular (RESONANCIA detectada)");
                        response.addResolutionStep("  • UC maneja la resonancia analíticamente");
                        metodoPrincipalFallo = false;
                        
                    } catch (Exception e) {
                        metodoPrincipalFallo = true;
                        response.addResolutionStep("  ✗ Error en UC: " + e.getMessage());
                        
                        if ("AUTO".equals(method)) {
                            response.addResolutionStep("  • Switcheando a Variación de Parámetros...");
                        }
                    }
                }
                
                // Fallback a VP
                if (metodoPrincipalFallo || "VP".equals(metodoActual)) {
                    response.addResolutionStep("Aplicando Variación de Parámetros (VP)...");
                    
                    if (order < 2) {
                        solution_p = "VP requiere orden >= 2";
                        response.addResolutionStep("  ✗ Error: VP requiere orden >= 2");
                    } else {
                        try {
                            WronskianCalculator wc = new WronskianCalculator(finalRoots);
                            List<String> yFunctions = wc.generateFundamentalSet();
                            double leadingCoeff = coeffsArray[0];
                            
                            VariationOfParametersSolverV2 vpSolver = 
                                new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
                            
                            solution_p = vpSolver.getYpFormula();
                            response.addResolutionStep("  ✓ VP exitoso");
                            method_used = "VP";
                            
                        } catch (Exception ex) {
                            solution_p = "Error: " + ex.getMessage();
                            response.addResolutionStep("  ✗ Error en VP: " + ex.getMessage());
                        }
                    }
                }
                
                response.setParticularMethod(method_used);
                
                if (solution_p != null) {
                    String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                    response.setParticulatSolution(cleanedYp);
                    response.setParticulatSolutionLatex(toLatex(cleanedYp));
                    response.addResolutionStep("Solución particular: y_p = " + cleanedYp);
                }
            }
            
            // ==================== SOLUCIÓN FINAL ====================
            String finalSolution;
            if (!data.getIsHomogeneous() && solution_p != null && !solution_p.startsWith("Error")) {
                String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                finalSolution = "y(x) = (" + solution_h + ") + (" + cleanedYp + ")";
            } else {
                finalSolution = "y(x) = " + solution_h;
            }
            
            response.setFinalSolution(finalSolution);
            response.setFinalSolutionLatex(toLatex(finalSolution));
            response.addResolutionStep("✅ Solución general: " + finalSolution);
            
            // ==================== SUCCESS ====================
            response.setStatus("SUCCESS");
            response.setCode(200);
            
        } catch (Exception e) {
            response.setStatus("ERROR");
            response.setCode(500);
            response.setMessage("Error interno: " + e.getMessage());
            response.addResolutionStep("❌ Excepción: " + e.getClass().getSimpleName());
        }
        
        return response;
    }
    
    /**
     * Valida si es una ecuación diferencial
     */
    private static boolean isValidDifferentialEquation(String ecuacion) {
        String[] patterns = { "dy/dx", "d2y/dx2", "y'", "y''", "y'''" };
        for (String pattern : patterns) {
            if (ecuacion.contains(pattern)) return true;
        }
        
        if (ecuacion.contains("y") && ecuacion.contains("=")) return true;
        if (ecuacion.contains("y^")) return true;
        
        return false;
    }
    
    /**
     * Convierte a LaTeX básico
     */
    private static String toLatex(String expr) {
        if (expr == null) return "";
        
        String latex = expr;
        latex = latex.replace("e^", "e^{");
        latex = latex.replace("sin(", "\\sin(");
        latex = latex.replace("cos(", "\\cos(");
        latex = latex.replace("tan(", "\\tan(");
        
        return latex;
    }
}
