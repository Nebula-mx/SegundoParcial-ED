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
     * Normaliza exponenciales para compatibilidad con Symja
     * Convierte:
     * - e^(3x) → e^(3*x)
     * - e^(-x) → e^(-1*x)
     * - e^(-2x) → e^(-2*x)
     * - e^(x) → e^(1*x)
     * También maneja "* " con espacios
     */
    private String normalizeExponentials(String expr) {
        // Patrón 1: e^(número seguido de letra, sin *)
        // e^(3x) → e^(3*x)
        expr = expr.replaceAll("e\\^\\((\\d+)\\s*([a-zA-Z])", "e^($1*$2");
        
        // Patrón 2: e^(letra sola, sin multiplicador)
        // e^(x) → e^(1*x)
        expr = expr.replaceAll("e\\^\\(([a-zA-Z])\\)(?!\\*)", "e^(1*$1)");
        
        // Patrón 3: e^(-letra)
        // e^(-x) → e^(-1*x)
        expr = expr.replaceAll("e\\^\\(-\\s*([a-zA-Z])\\)", "e^(-1*$1");
        
        // Patrón 4: e^(-número seguido de letra)
        // e^(-2x) → e^(-2*x)
        expr = expr.replaceAll("e\\^\\(-\\s*(\\d+)\\s*([a-zA-Z])", "e^(-$1*$2");
        
        // Patrón 5: Eliminar espacios alrededor de *
        expr = expr.replaceAll("\\s*\\*\\s*", "*");
        
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

                // Evaluar la derivada en x0 usando diferencias numéricas
                double value;
                if (ic.derivativeOrder == 0) {
                    // Función sin derivar
                    value = evaluateAt(baseFunc, ic.x0);
                } else {
                    // Usar derivada numérica
                    value = evaluateDerivativeAtNumerical(baseFunc, ic.x0, ic.derivativeOrder);
                }
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
     * Calcula la derivada de una expresión respecto a x usando diferencias numéricas
     * Esto evita problemas de parsing con Symja
     */
    private String calculateDerivative(String expr) {
        // Como esto es comple jo y Symja tiene problemas, usamos APROXIMACIÓN NUMÉRICA
        // en lugar de derivada simbólica para evaluar la derivada en los puntos
        // El parámetro "expr" se usa luego en evaluateAtWithDerivative
        return expr;
    }

    /**
     * Evalúa una expresión en un punto específico usando diferencias numéricas
     * Si necesitamos la derivada, usamos (f(x+h) - f(x-h))/(2h)
     * 
     * Ejemplo: evaluateAt("cos(2*x)", 0.0) → 1.0
     */
    private double evaluateAt(String expr, double x0) {
        // Normalizar antes de evaluar
        String normalized = normalizeExponentials(expr);
        // Usa SymjaEngine.evaluateNumerical que maneja todo
        return SymjaEngine.evaluateNumerical(normalized, x0);
    }
    
    /**
     * Evalúa la derivada de una expresión en un punto usando diferencias finitas
     * @param order el orden de la derivada (1 para primera derivada, 2 para segunda, etc.)
     */
    private double evaluateDerivativeAtNumerical(String expr, double x0, int order) {
        final double h = 1e-6;
        
        // Normalizar
        String normalized = normalizeExponentials(expr);
        
        try {
            if (order == 0) {
                return evaluateAt(normalized, x0);
            } else if (order == 1) {
                // Primera derivada: (f(x+h) - f(x-h))/(2h)
                double f_plus = SymjaEngine.evaluateNumerical(normalized, x0 + h);
                double f_minus = SymjaEngine.evaluateNumerical(normalized, x0 - h);
                return (f_plus - f_minus) / (2 * h);
            } else if (order == 2) {
                // Segunda derivada: (f(x+h) - 2f(x) + f(x-h))/h²
                double f_plus = SymjaEngine.evaluateNumerical(normalized, x0 + h);
                double f_center = SymjaEngine.evaluateNumerical(normalized, x0);
                double f_minus = SymjaEngine.evaluateNumerical(normalized, x0 - h);
                return (f_plus - 2 * f_center + f_minus) / (h * h);
            } else {
                // Para órdenes mayores, aproximación recurrente (menos precisa pero funciona)
                double[] values = new double[order + 1];
                for (int i = 0; i <= order; i++) {
                    double xi = x0 + (i - order/2.0) * h;
                    values[i] = SymjaEngine.evaluateNumerical(normalized, xi);
                }
                // Coeficientes de diferencias finitas para orden alto
                double result = 0;
                for (int i = 0; i <= order; i++) {
                    result += values[i] * binomialCoeff(order, i) * (i % 2 == order % 2 ? 1 : -1);
                }
                return result / Math.pow(h, order);
            }
        } catch (Exception e) {
            System.err.println("  [Error] No se pudo calcular derivada numérica de '" + expr + "'");
            return 0.0;
        }
    }
    
    /**
     * Calcula el coeficiente binomial C(n, k)
     */
    private static double binomialCoeff(int n, int k) {
        if (k > n - k) k = n - k;
        double result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
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
