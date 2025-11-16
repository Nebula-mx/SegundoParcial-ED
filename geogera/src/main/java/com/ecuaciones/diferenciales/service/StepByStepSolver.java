package com.ecuaciones.diferenciales.service;

import com.ecuaciones.diferenciales.dto.StepResponse;
import com.ecuaciones.diferenciales.dto.StepResponse.Step;
import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;
import java.util.*;

/**
 * Servicio que resuelve ecuaciones y genera pasos detallados tipo Photomath
 */
public class StepByStepSolver {
    
    private EcuationParser parser;
    
    public StepByStepSolver() {
        this.parser = new EcuationParser();
    }
    
    /**
     * Resuelve una ecuación y retorna pasos detallados
     */
    public StepResponse solve(String ecuacion) {
        return solve(ecuacion, "AUTO");
    }
    
    /**
     * Resuelve con método especificado
     */
    public StepResponse solve(String ecuacion, String metodo) {
        long startTime = System.currentTimeMillis();
        StepResponse respuesta = new StepResponse(ecuacion);
        int stepCounter = 1;
        
        try {
            // PASO 1: Parsing
            Step parseo = new Step("PARSE", "📖 Parsing de la ecuación", stepCounter++);
            parseo.addExpression("Entrada: " + ecuacion);
            parseo.setExplanation("Convertir la ecuación textual a estructura interna");
            respuesta.addStep(parseo);
            
            // Validar
            if (ecuacion == null || ecuacion.trim().isEmpty()) {
                respuesta.setStatus("ERROR");
                respuesta.setMessage("Ecuación vacía");
                respuesta.setSuccess(false);
                return respuesta;
            }
            
            // PASO 2: Parse
            ExpressionData data = parser.parse(ecuacion);
            if (data == null) {
                respuesta.setStatus("ERROR");
                respuesta.setMessage("No se pudo parsear la ecuación");
                respuesta.setSuccess(false);
                return respuesta;
            }
            
            Double[] coeffsArray = data.getCoefficients();
            int order = data.getOrder();
            
            if (coeffsArray == null || coeffsArray.length == 0) {
                respuesta.setStatus("ERROR");
                respuesta.setMessage("No se pudo extraer los coeficientes");
                respuesta.setSuccess(false);
                return respuesta;
            }
            
            // PASO 3: Clasificación
            Step clasificacion = new Step("CLASSIFY", "🏷️ Clasificación de la EDO", stepCounter++);
            String tipoEdo = data.getIsHomogeneous() ? "Homogénea" : "No Homogénea";
            clasificacion.addExpression("EDO de orden " + order + ", " + tipoEdo);
            clasificacion.addDetail("Tipo", tipoEdo);
            clasificacion.addDetail("Orden", String.valueOf(order));
            if (!data.getIsHomogeneous()) {
                clasificacion.addDetail("Término Forzado", data.getIndependentTerm().get("g(x)"));
            }
            clasificacion.setExplanation("La ecuación es de orden " + order + " y " + tipoEdo.toLowerCase());
            respuesta.addStep(clasificacion);
            
            // PASO 4: Ecuación Característica
            Step caracteristica = new Step("CHARACTERISTIC", "📐 Formar la ecuación característica", stepCounter++);
            String eqCaracteristica = generarEcuacionCaracteristica(coeffsArray);
            caracteristica.addExpression(eqCaracteristica);
            caracteristica.addDetail("Método", "Sustitución exponencial");
            caracteristica.setExplanation("Reemplazar y con e^(rx) en la ecuación homogénea");
            respuesta.addStep(caracteristica);
            
            // PASO 5: Resolver raíces
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> roots = PolynomialSolver.solve(coeffs);
            
            Step raices = new Step("ROOTS", "🔍 Encontrar las raíces", stepCounter++);
            String tipoRaices = clasificarRaices(roots);
            for (int i = 0; i < roots.size(); i++) {
                Root r = roots.get(i);
                raices.addExpression("r" + (i + 1) + " = " + r.toString());
            }
            raices.addDetail("Tipo de raíces", tipoRaices);
            raices.addDetail("Cantidad", String.valueOf(roots.size()));
            raices.setExplanation("Se obtienen " + roots.size() + " raíces " + tipoRaices.toLowerCase());
            respuesta.addStep(raices);
            
            // PASO 6: Solución Homogénea
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solutionH = hSolver.generateHomogeneousSolution(roots);
            
            Step solutionHomogenea = new Step("HOMOGENEOUS_SOLUTION", "✨ Construir la solución homogénea", stepCounter++);
            solutionHomogenea.addExpression("y(x) = " + solutionH);
            solutionHomogenea.addDetail("Número de constantes", String.valueOf(roots.size()));
            solutionHomogenea.setExplanation("La solución general homogénea es: y(x) = " + solutionH);
            respuesta.addStep(solutionHomogenea);
            
            // PASO 7: Solución Particular (si aplica)
            String solutionP = null;
            String metodoUsado = metodo;
            
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                
                if ("AUTO".equals(metodo)) {
                    metodoUsado = "UC";
                }
                
                boolean ucFallo = false;
                
                // Intentar UC
                if ("UC".equals(metodoUsado)) {
                    try {
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
                        String ypForm = ucSolver.getParticularSolutionForm(g_x);
                        
                        Step ucStep = new Step("UC_FORM", "🎯 Forma de la solución particular (UC)", stepCounter++);
                        ucStep.addExpression("y_p(x) = " + ypForm);
                        ucStep.addDetail("Método", "Coeficientes Indeterminados");
                        ucStep.addDetail("Término Forzado", g_x);
                        ucStep.setExplanation("Basado en la forma del término forzado");
                        respuesta.addStep(ucStep);
                        
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
                        Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
                        
                        solutionP = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        metodoUsado = "UC";
                        
                    } catch (ArithmeticException e) {
                        ucFallo = true;
                    } catch (Exception e) {
                        ucFallo = true;
                    }
                }
                
                // Fallback a VP
                if (ucFallo && "AUTO".equals(metodo)) {
                    if (order >= 2) {
                        try {
                            Step vpStep = new Step("VARIATION_PARAMETERS", "🔄 Usar Variación de Parámetros", stepCounter++);
                            vpStep.setExplanation("El método UC no aplica, usando VP");
                            respuesta.addStep(vpStep);
                            
                            WronskianCalculator wc = new WronskianCalculator(roots);
                            List<String> yFunctions = wc.generateFundamentalSet();
                            double leadingCoeff = coeffsArray[0];
                            
                            VariationOfParametersSolverV2 vpSolver = 
                                new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
                            
                            solutionP = vpSolver.getYpFormula();
                            metodoUsado = "VP";
                            
                        } catch (Exception e) {
                            solutionP = null;
                        }
                    }
                }
                
                if (solutionP != null && !solutionP.startsWith("ERROR")) {
                    String cleanedYp = solutionP.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                    
                    Step solutionParticularStep = new Step("PARTICULAR_SOLUTION", "🌟 Solución particular", stepCounter++);
                    solutionParticularStep.addExpression("y_p(x) = " + cleanedYp);
                    solutionParticularStep.addDetail("Método", metodoUsado);
                    solutionParticularStep.setExplanation("Solución particular obtenida por " + metodoUsado);
                    respuesta.addStep(solutionParticularStep);
                }
            }
            
            // PASO FINAL: Solución General
            String finalSolution;
            if (!data.getIsHomogeneous() && solutionP != null && !solutionP.startsWith("ERROR")) {
                String cleanedYp = solutionP.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                finalSolution = "y(x) = (" + solutionH + ") + (" + cleanedYp + ")";
            } else {
                finalSolution = "y(x) = " + solutionH;
            }
            
            Step finalStep = new Step("FINAL_SOLUTION", "🎓 Solución General Completa", stepCounter++);
            finalStep.addExpression(finalSolution);
            finalStep.setExplanation("Combinación de solución homogénea y particular");
            respuesta.addStep(finalStep);
            
            // Armar respuesta final
            respuesta.setFinalSolution(finalSolution);
            respuesta.setSolutionLatex(toLatex(finalSolution));
            respuesta.setStatus("SUCCESS");
            respuesta.setMessage("Ecuación resuelta exitosamente");
            respuesta.setSuccess(true);
            respuesta.setStepCount(respuesta.getSteps().size());
            
            // Metadata
            respuesta.addMetadata("Orden", String.valueOf(order));
            respuesta.addMetadata("Tipo", tipoEdo);
            respuesta.addMetadata("Pasos totales", String.valueOf(respuesta.getStepCount()));
            respuesta.addMetadata("Raíces", tipoRaices);
            respuesta.addMetadata("Método Particular", metodoUsado);
            
        } catch (Exception e) {
            respuesta.setStatus("ERROR");
            respuesta.setMessage("Error durante resolución: " + e.getMessage());
            respuesta.setSuccess(false);
        }
        
