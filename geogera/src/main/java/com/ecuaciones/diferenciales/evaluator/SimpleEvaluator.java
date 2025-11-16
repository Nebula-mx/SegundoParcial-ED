package com.ecuaciones.diferenciales.evaluator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Evaluador simple que retorna JSON
 * 
 * USO:
 *   String json = SimpleEvaluator.evaluate("y'' - 5*y' + 6*y = 0");
 *   System.out.println(json);
 */
public class SimpleEvaluator {
    
    private static EcuationParser parser = new EcuationParser();
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Evalúa una ecuación y retorna JSON string
     */
    public static String evaluate(String ecuacion) {
        return evaluate(ecuacion, "AUTO", new ArrayList<>());
    }
    
    /**
     * Evalúa una ecuación con método especificado
     */
    public static String evaluate(String ecuacion, String metodo) {
        return evaluate(ecuacion, metodo, new ArrayList<>());
    }
    
    /**
     * Evalúa una ecuación con todo
     */
    public static String evaluate(String ecuacion, String metodo, List<String> condicionesIniciales) {
        Map<String, Object> resultado = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Validar
            if (ecuacion == null || ecuacion.trim().isEmpty()) {
                resultado.put("status", "ERROR");
                resultado.put("message", "Ecuación vacía");
                resultado.put("code", 400);
                return toJson(resultado);
            }
            
            // Parsear
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
            
            // Información básica
            resultado.put("expression", ecuacion);
            resultado.put("order", order);
            resultado.put("isHomogeneous", data.getIsHomogeneous());
            
            if (!data.getIsHomogeneous()) {
                resultado.put("forcingTerm", data.getIndependentTerm().get("g(x)"));
            }
            
            // Resolver raíces
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            
            // Raíces formateadas
            List<Map<String, Object>> rootsList = new ArrayList<>();
            for (int i = 0; i < finalRoots.size(); i++) {
                Root r = finalRoots.get(i);
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("index", i + 1);
                rootMap.put("real", r.getReal());
                rootMap.put("imaginary", r.getImaginary());
                rootMap.put("display", r.toString());
                rootsList.add(rootMap);
            }
            resultado.put("roots", rootsList);
            
            // Solución homogénea
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            resultado.put("homogeneousSolution", solution_h);
            
            // Solución particular (si no es homogénea)
            String solution_p = null;
            String methodUsed = metodo;
            
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                
                String metodoActual = metodo;
                if ("AUTO".equals(metodo)) {
                    metodoActual = "UC";
                }
                
                boolean metodoPrincipalFallo = false;
                
                // Intentar UC
                if ("UC".equals(metodoActual)) {
                    try {
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(finalRoots);
                        String ypForm = ucSolver.getParticularSolutionForm(g_x);
                        
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
                        Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
                        
                        solution_p = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        methodUsed = "UC";
                        
                    } catch (ArithmeticException e) {
                        // Resonancia
                        metodoPrincipalFallo = false;
                        
                    } catch (Exception e) {
                        metodoPrincipalFallo = true;
                        
                        if ("AUTO".equals(metodo)) {
                            // Fallback a VP
                        }
                    }
                }
                
                // Fallback a VP
                if (metodoPrincipalFallo || "VP".equals(metodoActual)) {
                    if (order >= 2) {
                        try {
                            WronskianCalculator wc = new WronskianCalculator(finalRoots);
                            List<String> yFunctions = wc.generateFundamentalSet();
                            double leadingCoeff = coeffsArray[0];
                            
                            VariationOfParametersSolverV2 vpSolver = 
                                new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
                            
                            solution_p = vpSolver.getYpFormula();
                            methodUsed = "VP";
                            
                        } catch (Exception e) {
                            solution_p = null;
                        }
                    }
                }
                
                resultado.put("particularMethod", methodUsed);
                
                if (solution_p != null && !solution_p.startsWith("ERROR")) {
                    String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                    resultado.put("particulatSolution", cleanedYp);
                }
            }
            
            // Solución final
            String finalSolution;
            if (!data.getIsHomogeneous() && solution_p != null && !solution_p.startsWith("ERROR")) {
                String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                finalSolution = "y(x) = (" + solution_h + ") + (" + cleanedYp + ")";
            } else {
                finalSolution = "y(x) = " + solution_h;
            }
            
            resultado.put("finalSolution", finalSolution);
            resultado.put("solutionLatex", toLatex(finalSolution));
            resultado.put("status", "SUCCESS");
            resultado.put("code", 200);
            resultado.put("executionTimeMs", System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            resultado.put("status", "ERROR");
            resultado.put("message", "Error: " + e.getMessage());
            resultado.put("code", 500);
            resultado.put("executionTimeMs", System.currentTimeMillis() - startTime);
        }
        
        return toJson(resultado);
    }
    
    /**
     * Convertir mapa a JSON string
     */
    private static String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (Exception e) {
            return data.toString();
        }
    }
    
    /**
     * Convertir a LaTeX
     */
    private static String toLatex(String expr) {
        if (expr == null) return "";
        String latex = expr;
        latex = latex.replace("e^", "e^{");
        latex = latex.replace("sin(", "\\sin(");
        latex = latex.replace("cos(", "\\cos(");
        return latex;
    }
}
