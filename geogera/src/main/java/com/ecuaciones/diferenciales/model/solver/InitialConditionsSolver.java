package com.ecuaciones.diferenciales.model.solver;

import com.ecuaciones.diferenciales.utils.LinearSystemSolver;
import com.ecuaciones.diferenciales.utils.SymjaEngine;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

/**
 * CONDICIONES INICIALES SOLVER (CI)
 * 
 * Resuelve un sistema de n ecuaciones lineales con n constantes arbitrarias
 * a partir de n condiciones iniciales generalizadas.
 * 
 * Completamente basado en Symja/Matheclipse para:
 * - Evaluación de expresiones
 * - Cálculo de derivadas
 * - Simplificación algebraica
 * - Sustitución de variables
 * 
 * Soporta:
 * - Condiciones homogéneas: y(0)=1, y'(0)=0, y''(0)=2, ...
 * - Condiciones en cualquier punto: y(1)=5, y'(2)=-3, ...
 * - Orden de la EDO arbitrario (orden ≥ 1)
 */
public class InitialConditionsSolver {

    private static final double TOLERANCE = 1e-10;
    private static final DecimalFormat DF = new DecimalFormat("#.####", new java.text.DecimalFormatSymbols(Locale.US));

    private final String generalSolution; // Solución general: y(x) = ... con C1, C2, ..., Cn
    private final List<String> constantNames; // [C1, C2, ..., Cn]
    private final int order; // Número de condiciones iniciales (= orden EDO)

    /**
     * Constructor
     * 
     * @param generalSolution Solución general con constantes C1, C2, ..., Cn
     * @param order Número de constantes (= número de CI)
     */
    public InitialConditionsSolver(String generalSolution, int order) {
        // Normalizar formato exponencial: e^(-x) → e^(-1*x) si es necesario
        String normalized = normalizeExponentials(generalSolution);
        
        this.generalSolution = normalized;
        this.order = order;
        this.constantNames = new ArrayList<>();
        
        // Extraer nombres de constantes de la solución
        for (int i = 1; i <= order; i++) {
            this.constantNames.add("C" + i);
        }
    }

    /**
     * Normaliza exponenciales negativas para mejor compatibilidad
     */
    private String normalizeExponentials(String expr) {
        // e^(-x) → e^(-1*x)
        expr = expr.replaceAll("e\\^\\(-([a-zA-Z])", "e^(-1*$1");
        // e^(-2x) → e^(-2*x)
        expr = expr.replaceAll("e\\^\\(-(\\d+)([a-zA-Z])", "e^(-$1*$2");
        return expr;
    }

    /**
     * Clase interna para representar una condición inicial
     * Formato: "y'(x0) = valor" o "y(x0) = valor" o "y''(x0) = valor"
     */
    public static class InitialCondition {
        public int derivativeOrder; // 0 = y, 1 = y', 2 = y'', etc.
        public double x0; // Punto de evaluación
        public double value; // Valor esperado

        public InitialCondition(int derivativeOrder, double x0, double value) {
            this.derivativeOrder = derivativeOrder;
            this.x0 = x0;
            this.value = value;
        }

        @Override
        public String toString() {
            String func = "y";
            for (int i = 0; i < derivativeOrder; i++) func += "'";
            return func + "(" + x0 + ") = " + value;
        }
    }

