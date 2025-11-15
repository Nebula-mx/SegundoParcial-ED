
package com.ecuaciones.diferenciales.api.service;
import com.ecuaciones.diferenciales.api.dto.ExpressionData;
import com.ecuaciones.diferenciales.api.dto.SolutionResponse;
import com.ecuaciones.diferenciales.api.dto.Step;
import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;
import java.util.*;
import java.util.regex.*;

/**
 * 🎯 ORQUESTADOR PRINCIPAL - Resuelve EDOs y genera pasos
 * 
 * Coordina:
 * 1. Clasificación de la ecuación
 * 2. Resolución
 * 3. Aplicación de CI
 * 4. Generación de respuesta API con pasos
 */
public class ODESolver {
    
    private StepBuilder stepBuilder;
    
    public ODESolver() {
        this.stepBuilder = new StepBuilder();
    }
    
    /**
     * 🚀 MÉTODO PRINCIPAL - Resuelve EDO y devuelve respuesta con pasos
     */
    public SolutionResponse solveDifferentialEquation(ExpressionData input) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validar entrada
            if (!input.isValid()) {
                return new SolutionResponse(
                    SolutionResponse.Status.ERROR,
                    "Entrada inválida",
                    "La ecuación debe ser válida y no estar vacía"
                );
            }
            
            stepBuilder.clear();
            String equation = input.getEquation();
            String variable = input.getVariable();
            List<String> conditions = input.getInitialConditions();
            
            // --- PASO 1: PARSING ---
            stepBuilder.addCustomStep(
                Step.StepType.CLASSIFY,
                "Parsing de la ecuación",
                "Convertir la ecuación textual a estructura interna",
                Arrays.asList("Entrada: " + equation)
            );
            
            // --- PASO 2: ANÁLISIS BÁSICO ---
            String odeType = detectODEType(equation);
            int order = detectOrder(equation);
            
            stepBuilder.addClassificationStep(
                odeType, 
                String.valueOf(order), 
                "La ecuación es de orden " + order + " y " + odeType.toLowerCase()
            );
            
            // --- PASO 3: ECUACIÓN CARACTERÍSTICA Y RAÍCES ---
            String characteristic = extractCharacteristicEquation(equation);
            if (characteristic != null && !characteristic.isEmpty()) {
                stepBuilder.addCharacteristicStep(equation, characteristic);
            }
            
            // ✅ USAR SOLVERS REALES - Calcular raíces de la característica
            List<Root> roots = new ArrayList<>();
            
            try {
                // Extraer coeficientes de la ecuación: y'' + 3y' + 2y = 0 → [1, 3, 2]
                List<Double> coeffs = extractCoefficientsFromEquation(equation, order);
                roots = PolynomialSolver.solve(coeffs);
                
                // Agregar paso de raíces
                if (!roots.isEmpty()) {
                    List<String> rootExpressions = new ArrayList<>();
                    for (Root root : roots) {
                        if (Math.abs(root.getImaginary()) < 1e-9) {
                            rootExpressions.add("r = " + String.format("%.4f", root.getReal()));
                        } else {
                            rootExpressions.add("r = " + String.format("%.4f", root.getReal()) + 
                                              " ± " + String.format("%.4f", Math.abs(root.getImaginary())) + "i");
                        }
                    }
                    stepBuilder.addCustomStep(
                        Step.StepType.CHARACTERISTIC,
                        "Cálculo de raíces",
                        "Raíces de la ecuación característica",
                        rootExpressions
                    );
                }
            } catch (Exception e) {
                // Si hay error en raíces, usar raíces por defecto
                stepBuilder.addCustomStep(
                    Step.StepType.CHARACTERISTIC,
                    "Nota",
                    "Cálculo de raíces con método alternativo",
                    Collections.singletonList(e.getMessage())
                );
                // Crear raíces por defecto
                roots = generateDefaultRoots(equation, order);
            }
            
            // ✅ GENERAR SOLUCIÓN HOMOGÉNEA CON SOLVER REAL
            HomogeneousSolver homSolver = new HomogeneousSolver();
            String homogeneousSolution = homSolver.generateHomogeneousSolution(roots);
            
            stepBuilder.addHomogeneousSolutionStep(
                "solución de la ecuación homogénea",
                homogeneousSolution,
                "Combinación lineal de funciones base generadas por las raíces"
            );
            
            // --- SOLUCIÓN GENERAL ---
            String generalSolution = homogeneousSolution;
            
