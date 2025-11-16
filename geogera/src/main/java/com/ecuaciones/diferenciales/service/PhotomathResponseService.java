package com.ecuaciones.diferenciales.service;

import com.ecuaciones.diferenciales.dto.PhotomathResponse;
import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Servicio que retorna respuestas en formato Photomath con pasos detallados
 */
public class PhotomathResponseService {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static PhotomathResponse solve(String ecuacion) {
        return solve(ecuacion, "AUTO", new ArrayList<>());
    }
    
    public static PhotomathResponse solve(String ecuacion, String metodo) {
        return solve(ecuacion, metodo, new ArrayList<>());
    }
    
    public static PhotomathResponse solve(String ecuacion, String metodo, List<String> condicionesIniciales) {
        PhotomathResponse response = new PhotomathResponse();
        long startTime = System.currentTimeMillis();
        
        try {
            // Validaciones básicas
            if (ecuacion == null || ecuacion.trim().isEmpty()) {
                return crearError(response, "Ecuación vacía", startTime);
            }
            
            response.setEquation(ecuacion);
            response.setVariable("x");
            
            // PASO 1: Parsing
            PhotomathResponse.Step stepParsing = new PhotomathResponse.Step(
                "CLASSIFY",
                "📖 Parsing de la ecuación",
                "Convertir la ecuación textual a estructura interna"
            );
            stepParsing.addExpression("Entrada: " + ecuacion);
            response.addStep(stepParsing);
            
            EcuationParser parser = new EcuationParser();
            ExpressionData data = parser.parse(ecuacion);
            
            if (data == null) {
                return crearError(response, "No se pudo parsear la ecuación", startTime);
            }
            
            // PASO 2: Clasificación
            PhotomathResponse.Step stepClassify = new PhotomathResponse.Step(
                "CLASSIFY",
                "🏷️ Clasificación de la EDO",
                "Identificar el tipo y orden de la ecuación"
            );
            
            String tipo = data.getIsHomogeneous() ? "Homogénea" : "No Homogénea";
            stepClassify.addExpression("EDO de orden " + data.getOrder() + ", " + tipo);
            stepClassify.addDetail("Tipo", tipo);
            stepClassify.addDetail("Orden", data.getOrder());
            response.addStep(stepClassify);
            
            // Obtener coeficientes y raíces
            Double[] coeffsArray = data.getCoefficients();
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> roots = PolynomialSolver.solve(coeffs);
            
            if (roots == null || roots.isEmpty()) {
                return crearError(response, "No se pudo extraer el polinomio característico", startTime);
            }
            
            // PASO 3: Ecuación característica
            PhotomathResponse.Step stepChar = new PhotomathResponse.Step(
                "CHARACTERISTIC",
                "📐 Formar la ecuación característica",
                "Reemplazar y con e^(rx) para encontrar la ecuación característica"
            );
            
            String charEq = generarEcuacionCaracteristica(coeffsArray);
            stepChar.addExpression(charEq);
            stepChar.addDetail("Método", "Sustitución exponencial y = e^(rx)");
            response.addStep(stepChar);
            
            // PASO 4: Raíces
            PhotomathResponse.Step stepRoots = new PhotomathResponse.Step(
                "ROOTS",
                "🔍 Encontrar las raíces",
                "Resolver la ecuación característica"
            );
            
            String tipoRaices = clasificarRaices(roots);
            for (Root r : roots) {
                stepRoots.addExpression("r = " + r.toString());
            }
            stepRoots.addDetail("Tipo de raíces", tipoRaices);
            stepRoots.addDetail("Cantidad", roots.size());
            response.addStep(stepRoots);
            
            // PASO 5: Solución homogénea
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solutionH = hSolver.generateHomogeneousSolution(roots);
            
            PhotomathResponse.Step stepHomogeneous = new PhotomathResponse.Step(
                "HOMOGENEOUS_SOLUTION",
                "✨ Construir la solución homogénea",
                "Usar las raíces para construir la solución general"
            );
            
            stepHomogeneous.addExpression("y_h(x) = " + solutionH);
            stepHomogeneous.addDetail("Número de constantes", roots.size());
            stepHomogeneous.addDetail("Método", "Combinación lineal de soluciones fundamentales");
            response.addStep(stepHomogeneous);
            
            // PASO 6: Solución particular (si no es homogénea)
            String solutionP = null;
            String metodoUsado = metodo;
            
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                
                // Determinar método
                if ("AUTO".equals(metodo)) {
                    metodoUsado = determinarMetodo(g_x, roots);
                }
                
                // Intentar resolver particular
                if ("UC".equals(metodoUsado)) {
                    solutionP = resolverConUC(data, roots, g_x);
                } else if ("VP".equals(metodoUsado)) {
                    solutionP = resolverConVP(roots, g_x, coeffsArray[0], data.getOrder());
                }
                
                if (solutionP != null && !solutionP.startsWith("ERROR")) {
                    PhotomathResponse.Step stepParticular = new PhotomathResponse.Step(
                        "PARTICULAR_SOLUTION",
                        "🎯 Construir la solución particular",
                        "Encontrar una solución particular para la ecuación no homogénea"
                    );
                    
                    String cleanedYp = solutionP.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                    stepParticular.addExpression("y_p(x) = " + cleanedYp);
                    stepParticular.addDetail("Método", metodoUsado);
                    stepParticular.addDetail("Término forzante", g_x);
                    response.addStep(stepParticular);
                }
            }
            
            // PASO 7: Solución final
            String finalSolution;
            if (!data.getIsHomogeneous() && solutionP != null && !solutionP.startsWith("ERROR")) {
                String cleanedYp = solutionP.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                finalSolution = "y(x) = (" + solutionH + ") + (" + cleanedYp + ")";
            } else {
                finalSolution = "y(x) = " + solutionH;
            }
            
            PhotomathResponse.Step stepFinal = new PhotomathResponse.Step(
                "FINAL_SOLUTION",
                "🎓 Solución General",
                "Combinar soluciones homogénea y particular"
            );
            stepFinal.addExpression(finalSolution);
            stepFinal.addDetail("Tipo de solución", data.getIsHomogeneous() ? "Solo homogénea" : "Homogénea + Particular");
            response.addStep(stepFinal);
            
            // Metadata
            response.addMetadata("Orden", data.getOrder());
            response.addMetadata("Tipo", tipo);
            response.addMetadata("Pasos totales", response.getSteps().size());
            response.addMetadata("Raíces", tipoRaices);
            response.addMetadata("Método", metodoUsado);
            
            // Resultado final
            response.setStatus("SUCCESS");
            response.setMessage("Ecuación resuelta exitosamente");
            response.setFinalSolution(finalSolution);
            response.setSolutionLatex(toLatex(finalSolution));
            response.setStepCount(response.getSteps().size());
            response.setSuccess(true);
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            
            return response;
            
        } catch (Exception e) {
            return crearError(response, "Error: " + e.getMessage(), startTime);
        }
    }
    
    // ============ MÉTODOS AUXILIARES ============
    
    private static PhotomathResponse crearError(PhotomathResponse response, String message, long startTime) {
        response.setStatus("ERROR");
        response.setMessage(message);
        response.setSuccess(false);
        response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        return response;
    }
    
    private static String generarEcuacionCaracteristica(Double[] coeffs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coeffs.length; i++) {
            double coeff = coeffs[i];
            if (coeff == 0) continue;
            
            int exp = coeffs.length - 1 - i;
            if (coeff > 0 && i > 0) sb.append(" + ");
            if (coeff < 0) sb.append(" - ");
            
            if (Math.abs(coeff) != 1 || exp == 0) {
                sb.append(Math.abs(coeff));
            }
            
            if (exp > 1) {
                sb.append("r^").append(exp);
            } else if (exp == 1) {
                sb.append("r");
            }
        }
        sb.append(" = 0");
        return sb.toString().replaceAll("^\\s+\\+\\s+", "");
    }
    
    private static String clasificarRaices(List<Root> roots) {
        if (roots.isEmpty()) return "Sin raíces";
        
        boolean tieneImaginaria = false;
        for (Root r : roots) {
            if (Math.abs(r.getImaginary()) > 1e-10) {
                tieneImaginaria = true;
                break;
            }
        }
        
        if (tieneImaginaria) {
            return "Complejas Conjugadas";
        } else if (roots.size() == 1) {
            return "Raíz Real Única";
        } else {
            return "Reales Distintas";
        }
    }
    
    private static String determinarMetodo(String g_x, List<Root> roots) {
        // Simplificado: UC para polinomios/exponenciales/trigonom, VP para otros
        if (g_x.contains("sin") || g_x.contains("cos") || g_x.matches(".*\\d+.*")) {
            return "UC";
        }
        return "VP";
    }
    
    private static String resolverConUC(ExpressionData data, List<Root> roots, String g_x) {
        try {
            UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
            String ypForm = ucSolver.getParticularSolutionForm(g_x);
            UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
            Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
            return ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String resolverConVP(List<Root> roots, String g_x, double leadingCoeff, int order) {
        try {
            if (order < 2) return null;
            WronskianCalculator wc = new WronskianCalculator(roots);
            List<String> yFunctions = wc.generateFundamentalSet();
            VariationOfParametersSolverV2 vpSolver = 
                new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
            return vpSolver.getYpFormula();
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String toLatex(String expr) {
        if (expr == null) return "";
        String latex = expr;
        latex = latex.replace("e^", "e^{");
        latex = latex.replace("sin(", "\\sin(");
        latex = latex.replace("cos(", "\\cos(");
        latex = latex.replace("C1", "C₁");
        latex = latex.replace("C2", "C₂");
        latex = latex.replace("C3", "C₃");
        latex = latex.replace("c1", "C₁");
        latex = latex.replace("c2", "C₂");
        latex = latex.replace("c3", "C₃");
        return "$" + latex + "$";
    }
    
    /**
     * Convertir respuesta a JSON string
     */
    public static String toJsonString(PhotomathResponse response) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (Exception e) {
            return "{\"status\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }
}