    /**
     * Parsea una lista de condiciones iniciales en formato string.
     * 
     * Formatos aceptados:
     * - "y(0)=1"
     * - "y'(0)=2"
     * - "y''(1)=-3"
     * - "y'''(0.5)=4"
     * 
     * @param conditions Lista de strings con condiciones
     * @return Lista de objetos InitialCondition
     */
    public static List<InitialCondition> parseConditions(List<String> conditions) {
        List<InitialCondition> parsed = new ArrayList<>();
        
        // Patrón regex: y(primas)?(punto) = valor
        // Grupos: 1=primas, 2=punto, 3=valor
        Pattern pattern = Pattern.compile(
            "y('*)\\s*\\(\\s*([+\\-]?\\d*\\.?\\d+)\\s*\\)\\s*=\\s*([+\\-]?\\d*\\.?\\d+)",
            Pattern.CASE_INSENSITIVE
        );

        for (String cond : conditions) {
            String normalized = cond.trim().replaceAll("\\s+", "");
            Matcher matcher = pattern.matcher(normalized);

            if (matcher.find()) {
                String primas = matcher.group(1); // "" o "'" o "''" etc.
                double x0 = Double.parseDouble(matcher.group(2));
                double value = Double.parseDouble(matcher.group(3));

                int derivOrder = primas.length(); // 0 si "", 1 si "'", etc.

                parsed.add(new InitialCondition(derivOrder, x0, value));
            }
        }

        return parsed;
    }

    /**
     * Resuelve el sistema de condiciones iniciales usando Symja para todos los cálculos
     * 
     * @param conditions Lista de condiciones iniciales parseadas
     * @return Mapa {C1=val1, C2=val2, ..., Cn=valn}
     */
    public Map<String, Double> solveInitialConditions(List<InitialCondition> conditions) {
        if (conditions.size() != order) {
            throw new IllegalArgumentException(
                "Se esperan " + order + " condiciones iniciales, pero se proporcionaron " + conditions.size()
            );
        }

        // --- PASO 1: Extraer funciones base de la solución general ---
        List<String> baseFunctions = extractBaseFunctions();
        
        if (baseFunctions.size() != order) {
            throw new IllegalArgumentException(
                "No se pudieron extraer " + order + " funciones base de la solución general."
            );
        }

        // --- PASO 2: Construir matriz A (coeficientes del sistema) ---
        List<List<Double>> matrixA = new ArrayList<>();

        for (int i = 0; i < order; i++) {
            InitialCondition ic = conditions.get(i);
            List<Double> row = new ArrayList<>();

            // Fila i: evaluar la derivada ic.derivativeOrder de cada función base
            for (int j = 0; j < order; j++) {
                String baseFunc = baseFunctions.get(j);

                // Calcular derivada de orden ic.derivativeOrder usando Symja
                String derived = baseFunc;
                for (int k = 0; k < ic.derivativeOrder; k++) {
                    derived = calculateDerivative(derived);
                }

                // Evaluar en x0 usando Symja
                double value = evaluateAt(derived, ic.x0);
                row.add(value);
            }
            matrixA.add(row);
        }

        // --- PASO 3: Construir vector b (valores de las CI) ---
        List<Double> vectorB = new ArrayList<>();
        for (int i = 0; i < order; i++) {
            vectorB.add(conditions.get(i).value);
        }

        // --- PASO 4: Resolver el sistema A * C = b ---
        double[] solution = LinearSystemSolver.solve(matrixA, vectorB);

        if (solution == null) {
            throw new RuntimeException(
                "No se pudo resolver el sistema de condiciones iniciales. " +
                "La matriz puede ser singular o el sistema es inconsistente."
            );
        }

        // --- PASO 5: Mapear solución a nombres de constantes ---
        Map<String, Double> result = new LinkedHashMap<>();
        for (int i = 0; i < order; i++) {
            result.put("C" + (i + 1), solution[i]);
        }

        return result;
    }

