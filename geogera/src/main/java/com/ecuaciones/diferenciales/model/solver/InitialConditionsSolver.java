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
     * CORREGIDO: Se añadieron ')' faltantes en patrones 1 y 4
     * AÑADIDO: (?i) para insensibilidad a mayúsculas (e ó E)
     * Convierte:
     * - e^(3x) → e^(3*x)
     * - e^(-x) → e^(-1*x)
     * - e^(-2x) → e^(-2*x)
     * - e^(x) → e^(1*x)
     * También maneja "* " con espacios
     */
/**
     * Normaliza exponenciales para compatibilidad con Symja
     * CORREGIDO (¡DE VERDAD!): Se añadieron ')' faltantes en patrones 1 y 4
     * AÑADIDO: (?i) para insensibilidad a mayúsculas (e ó E)
     */
    private String normalizeExponentials(String expr) {
        
        // Patrón 1: e^(3x) → e^(3*x) 
        // (?i) ignora mayúsculas/minúsculas (e ó E)
        expr = expr.replaceAll("(?i)e\\^\\((\\d+)\\s*([a-zA-Z])\\)", "e^($1*$2)");
        
        // Patrón 2: e^(x) → e^(1*x)
        expr = expr.replaceAll("(?i)e\\^\\(([a-zA-Z])\\)(?!\\*)", "e^(1*$1)");
        
        // Patrón 3: e^(-x) → e^(-1*x)
        expr = expr.replaceAll("(?i)e\\^\\(-\\s*([a-zA-Z])\\)", "e^(-1*$1)");
        
        // Patrón 4: e^(-2x) → e^(-2*x)
        expr = expr.replaceAll("(?i)e\\^\\(-\\s*(\\d+)\\s*([a-zA-Z])\\)", "e^(-$1*$2)");
        
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
     * IMPORTANTE: Si la solución general contiene un término constante (solución particular),
     * este se resta de las condiciones iniciales antes de resolver.
     * 
     * Ejemplo: y(x) = C1*e^(-2*x) + 2 con y(0)=1
     *  -> Funciones base: [e^(-2*x)]
     *  -> Término constante: +2
     *  -> Sistema ajustado: C1*e^(0) = 1 - 2 = -1 → C1 = -1
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

        // --- PASO 1: Extraer funciones base y término constante de la solución general ---
        List<String> baseFunctions = extractBaseFunctions();
        double constantTerm = extractConstantTerm();  // Nuevo método
        
        if (baseFunctions.size() != order) {
            throw new IllegalArgumentException(
                "No se pudieron extraer " + order + " funciones base de la solución general. " +
                "Se extrajeron " + baseFunctions.size() + "."
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

                // Evaluar la derivada en x0 usando derivación simbólica
                double value;
                if (ic.derivativeOrder == 0) {
                    // Función sin derivar
                    value = evaluateAt(baseFunc, ic.x0);
                } else {
                    // Usar derivada simbólica
                    value = evaluateDerivativeAtSymbolic(baseFunc, ic.x0, ic.derivativeOrder);
                }
                row.add(value);
            }
            matrixA.add(row);
        }

        // --- PASO 3: Construir vector b (valores de las CI ajustados) ---
        List<Double> vectorB = new ArrayList<>();
        for (int i = 0; i < order; i++) {
            InitialCondition ic = conditions.get(i);
            double adjustedValue = ic.value;
            
            // Si la derivada es 0 (función sin derivar) y hay término constante,
            // restamos el término constante de la CI
            if (ic.derivativeOrder == 0 && Math.abs(constantTerm) > 1e-12) {
                adjustedValue = ic.value - constantTerm;
            }
            
            vectorB.add(adjustedValue);
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
     * Maneja correctamente paréntesis múltiples y términos con constantes.
     * 
     * ESTRATEGIA MEJORADA (v2):
     * 1. Elimina TODOS los paréntesis redundantes (incluso anidados múltiples)
     * 2. Divide por + y - respetando paréntesis  
     * 3. Extrae funciones con C# y constantes por separado
     * 
     * IMPORTANTE: Para ecuaciones no-homogéneas con y_p constante:
     * - NO agrega el término constante como función base
     * - El término constante se "absorbe" en las condiciones iniciales
     * - Ej: y(x) = (C1*e^(-2x)) + (2) → funciones base = [e^(-2*x)]
     */
    private List<String> extractBaseFunctions() {
        List<String> functions = new ArrayList<>();

        // PASO 1: Normalizar (quitar espacios)
        String normalized = generalSolution.replaceAll("\\s+", "");
        
        // PASO 1.5: Limpiar paréntesis innecesarios de CADA término
        // Primero, dividir en términos para limpiar cada uno por separado
        List<String> dirtyTerms = splitByAdditionRespectingParentheses(normalized);
        List<String> cleanTerms = new ArrayList<>();
        
        for (String dirtyTerm : dirtyTerms) {
            String cleanedTerm = stripAllOuterParentheses(dirtyTerm);
            cleanTerms.add(cleanedTerm);
        }
        
        // Reconstruir expression sin paréntesis innecesarios
        normalized = String.join("+", cleanTerms);
        
        // PASO 2: Asegurar que comience con signo
        if (!normalized.matches("^[+\\-].*")) {
            normalized = "+" + normalized;
        }

        // PASO 3: Re-dividir por + y - (respetando paréntesis internos)
        List<String> terms = splitByAdditionRespectingParentheses(normalized);

        // PASO 4: Procesar cada término
        for (String term : terms) {
            if (term.isEmpty() || term.matches("^[+\\-]$")) continue;
            
            // Extraer signo
            String cleanTerm = term;
            if (!cleanTerm.matches("^[+\\-].*")) {
                cleanTerm = "+" + cleanTerm;
            }
            
            // Intentar extraer función con C# (Ej: +C1*cos(3x))
            Pattern cPattern = Pattern.compile("^([+\\-])(C\\d+)(?:\\*)?(.*)$");
            Matcher cMatcher = cPattern.matcher(cleanTerm);

            if (cMatcher.matches()) {
                String sign = cMatcher.group(1);
                String funcPart = cMatcher.group(3);

                if (funcPart.isEmpty()) {
                    funcPart = "1";
                }

                if ("-".equals(sign)) {
                    funcPart = "-(" + funcPart + ")";
                }

                functions.add(funcPart);
            } else {
                // Intentar extraer término constante puro (Ej: +2 o -3.5)
                // Si es término constante, lo ignoramos como función base
                // Se manejará mediante extractConstantTerm()
            }
        }
        
        return functions;
    }
    
    /**
     * Elimina TODOS los paréntesis redundantes externos, incluso múltiples niveles.
     * Ejemplo: (((...))) → ... (repite hasta que no hay más)
     */
    private String stripAllOuterParentheses(String expr) {
        boolean changed = true;
        while (changed) {
            changed = false;
            if (expr.startsWith("(") && expr.endsWith(")")) {
                // Verificar si el paréntesis inicial envuelve todo
                int depth = 0;
                boolean wrapsAll = true;
                for (int i = 0; i < expr.length(); i++) {
                    if (expr.charAt(i) == '(') depth++;
                    else if (expr.charAt(i) == ')') depth--;
                    
                    if (depth == 0 && i < expr.length() - 1) {
                        wrapsAll = false;
                        break;
                    }
                }
                
                if (wrapsAll) {
                    expr = expr.substring(1, expr.length() - 1);
                    changed = true;  // Repetir en caso de más niveles
                }
            }
        }
        return expr;
    }
    
    /**
     * Divide una expresión por + y - respetando paréntesis internos.
     */
    private List<String> splitByAdditionRespectingParentheses(String expr) {
        List<String> terms = new ArrayList<>();
        StringBuilder currentTerm = new StringBuilder();
        int parenDepth = 0;
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            
            if (c == '(') {
                parenDepth++;
                currentTerm.append(c);
            } else if (c == ')') {
                parenDepth--;
                currentTerm.append(c);
            } else if ((c == '+' || c == '-') && parenDepth == 0) {
                if (currentTerm.length() > 0) {
                    terms.add(currentTerm.toString());
                }
                currentTerm = new StringBuilder();
                currentTerm.append(c);
            } else {
                currentTerm.append(c);
            }
        }
        
        if (currentTerm.length() > 0) {
            terms.add(currentTerm.toString());
        }
        
        return terms;
    }
    
    /**
     * Extrae el término constante de la solución general (solución particular constante).
     * Si la solución es "y(x) = (C1*e^(-2x)) + (2)", devuelve 2.0
     * 
     * @return Valor del término constante, o 0 si no existe
     */
    private double extractConstantTerm() {
        String normalized = generalSolution.replaceAll("\\s+", "");
        
        // Limpiar paréntesis redundantes externos
        while (normalized.startsWith("(") && normalized.endsWith(")")) {
            int depth = 0;
            boolean isWrapped = true;
            for (int i = 0; i < normalized.length(); i++) {
                if (normalized.charAt(i) == '(') depth++;
                else if (normalized.charAt(i) == ')') depth--;
                
                if (depth == 0 && i < normalized.length() - 1) {
                    isWrapped = false;
                    break;
                }
            }
            
            if (isWrapped) {
                normalized = normalized.substring(1, normalized.length() - 1);
            } else {
                break;
            }
        }
        
        // Asegurar que comience con signo
        if (!normalized.matches("^[+\\-].*")) {
            normalized = "+" + normalized;
        }

        // Dividir términos
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
                if (currentTerm.length() > 0) {
                    terms.add(currentTerm.toString());
                }
                currentTerm = new StringBuilder();
                currentTerm.append(c);
            } else {
                currentTerm.append(c);
            }
        }
        
        if (currentTerm.length() > 0) {
            terms.add(currentTerm.toString());
        }

        // Buscar término constante
        for (String term : terms) {
            if (term.isEmpty() || term.matches("^[+\\-]$")) continue;
            
            String cleanTerm = term;
            
            // Limpiar paréntesis
            while (cleanTerm.matches("^[+\\-]\\(.*\\)$")) {
                char sign = cleanTerm.charAt(0);
                String inner = cleanTerm.substring(1);
                
                int depth = 0;
                boolean wrapsAll = true;
                for (int i = 0; i < inner.length(); i++) {
                    if (inner.charAt(i) == '(') depth++;
                    else if (inner.charAt(i) == ')') depth--;
                    
                    if (depth == 0 && i < inner.length() - 1) {
                        wrapsAll = false;
                        break;
                    }
                }
                
                if (wrapsAll && inner.startsWith("(")) {
                    cleanTerm = sign + inner.substring(1, inner.length() - 1);
                } else {
                    break;
                }
            }
            
            // Detectar si es un término constante (sin C#)
            Pattern constPattern = Pattern.compile("^([+\\-])(\\d+(?:\\.\\d+)?)$");
            Matcher constMatcher = constPattern.matcher(cleanTerm);
            
            if (constMatcher.matches()) {
                String sign = constMatcher.group(1);
                double value = Double.parseDouble(constMatcher.group(2));
                
                if ("-".equals(sign)) {
                    value = -value;
                }
                
                return value;
            }
        }
        
        return 0.0;
    }
    
    /**
     * Evalúa una expresión en un punto específico
     * Ejemplo: evaluateAt("cos(2*x)", 0.0) → 1.0
     * 
     * NOTA: La expresión ya debe estar normalizada (normalizeExponentials se llama en el constructor)
     */
    private double evaluateAt(String expr, double x0) {
        // Usa SymjaEngine.evaluateNumerical que maneja la conversión a sintaxis Symja
        return SymjaEngine.evaluateNumerical(expr, x0);
    }
    
    /**
     * Calcula y evalúa la derivada de una expresión en un punto específico
     * ¡USA CÁLCULO SIMBÓLICO REAL con Symja!
     * 
     * @param expr la expresión (ej: "e^(2*x)")
     * @param x0 el punto de evaluación (ej: 0.0)
     * @param order el orden de la derivada (1, 2, 3, etc.)
     * @return el valor numérico de la derivada en x0
     */
    private double evaluateDerivativeAtSymbolic(String expr, double x0, int order) {
        // Si el orden es 0, solo evaluamos la función original
        if (order == 0) {
            return evaluateAt(expr, x0);
        }

        // --- Derivación Simbólica ---
        String derivativeExpression;
        try {
            // Usar SymjaEngine para obtener la derivada simbólica
            derivativeExpression = SymjaEngine.getSymbolicDerivative(expr, "x", order);

        } catch (Exception e) {
            System.err.println("[InitialConditionsSolver] Error al calcular derivada simbólica: " + e.getMessage());
            return Double.NaN; // Retornar NaN en caso de fallo
        }
        
        // --- Evaluación Numérica ---
        // Ahora, evaluamos la expresión de la derivada en el punto x0
        return evaluateAt(derivativeExpression, x0);
    }

    /**
     * Aplica las constantes resueltas a la solución general usando Symja
     * 
     * De: "C1*e^x + C2*cos(x)"
     * Con {C1=1, C2=2}
     * A: "e^x + 2*cos(x)"
     * 
     * MEJORADO (v5): Usa SymjaEngine.substituteMultipleConstants() 
     * que realiza TODAS las sustituciones en UN SOLO comando Symja
     */
    public String applyConstants(Map<String, Double> constants) {
        String solution = generalSolution;

        try {
            // --- Estrategia Principal: Usar Symja con sustitución múltiple segura ---
            solution = SymjaEngine.substituteMultipleConstants(solution, constants);
            
        } catch (Exception e) {
            // Si Symja falla completamente, usar fallback
            System.err.println("[InitialConditionsSolver] Symja falló, usando fallback: " + e.getMessage());
            solution = applyConstantsSimpleFallback(constants);
        }
        
        // --- Limpieza y formateo final ---
        
        // 1. Convertir E^ a e^ (notación estándar, pero preservar E^(...) )
        // Symja usa E^x para exponenciales, convertir a e^(x)
        solution = solution.replaceAll("(?i)E\\^\\(([^)]*)\\)", "e^($1)");
        solution = solution.replaceAll("(?i)E\\^([a-z])", "e^($1)");
        // Convertir 1/E^x a e^(-x) para mejor legibilidad
        solution = solution.replaceAll("1/E\\^\\(([^)]*)\\)", "e^(-($1))");
        solution = solution.replaceAll("(?i)1/E\\^([a-z])", "e^(-$1)");
        
        // 2. Convertir valores decimales muy cercanos a enteros
        solution = cleanNearIntegerValues(solution);
        
        // 3. Convertir notación de funciones Symja a nuestra notación
        solution = solution.replaceAll("(?i)Sin\\[", "sin(");
        solution = solution.replaceAll("(?i)Cos\\[", "cos(");
        solution = solution.replaceAll("(?i)Tan\\[", "tan(");
        solution = solution.replaceAll("(?i)\\bSin\\b", "sin");
        solution = solution.replaceAll("(?i)\\bCos\\b", "cos");
        solution = solution.replaceAll("(?i)\\bTan\\b", "tan");
        
        // 4. Expandir notación exponencial si es que hay
        solution = solution.replaceAll("(?i)Exp\\[", "e^(");
        solution = solution.replaceAll("\\]", ")");
        
        // 5. Limpiar paréntesis redundantes
        solution = cleanRedundantParentheses(solution);

        return solution;
    }
    
    /**
     * Fallback: Sustitución simple sin Symja
     * Se usa cuando Symja falla o retorna algo inválido
     */
    private String applyConstantsSimpleFallback(Map<String, Double> constants) {
        String solution = generalSolution;
        
        for (Map.Entry<String, Double> entry : constants.entrySet()) {
            String constName = entry.getKey();
            double value = entry.getValue();
            
            // Redondear valores muy cercanos a enteros
            double roundedValue = Math.round(value * 1e10) / 1e10;
            
            // Formato del valor numérico
            String formattedValue = formatNumericValue(roundedValue);
            
            // Buscar C# como palabra completa
            String regex = "\\b" + Pattern.quote(constName) + "\\b";
            
            if (roundedValue < 0) {
                // Si es negativo, envolver en paréntesis
                solution = solution.replaceAll(regex, "(" + formattedValue + ")");
            } else {
                solution = solution.replaceAll(regex, formattedValue);
            }
        }
        
        return solution;
    }
    
    /**
     * Formatea un valor numérico de manera legible
     */
    private String formatNumericValue(double value) {
        // Redondear a entero si está muy cerca
        if (Math.abs(value - Math.round(value)) < 1e-10) {
            return String.valueOf((long) Math.round(value));
        }
        
        // Para decimales, usar máximo 6 decimales y remover ceros finales
        String formatted = String.format(Locale.US, "%.6f", value);
        formatted = formatted.replaceAll("0+$", "").replaceAll("\\.$", "");
        return formatted;
    }
    
    /**
     * Elimina paréntesis redundantes alrededor de números enteros
     * Por ejemplo: (3)*sin(x) → 3*sin(x) o sin((x)) → sin(x)
     */
    private String cleanRedundantParentheses(String expr) {
        // Patrón 1: (número_entero) → número_entero (excepto negativo)
        expr = expr.replaceAll("\\((\\d+)\\)", "$1");
        
        // Patrón 2: (número_decimal) cuando está entre operadores
        expr = expr.replaceAll("\\((\\d+\\.\\d+)\\)([+\\-*/)\\]]|$)", "$1$2");
        
        return expr;
    }
    
    /**
     * Convierte decimales muy cercanos a enteros en su representación entera
     * Ejemplo: 2.9999999999999996 → 3, -0.9999999999999996 → -1
     */
    private String cleanNearIntegerValues(String expr) {
        // Encontrar todos los números decimales
        Pattern numberPattern = Pattern.compile("(-?\\d+\\.\\d+)");
        Matcher matcher = numberPattern.matcher(expr);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            double num = Double.parseDouble(matcher.group(1));
            double rounded = Math.round(num);
            
            // Si el número está muy cercano a su redondeado (dentro de 1e-8)
            if (Math.abs(num - rounded) < 1e-8) {
                // Reemplazar por el entero
                long intValue = Math.round(num);
                matcher.appendReplacement(sb, String.valueOf(intValue));
            } else {
                // Mantener el original pero con menos decimales (máximo 4)
                String formatted = String.format(Locale.US, "%.4f", num);
                // Remover ceros al final
                formatted = formatted.replaceAll("0+$", "").replaceAll("\\.$", "");
                matcher.appendReplacement(sb, formatted);
            }
        }
        matcher.appendTail(sb);
        
        return sb.toString();
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