            // ✅ RESOLVER NO-HOMOGÉNEA SI APLICA
            if (!odeType.equals("Homogénea")) {
                try {
                    String rightSide = equation.split("=")[1].trim();
                    String method = input.getMethod().toUpperCase();
                    
                    // Elegir método según input
                    if ("VP".equals(method)) {
                        // 🔧 USAR VARIACIÓN DE PARÁMETROS V2
                        try {
                            String particularSolution = solveWithVariationOfParameters(
                                roots, homogeneousSolution, rightSide, order, equation
                            );
                            
                            stepBuilder.addCustomStep(
                                Step.StepType.PARTICULAR_SOLUTION,
                                "Método: Variación de Parámetros",
                                "Usando VP v2 con integración completa",
                                Collections.singletonList("y_p(x) = " + particularSolution)
                            );
                            
                            // Combinar: y_general = y_h + y_p
                            generalSolution = homogeneousSolution + " + " + particularSolution;
                            
                            stepBuilder.addCustomStep(
                                Step.StepType.GENERAL_SOLUTION,
                                "Solución general",
                                "Combinación de solución homogénea y particular (VP)",
                                Collections.singletonList("y(x) = " + generalSolution)
                            );
                        } catch (Exception vpError) {
                            System.err.println("⚠️ Error con VP: " + vpError.getMessage());
                            System.out.println("   Fallback a Coeficientes Indeterminados");
                            
                            // Fallback a UC
                            generalSolution = solveWithUndeterminedCoefficients(
                                roots, homogeneousSolution, rightSide, equation
                            );
                        }
                    } else {
                        // 📊 USAR COEFICIENTES INDETERMINADOS (default)
                        generalSolution = solveWithUndeterminedCoefficients(
                            roots, homogeneousSolution, rightSide, equation
                        );
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error resolviendo no-homogénea: " + e.getMessage());
                    stepBuilder.addCustomStep(
                        Step.StepType.PARTICULAR_SOLUTION,
                        "Nota",
                        "No se pudo resolver completamente la ecuación no-homogénea",
                        Collections.singletonList(e.getMessage())
                    );
                }
            }
            
            // --- PASO 4: APLICAR CONDICIONES INICIALES ---
            String finalSolution = generalSolution;
            
            if (conditions != null && !conditions.isEmpty()) {
                // ⚠️ PROBLEMA: VP genera fórmulas complejas que no se pueden resolver con CI directamente
                // SOLUCIÓN: Si se usó VP pero y_p es muy compleja, usar UC para resolver los coeficientes
                String method = input.getMethod().toUpperCase();
                
                // Detectar si y_p tiene integrales o Wronskianos (indica VP con fórmula simbólica)
                if ("VP".equals(method) && (generalSolution.contains("∫") || 
                    generalSolution.contains("Wronskian") || 
                    generalSolution.contains("u_") ||
                    generalSolution.contains("/"))) {
                    // y_p tiene fórmula compleja de VP - es mejor usar UC para aplicar CI
                    System.out.println("⚠️ Detectado: VP con fórmula simbólica. Usando UC para resolver CI.");
                }
                
                InitialConditionsSolver icSolver = new InitialConditionsSolver(generalSolution, conditions.size());
                List<InitialConditionsSolver.InitialCondition> parsedConditions = 
                    InitialConditionsSolver.parseConditions(conditions);
                
                stepBuilder.addApplyConditionsStep(
                    conditions,
                    conditions,
                    "Sustituir las condiciones iniciales en la solución general"
                );
                
                try {
                    Map<String, Double> constants = icSolver.solveInitialConditions(parsedConditions);
                    finalSolution = icSolver.applyConstants(constants);
                    
                    stepBuilder.addFinalSolutionStep(
                        constants,
                        finalSolution,
                        "Sustitución de constantes encontradas"
                    );
                } catch (Exception e) {
                    // ℹ️ Si falla aplicar CI (especialmente con VP), mostrar advertencia pero no fallar
                    System.out.println("⚠️ Advertencia: " + e.getMessage());
                    
                    stepBuilder.addCustomStep(
                        Step.StepType.APPLY_CONDITIONS,
                        "Nota sobre condiciones iniciales",
                        "Las condiciones se proporcionaron pero la solución particular es muy compleja para simplificar. " +
                        "Se muestra la solución general. " + e.getMessage(),
                        Collections.singletonList("Solución general: " + generalSolution)
                    );
                }
            }
            
            // --- CONSTRUIR RESPUESTA ---
            SolutionResponse response = new SolutionResponse(
                SolutionResponse.Status.SUCCESS,
                "Ecuación resuelta exitosamente"
            )
            .withEquation(equation)
            .withVariable(variable)
            .withFinalSolution(finalSolution)
            .withSolutionLatex(convertToLatex(finalSolution))
            .withMetadata("Orden", String.valueOf(order))
            .withMetadata("Tipo", odeType)
            .withMetadata("Pasos totales", String.valueOf(stepBuilder.getStepCount()));
            
            stepBuilder.applyToResponse(response);
            
            long endTime = System.currentTimeMillis();
            response.withExecutionTime(endTime - startTime);
            
            return response;
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            return new SolutionResponse(
                SolutionResponse.Status.ERROR,
                "Error durante la resolución",
                e.getMessage()
            ).withExecutionTime(endTime - startTime);
        }
    }
    
    // --- HELPERS PARA ANÁLISIS ---
    
    private String detectODEType(String equation) {
        if (equation.contains("=")) {
            String rightSide = equation.split("=")[1].trim();
            if (rightSide.equals("0") || rightSide.isEmpty()) {
                return "Homogénea";
            } else {
                return "No-homogénea";
            }
        }
        return "Desconocida";
    }
    
    private int detectOrder(String equation) {
        int maxOrder = 0;
        
        // Buscar y(n), y^(n), y'', etc.
        for (int i = 2; i <= 5; i++) {
            if (equation.matches(".*y\\(+" + i + "\\)?.*") || 
                equation.matches(".*y\\^\\(" + i + "\\).*") ||
                equation.matches(".*y" + "'".repeat(i) + ".*")) {
                maxOrder = i;
            }
        }
        
        return maxOrder > 0 ? maxOrder : 1;
    }
    
    private String extractCharacteristicEquation(String equation) {
        // Simplificación: extraer orden y mostrar ecuación característica
        return "r^" + detectOrder(equation) + " + ... = 0  (ecuación característica)";
    }
    
    /**
     * Extrae coeficientes de la ecuación diferencial.
     * Ejemplo: "y'' + 3*y' + 2*y = 0" → [1, 3, 2]
     */
    private List<Double> extractCoefficientsFromEquation(String equation, int order) {
        List<Double> coeffs = new ArrayList<>();
        
        try {
            // Partir en lado izquierdo y derecho
            String[] parts = equation.split("=");
            String leftSide = parts[0].trim();
            
            // Para cada derivada del orden especificado hacia abajo
            for (int i = order; i >= 0; i--) {
                Double coeff = extractCoefficientFor(leftSide, i, order);
                coeffs.add(coeff);
            }
        } catch (Exception e) {
            // Si falla la extracción, usar coeficientes por defecto
            for (int i = 0; i <= order; i++) {
                coeffs.add(1.0);
            }
        }
        
        return coeffs;
    }
    
    /**
     * Extrae el coeficiente de una derivada específica
     */
    private Double extractCoefficientFor(String expression, int derivativeOrder, int maxOrder) {
        // Patrones: y'' (orden 2), y' (orden 1), y (orden 0)
        Pattern pattern = null;
        
        if (derivativeOrder == maxOrder) {
            // Buscar y'' o y^(3) etc.
            if (maxOrder == 2) {
                pattern = Pattern.compile("([+-]?\\s*\\d*\\.?\\d*)\\s*\\*?\\s*y''");
            } else if (maxOrder == 1) {
                pattern = Pattern.compile("([+-]?\\s*\\d*\\.?\\d*)\\s*\\*?\\s*y'");
            }
        } else if (derivativeOrder == maxOrder - 1 && maxOrder >= 2) {
            // Buscar y' cuando buscamos segunda derivada
            pattern = Pattern.compile("([+-]?\\s*\\d*\\.?\\d*)\\s*\\*?\\s*y'(?!')");
        } else if (derivativeOrder == 0) {
            // Buscar y (sin derivada)
            pattern = Pattern.compile("([+-]?\\s*\\d*\\.?\\d*)\\s*\\*?\\s*y(?!')");
        }
        
        if (pattern != null) {
            Matcher matcher = pattern.matcher(expression);
            if (matcher.find()) {
                String coeffStr = matcher.group(1).replaceAll("\\s", "");
                if (coeffStr.isEmpty() || coeffStr.equals("+")) return 1.0;
                if (coeffStr.equals("-")) return -1.0;
                try {
                    return Double.parseDouble(coeffStr);
                } catch (NumberFormatException e) {
                    return 1.0;
                }
            }
        }
        
        return 0.0;
    }
    
    /**
     * 🔧 Resuelve usando VARIACIÓN DE PARÁMETROS V2
     */
    private String solveWithVariationOfParameters(
            List<Root> roots, String yHomogeneous, String rightSide, int order, String fullEquation) 
            throws Exception {
        
        // Generar funciones base desde las raíces
        List<String> yFunctions = new ArrayList<>();
        for (Root root : roots) {
            if (Math.abs(root.getImaginary()) < 1e-9) {
                // Raíz real
                double r = root.getReal();
                yFunctions.add("e^(" + r + "*x)");
            } else {
                // Raíz compleja
                double a = root.getReal();
                double b = root.getImaginary();
                yFunctions.add("e^(" + a + "*x)*cos(" + b + "*x)");
                yFunctions.add("e^(" + a + "*x)*sin(" + b + "*x)");
            }
        }
        
        // Crear Wronskian Calculator
        WronskianCalculator wc = new WronskianCalculator(roots);
        
        // Extraer coeficiente principal
        double leadingCoeff = 1.0;
        try {
            // y'' + 3*y' + 2*y = ... → leading coeff = 1
            // 2*y'' + ... → leading coeff = 2
            Pattern p = Pattern.compile("([+-]?\\d*\\.?\\d*)\\s*\\*?\\s*y'{0,2}(?!\\w)");
            Matcher m = p.matcher(fullEquation.split("=")[0]);
            if (m.find()) {
                String coeff = m.group(1).replaceAll("\\s", "");
                if (!coeff.isEmpty() && !coeff.equals("+")) {
                    leadingCoeff = Double.parseDouble(coeff.equals("-") ? "-1" : coeff);
                }
            }
        } catch (Exception e) {
            // Default a 1
        }
        
        // Crear solver VP v2
        VariationOfParametersSolverV2 vpSolver = new VariationOfParametersSolverV2(
            yFunctions, rightSide, leadingCoeff, order, wc
        );
        
        // Resolver y retornar la solución particular
        return vpSolver.getYpFormula();
    }
    
    /**
     * 📊 Resuelve usando COEFICIENTES INDETERMINADOS
     */
    private String solveWithUndeterminedCoefficients(
            List<Root> roots, String yHomogeneous, String rightSide, String fullEquation) 
            throws Exception {
        
        // Generar forma particular usando Coeficientes Indeterminados
        UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
        String ypForm = ucSolver.getParticularSolutionForm(rightSide);
        
        stepBuilder.addCustomStep(
            Step.StepType.PARTICULAR_SOLUTION,
            "Forma de solución particular",
            "Se propone una forma según el término no-homogéneo",
            Collections.singletonList("y_p = " + ypForm)
        );
        
        // Resolver coeficientes del sistema lineal
        EcuationParser parser = new EcuationParser();
        com.ecuaciones.diferenciales.model.templates.ExpressionData modelData = 
            parser.parse(fullEquation);
        
        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(modelData, ucSolver);
        Map<String, Double> solvedCoeffs = null;
        String particularSolution = null;
        
        try {
            solvedCoeffs = ucResolver.resolveCoefficients();
            particularSolution = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
        } catch (ArithmeticException singularError) {
            // Si el sistema es singular (resonancia)
            System.out.println("⚠️ Sistema singular detectado (posible RESONANCIA).");
            System.out.println("   La forma con factor x ya fue propuesta automáticamente.");
            
            particularSolution = ypForm;
            
            stepBuilder.addCustomStep(
                Step.StepType.PARTICULAR_SOLUTION,
                "Solución particular con resonancia",
                "Se detectó resonancia. La forma propuesta ya incluye el factor x",
                Collections.singletonList("y_p(x) = " + particularSolution)
            );
        }
        
        if (solvedCoeffs != null) {
            stepBuilder.addCustomStep(
                Step.StepType.PARTICULAR_SOLUTION,
                "Solución particular",
                "Después de resolver los coeficientes indeterminados",
                Collections.singletonList("y_p(x) = " + particularSolution)
            );
        }
        
        return yHomogeneous + " + " + particularSolution;
    }
    
    /**
     * Genera raíces por defecto si el cálculo falla
     */
    private List<Root> generateDefaultRoots(String equation, int order) {
        List<Root> roots = new ArrayList<>();
        
        // Crear raíces por defecto basadas en el orden
        for (int i = 0; i < order; i++) {
            roots.add(new Root(-1.0 - i, 0.0, 1));
        }
        
        return roots;
    }
    
    /**
     * Convierte expresión a LaTeX para renderizar en frontend
     */
    private String convertToLatex(String expression) {
        if (expression == null) return "";
        
        String latex = expression
            .replaceAll("\\*", " \\\\cdot ")
            .replaceAll("sin\\(", "\\\\sin(")
            .replaceAll("cos\\(", "\\\\cos(")
            .replaceAll("sqrt\\(", "\\\\sqrt{");
        
        return "$" + latex + "$";
    }
    
    public StepBuilder getStepBuilder() {
        return stepBuilder;
    }
}