    /**
     * Extrae las funciones base de la solución general.
     * 
     * Soporta múltiples formatos:
     * - "C1*cos(2x) + C2*sin(2x)"
     * - "C1 + C2*x + C3*e^x"
     * - "C1*e^x + C2*e^(2x)"
     */
    private List<String> extractBaseFunctions() {
        List<String> functions = new ArrayList<>();

        // Normalizar: quitar espacios
        String normalized = generalSolution.replaceAll("\\s+", "");
        
        // Asegurar que comience con signo
        if (!normalized.matches("^[+\\-].*")) {
            normalized = "+" + normalized;
        }

        // Dividir por + o - manteniendo el operador, pero SIN dividir dentro de paréntesis
        List<String> terms = new ArrayList<>();
        StringBuilder currentTerm = new StringBuilder();
        int parenDepth = 0;
        
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            
            if (c == '(') {
                parenDepth++;
                currentTerm.append(c);
            } else if (c == ')') {
                parenDepth--;
                currentTerm.append(c);
            } else if ((c == '+' || c == '-') && parenDepth == 0) {
                // Separador encontrado fuera de paréntesis
                if (currentTerm.length() > 0) {
                    terms.add(currentTerm.toString());
                }
                currentTerm = new StringBuilder();
                currentTerm.append(c);
            } else {
                currentTerm.append(c);
            }
        }
        
        // Agregar el último término
        if (currentTerm.length() > 0) {
            terms.add(currentTerm.toString());
        }

        for (String term : terms) {
            if (term.isEmpty()) continue;

            // Cada término tiene formato: (+/-)C#*(función) o (+/-)C#
            // Capturar signo, número, y lo que sigue
            Pattern pattern = Pattern.compile("^([+\\-])C(\\d+)(?:\\*)?(.*)$");
            Matcher matcher = pattern.matcher(term);

            if (matcher.matches()) {
                String sign = matcher.group(1);
                String funcPart = matcher.group(3);

                // Si funcPart está vacío, la función es 1 (constante)
                if (funcPart.isEmpty()) {
                    funcPart = "1";
                }

                // Aplicar signo negativo si aplica
                if ("-".equals(sign)) {
                    funcPart = "-(" + funcPart + ")";
                }

                functions.add(funcPart);
            }
        }

        return functions;
    }

    /**
     * Calcula la derivada de una expresión respecto a x usando SymjaEngine
     */
    private String calculateDerivative(String expr) {
        // Usa SymjaEngine.symbolicDerivativeSimplified para obtener derivada simplificada
        // Esto resuelve automáticamente Log(e) -> 1
        return SymjaEngine.symbolicDerivativeSimplified(expr, 1);
    }

    /**
     * Evalúa una expresión en un punto específico usando SymjaEngine
     * 
     * Ejemplo: evaluateAt("cos(2*x)", 0.0) → 1.0
     */
    private double evaluateAt(String expr, double x0) {
        // Usa SymjaEngine.evaluateNumerical que maneja todo
        return SymjaEngine.evaluateNumerical(expr, x0);
    }

    /**
     * Aplica las constantes resueltas a la solución general usando SymjaEngine
     * 
     * De: "C1*e^x + C2*cos(x)"
     * Con {C1=1, C2=2}
     * A: "e^x + 2*cos(x)"
     */
    public String applyConstants(Map<String, Double> constants) {
        String solution = generalSolution;

        // Reemplazar cada constante por su valor usando SymjaEngine
        for (Map.Entry<String, Double> entry : constants.entrySet()) {
            String constName = entry.getKey();
            double value = entry.getValue();
            
            // Usar SymjaEngine para sustitución y simplificación
            solution = SymjaEngine.applyConstantSubstitution(solution, constName, value);
        }

        // Simplificar la expresión final usando SymjaEngine
        solution = SymjaEngine.symbolicSimplify(solution);

        return solution;
    }

    /**
     * Formatea un valor numérico para visualización
     */
    private String formatValue(double value) {
        if (Math.abs(value) < TOLERANCE) return "0";
        if (Math.abs(value - Math.round(value)) < TOLERANCE) {
            return String.valueOf((long) Math.round(value));
        }
        return DF.format(value);
    }

    // --- GETTERS ---

    public String getGeneralSolution() {
        return generalSolution;
    }

    public List<String> getConstantNames() {
        return constantNames;
    }

    public int getOrder() {
        return order;
    }
}