        respuesta.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        return respuesta;
    }
    
    /**
     * Genera la ecuación característica como string
     */
    private String generarEcuacionCaracteristica(Double[] coeffs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coeffs.length; i++) {
            int potencia = coeffs.length - 1 - i;
            if (coeffs[i] == 0) continue;
            
            if (sb.length() > 0 && coeffs[i] > 0) sb.append(" + ");
            if (coeffs[i] < 0) sb.append(" - ");
            
            double absCoeff = Math.abs(coeffs[i]);
            if (absCoeff != 1 || potencia == 0) {
                sb.append(formatDouble(absCoeff));
            }
            
            if (potencia > 1) sb.append("*r^").append(potencia);
            else if (potencia == 1) sb.append("*r");
        }
        sb.append(" = 0");
        return sb.toString().replaceAll("\\s+", " ").trim();
    }
    
    /**
     * Clasifica el tipo de raíces
     */
    private String clasificarRaices(List<Root> roots) {
        if (roots.isEmpty()) return "Ninguna";
        
        boolean tieneCompleja = roots.stream().anyMatch(r -> r.getImaginary() != 0);
        boolean tieneRepetida = roots.size() < 2 || roots.get(0).equals(roots.get(1));
        
        if (tieneCompleja) {
            return "Complejas Conjugadas";
        } else if (tieneRepetida) {
            return "Reales Repetidas";
        } else {
            return "Reales Distintas";
        }
    }
    
    /**
     * Convierte a LaTeX
     */
    private String toLatex(String expr) {
        if (expr == null) return "";
        String latex = expr;
        latex = latex.replace("e^", "e^{");
        latex = latex.replace("sin(", "\\sin(");
        latex = latex.replace("cos(", "\\cos(");
        latex = latex.replace("C1", "C₁");
        latex = latex.replace("C2", "C₂");
        latex = latex.replace("c1", "c₁");
        latex = latex.replace("c2", "c₂");
        return "$" + latex + "$";
    }
    
    /**
     * Formatea números decimales
     */
    private String formatDouble(double value) {
        if (value == (long) value) {
            return String.format("%.0f", value);
        } else {
            return String.format("%.4f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
    }
}
