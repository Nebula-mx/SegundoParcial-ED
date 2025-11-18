package com.ecuaciones.diferenciales.utils;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🛑 SymjaEngine: Motor que encapsula todas las llamadas a matheclipse-core (Symja).
 * Se implementa con métodos estáticos para acceso directo sin inyección en constructores.
 */
public class SymjaEngine {

    private static final ExprEvaluator EVALUATOR = new ExprEvaluator();
    private static final ISymbol X_SYMBOL = F.x; // Variable independiente para D, Integrate

    // --- 1. Utilidad de Sintaxis (Conversión a Symja: sin(x) -> Sin[x]) ---

    /**
     * Convierte la salida de Symja de vuelta a nuestro formato estándar.
     * Symja retorna: E^x, Sin[x], Cos[x], Exp[x], etc.
     * Nosotros usamos: e^(x), sin(x), cos(x), etc.
     */
    public static String convertFromSymjaSyntax(String symjaOutput) {
        String result = symjaOutput;
        
        // Paso 1: Convertir funciones trigonométricas Symja a nuestro formato
        result = result.replaceAll("Sin\\[([^\\]]+)\\]", "sin($1)");
        result = result.replaceAll("Cos\\[([^\\]]+)\\]", "cos($1)");
        result = result.replaceAll("Tan\\[([^\\]]+)\\]", "tan($1)");
        
        // Paso 1b: Convertir funciones hiperbólicas Symja a nuestro formato
        result = result.replaceAll("Sinh\\[([^\\]]+)\\]", "sinh($1)");
        result = result.replaceAll("Cosh\\[([^\\]]+)\\]", "cosh($1)");
        result = result.replaceAll("Tanh\\[([^\\]]+)\\]", "tanh($1)");
        
        // Paso 2: Convertir exponenciales
        result = result.replaceAll("Exp\\[([^\\]]+)\\]", "e^($1)");
        result = result.replaceAll("Log\\[([^\\]]+)\\]", "ln($1)");
        result = result.replaceAll("Sqrt\\[([^\\]]+)\\]", "sqrt($1)");
        
        // Paso 3: Convertir E^(...) (salida Symja) a e^(...)
        result = result.replaceAll("E\\^\\(([^\\)]+)\\)", "e^($1)");
        
        // Paso 4: Convertir E^variable (como E^x, E^y) a e^(variable)
        // Usa word boundary para evitar capturar "Exp" o "Sinh", etc.
        result = result.replaceAll("E\\^([a-zA-Z](?:\\^[0-9]+)?)", "e^($1)");
        
        // Paso 5: Convertir E (número de Euler standalone) a e
        // Pero preservar E dentro de funciones ya convertidas
        result = result.replaceAll("\\bE\\b", "e");
        
        return result;
    }

    public static String convertToSymjaSyntax(String mathString) {
        String result = mathString;
        
        // PASO 0: Normalizar E^x (salida de Symja) a e^x (entrada)
        // E^(3*x) -> e^(3*x)
        result = result.replaceAll("(?i)E\\^\\(([^\\)]+)\\)", "e^($1)");
        // E^x -> e^(x)
        result = result.replaceAll("(?i)E\\^([a-z])", "e^($1)");
        
        // PASO 1: Reemplazar funciones trigonométricas (sin → Sin, cos → Cos, etc.)
        result = result.replaceAll("(?i)sin\\(", "Sin[");
        result = result.replaceAll("(?i)cos\\(", "Cos[");
        result = result.replaceAll("(?i)tan\\(", "Tan[");
        result = result.replaceAll("(?i)exp\\(", "Exp[");
        result = result.replaceAll("(?i)ln\\(", "Log[");
        result = result.replaceAll("(?i)log\\(", "Log[");
        result = result.replaceAll("(?i)sqrt\\(", "Sqrt[");
        
        // PASO 2: Cerrar brackets para funciones
        result = closeMatchingBrackets(result);
        
        // PASO 3: Convertir e^(...) a Exp[...]
        // Patrón: e^(algo) → Exp[algo]
        result = result.replaceAll("(?i)e\\^\\(([^\\)]+)\\)", "Exp[$1]");
        // Patrón: e^x → Exp[x]
        result = result.replaceAll("(?i)e\\^([a-z])", "Exp[$1]");
        
        // PASO 4: Otros reemplazos menores
        result = result.replaceAll("([0-9])x", "$1*x");
        result = result.replaceAll("([x\\])])\\(", "$1*");
        
        return result;
    }
    
    /**
     * Ayuda a cerrar brackets correctamente para funciones Symja
     * Después de reemplazar sin( -> Sin[, necesita cerrar con ]
     */
    private static String closeMatchingBrackets(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[') {
                // Encontramos un bracket abierto (de Sin[, Cos[, etc.)
                result.append(c);
                // Ahora buscar el ) correspondiente
                int depth = 1;
                int j = i + 1;
                while (j < str.length() && depth > 0) {
                    char next = str.charAt(j);
                    if (next == '(') {
                        depth++;
                    } else if (next == ')') {
                        depth--;
                        if (depth == 0) {
                            // Encontramos el ) que cierra este [
                            // No lo agregamos aún, lo saltamos y agregamos ]
                            i = j;
                            result.append(']');
                            break;
                        }
                    }
                    result.append(next);
                    j++;
                }
                // Si no encontramos ), simplemente cerramos el bracket
                if (depth > 0) {
                    result.append(']');
                    i = str.length(); // Para salir del loop exterior
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // --- 2. Derivación y Simplificación (Usado en CI y VdP) ---

    /**
     * Calcula la derivada simbólica de orden n.
     * Usa D[f, {x, n}] para derivadas de orden superior.
     * 
     * @param expression La expresión (ej: "x^2 + sin(x)")
     * @param variable La variable respecto a la cual derivar (ej: "x")
     * @param order El orden de la derivada (1, 2, 3...)
     * @return La expresión de la derivada como String
     */
    public static String getSymbolicDerivative(String expression, String variable, int order) {
        if (order == 0) return expression;
        
        String symjaSyntax = convertToSymjaSyntax(expression);
        try {
            // Construye el comando: D[expresión, {variable, orden}]
            String derivativeCommand = "D[" + symjaSyntax + ", {" + variable + ", " + order + "}]";
            IExpr result = EVALUATOR.eval(derivativeCommand);
            
            // Simplificar la derivada antes de devolverla
            String simplified = symbolicSimplify(result.toString());
            return simplified;
        } catch (Exception e) {
            System.err.println("Error Symja al calcular derivada de orden " + order + ": " + e.getMessage());
            return "d^" + order + "/d" + variable + "^" + order + "(" + expression + ")";
        }
    }

    /**
     * Calcula la derivada de una función respecto a x.
     * Solo usa D[f, x] (primera derivada) de una vez.
     * Para derivadas de orden superior, llamar múltiples veces.
     */
    public static String symbolicDerivative(String functionExpr, int order) {
        if (order == 0) return functionExpr;
        
        String symjaSyntax = convertToSymjaSyntax(functionExpr);
        try {
            // Usar notación string "D[f, x]" que Symja interpreta correctamente
            String derivativeCommand = "D[" + symjaSyntax + ", x]";
            IExpr result = EVALUATOR.eval(derivativeCommand);
            
            return result.toString();
        } catch (Exception e) {
            System.err.println("Error Symja derivada: " + e.getMessage());
            return "d/dx(" + functionExpr + ")";
        }
    }

    public static String symbolicSimplify(String expression) {
         try {
            String symjaSyntax = convertToSymjaSyntax(expression);
            String simplifyCommand = "Simplify[" + symjaSyntax + "]";
            IExpr result = EVALUATOR.eval(simplifyCommand);
            return result.toString();
        } catch (Exception e) {
            return expression;
        }
    }
    
    /**
     * Calcula la derivada simplificada (con Simplify aplicado).
     * Esto resuelve el problema de Log(e) -> 1 automáticamente.
     */
    public static String symbolicDerivativeSimplified(String functionExpr, int order) {
        if (order == 0) return functionExpr;
        
        String symjaSyntax = convertToSymjaSyntax(functionExpr);
        try {
            String derivativeCommand = "Simplify[D[" + symjaSyntax + ", x]]";
            IExpr result = EVALUATOR.eval(derivativeCommand);
            return result.toString();
        } catch (Exception e) {
            System.err.println("Error Symja derivada simplificada: " + e.getMessage());
            return symbolicDerivative(functionExpr, order);
        }
    }
    
    /**
     * Evalúa una expresión en un valor numérico.
     * Convierte automáticamente a sintaxis Symja y luego evalúa numéricamente.
     * Resultado: double (valor numérico puro, sin símbolos).
     */
    public static double evaluateNumerical(String expression, double xValue) {
        String symjaSyntax = convertToSymjaSyntax(expression);
        try {
            // Paso 1: Sustituir x por el valor
            String substitutionCommand = "(" + symjaSyntax + ") /. x -> " + xValue;
            IExpr substituted = EVALUATOR.eval(substitutionCommand);
            
            // Paso 2: Convertir a decimal numérico con N[]
            String numericCommand = "N[" + substituted.toString() + "]";
            IExpr numeric = EVALUATOR.eval(numericCommand);
            
            // Paso 3: Usar evalDouble() de Symja (maneja fracciones como 1/2, raíces, etc.)
            return numeric.evalDouble();
        } catch (NumberFormatException nfe) {
            System.err.println("  [Error numérico] No se pudo evaluar '" + expression + "' en x=" + xValue);
            System.err.println("  Resultado Symja: " + symjaSyntax);
            return 0.0;
        } catch (Exception e) {
            System.err.println("  [Error Symja] Evaluación de '" + expression + "': " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Sustituye MÚLTIPLES constantes en una expresión de forma segura.
     * 
     * Estrategia MEJORADA (v2):
     * - Construir UN SOLO comando Symja con TODAS las sustituciones
     * - Evita problemas de case-sensitivity (C1 → c1 → C1)
     * - Aplica Simplify al final para optimizar
     * 
     * Ejemplo:
     *   Expression: "C1*cos(x) + C2*sin(x)"
     *   Constants: {C1=2, C2=-1}
     *   Comando: Simplify[(C1*Cos[x] + C2*Sin[x]) /. C1 -> 2 /. C2 -> -1]
     *   Result: "2*Cos(x)-Sin(x)"
     * 
     * @param expression Expresión con constantes (ej: "C1*e^x + C2*cos(x)")
     * @param constants Mapa de constantes y valores (ej: {C1=1, C2=2})
     * @return Expresión con constantes sustituidas y simplificada
     */
    public static String substituteMultipleConstants(String expression, java.util.Map<String, Double> constants) {
        try {
            String symjaSyntax = convertToSymjaSyntax(expression);
            
            // Construir el comando con TODAS las sustituciones en una sola llamada
            StringBuilder command = new StringBuilder("Simplify[(").append(symjaSyntax).append(")");
            
            for (java.util.Map.Entry<String, Double> entry : constants.entrySet()) {
                String constName = entry.getKey();
                double value = entry.getValue();
                
                // Redondear valores muy cercanos a enteros
                double roundedValue = Math.round(value * 1e10) / 1e10;
                
                command.append(" /. ").append(constName).append(" -> ").append(roundedValue);
            }
            
            command.append("]");
            
            // Ejecutar el comando completo
            IExpr result = EVALUATOR.eval(command.toString());
            return result.toString();
            
        } catch (Exception e) {
            System.err.println("[SymjaEngine] Error en substitución múltiple: " + e.getMessage());
            return expression;
        }
    }

    /**
     * Sustituye una constante en una expresión de forma segura.
     * NOTA: Para múltiples constantes, usar substituteMultipleConstants()
     */
    public static String applyConstantSubstitution(String expression, String constant, double value) {
        String symjaSyntax = convertToSymjaSyntax(expression);
        try {
            String substitutionCommand = "Simplify[(" + symjaSyntax + ") /. " + constant + " -> " + value + "]";
            IExpr result = EVALUATOR.eval(substitutionCommand);
            return result.toString();
        } catch (Exception e) {
            System.err.println("  [Error] Sustitución de " + constant + "=" + value + ": " + e.getMessage());
            return expression;
        }
    }
    
    // --- 3. Integración (Usado en VdP) ---

    public static String symbolicIntegral(String functionExpr) {
         try {
            // Convertir a sintaxis Symja
            String symjaSyntax = convertToSymjaSyntax(functionExpr);
            
            // Primero simplificar trigonométricamente con Simplify
            String simplifyCommand = "Simplify[" + symjaSyntax + "]";
            IExpr simplified = EVALUATOR.eval(simplifyCommand);
            String simplifiedStr = simplified.toString();
            
            // Luego integrar
            String integralCommand = "Integrate[" + simplifiedStr + ", x]";
            IExpr integral = EVALUATOR.eval(integralCommand);
            
            // Comprueba si Symja devolvió la integral sin resolver (no elemental)
            String result = integral.toString();
            if (result.startsWith("Integrate[") || result.startsWith("∫")) {
                return "∫ (" + functionExpr + ") dx";
            }
            return result;
        } catch (Exception e) {
            return "∫ (" + functionExpr + ") dx";
        }
    }

    // --- 4. Extracción de Coeficientes (Usado en CI) ---

    /**
     * Extrae el coeficiente simbólico de `functionalTerm` en `expression` de forma INTELIGENTE.
     * 
     * Estrategia:
     * 1. SIEMPRE intentar Coefficient[] primero (funciona para polinomios y términos simples)
     * 2. Si falla, usar división + TrigExpand + Simplify (para términos complejos)
     * 3. Validar que el resultado sea válido (no fracción "numero/x")
     */
    public static String extractCoefficientExpr(String expression, String functionalTerm) {
        String symjaExpr = convertToSymjaSyntax(expression);
        String symjaTerm = convertToSymjaSyntax(functionalTerm);
        try {
            // PASO 0: Simplificar la expresión primero para normalizar
            String simplifyCmd = "Simplify[" + symjaExpr + "]";
            IExpr simplifiedExpr = EVALUATOR.eval(simplifyCmd);
            String simplifiedStr = simplifiedExpr.toString();
            
            // CASO ESPECIAL: Si estamos buscando el coeficiente de "1" (término constante)
            // significa que queremos saber si la expresión es constante (sin x).
            // Si functionalTerm = "1", extraer la parte constante de la expresión.
            if (functionalTerm.equals("1") || functionalTerm.equals("1.0")) {
                // Usar Coefficient[] para extraer términos de grado 0 (constantes)
                String coeffCmd = "Coefficient[" + simplifiedStr + ", x, 0]";
                try {
                    IExpr coeff = EVALUATOR.eval(coeffCmd);
                    String result = coeff.toString();
                    if (!result.startsWith("Coefficient[")) {
                        // Funcionó
                        if (result.equals("0")) return "0";
                        return result;
                    }
                } catch (Exception e) {
                    // Si eso no funciona, intentar como constante pure
                    // Si NO contiene 'x', es puramente constante
                    if (!simplifiedStr.matches("(?s).*(?<![A-Za-z0-9_])x(?![A-Za-z0-9_]).*")) {
                        return simplifiedStr;
                    }
                    return "0";
                }
            }
            
            // Estrategia 1: INTENTAR Coefficient[] primero (funciona para polinomios, exponenciales, etc)
            // Usar la expresión ya simplificada
            String coeffCmd = "Coefficient[" + simplifiedStr + ", " + symjaTerm + "]";
            IExpr coeff = EVALUATOR.eval(coeffCmd);
            String result = coeff.toString();
            
            // Si Coefficient funcionó (retorna algo que no es "Coefficient[...")
            if (!result.startsWith("Coefficient[")) {
                if (result.equals("0")) return "0";
                return result;
            }
            
            // Estrategia 2: Si Coefficient[] no funcionó, usar división + simplificación
            String divisionCommand = "(" + simplifiedStr + ") / (" + symjaTerm + ")";
            IExpr quotient = EVALUATOR.eval(divisionCommand);
            
            String quotientStr = quotient.toString();
            
            // Expand para distribuir
            String expandCmd = "Expand[" + quotientStr + "]";
            IExpr expanded = EVALUATOR.eval(expandCmd);
            
            // TrigExpand para expandir trig
            String trigCmd = "TrigExpand[" + expanded.toString() + "]";
            IExpr trigExpanded = EVALUATOR.eval(trigCmd);
            
            // Simplify para colapsar
            String simplifyCmd2 = "Simplify[" + trigExpanded.toString() + "]";
            IExpr simplified = EVALUATOR.eval(simplifyCmd2);
            
            String result2 = simplified.toString();
            
            if (result2.equals("0")) return "0";
            
            // VALIDACIÓN: Si es fracción pura "numero/x", retornar "0"
            if (result2.contains("/") && !result2.matches(".*[a-zA-Z]+\\(.*\\).*")) {
                String[] parts = result2.split("/");
                if (parts.length == 2 && !parts[0].contains("x") && parts[1].contains("x")) {
                    return "0";
                }
            }

            return result2;
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * Simplifica una expresión Symja y trata de evaluarla numéricamente. Si la
     * expresión resulta en una constante devuelve su valor double; si contiene
     * símbolos (por ejemplo 'x') devuelve Double.NaN SIN CUELGUES.
     * 
     * FIX: Primero SIMPLIFICA la expresión (Expand, Simplify, etc.) para colapsar sumas
     * y luego verifica si contiene SOLO la variable 'x' (no otras variables/símbolos).
     * También usa TrigReduce para cancelar términos trig que se simplifican.
     */
    public static double simplifyAndEvalDouble(String symjaExpression) {
        try {
            // Convertir la expresión a sintaxis Symja (por ejemplo sin() -> Sin[])
            String symjaSyntax = convertToSymjaSyntax(symjaExpression);

            // PASO 1: Simplificar agresivamente la expresión
            // Primero Expand para distribuir, luego TrigReduce para cancelar trig, luego Simplify
            String expandCmd = "Expand[" + symjaSyntax + "]";
            IExpr expanded = EVALUATOR.eval(expandCmd);
            
            String trigCmd = "TrigReduce[" + expanded.toString() + "]";
            IExpr trigReduced = EVALUATOR.eval(trigCmd);
            
            String simplifyCommand = "Simplify[" + trigReduced.toString() + "]";
            IExpr simplified = EVALUATOR.eval(simplifyCommand);
            String simplifiedStr = simplified.toString();

            // PASO 2: Detectar únicamente la variable independiente 'x' (no rechazar nombres de
            // funciones como "Sin"/"Cos"/"Sec"). Si la expresión contiene la variable
            // 'x' en forma de identificador, no es una constante y devolvemos NaN.
            if (simplifiedStr.matches("(?s).*(?<![A-Za-z0-9_])x(?![A-Za-z0-9_]).*")) {
                return Double.NaN;
            }

            // PASO 3: Intentar evaluar numéricamente usando Symja. Esto permite que expresiones
            // como Sin[2] o Sec[Pi/4] sean evaluadas correctamente aunque contengan
            // letras en los nombres de funciones.
            String numericCommand = "N[" + simplifiedStr + "]";
            IExpr numeric = EVALUATOR.eval(numericCommand);
            try {
                return numeric.evalDouble();
            } catch (Exception e) {
                return Double.NaN;
            }
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    /**
     * Backwards compatible helper used por callers previos; intenta extraer y evaluar
     * numéricamente el coeficiente. Si no es numérico devuelve 0.0
     */
    public static double extractCoefficient(String expression, String functionalTerm) {
        String coeffExpr = extractCoefficientExpr(expression, functionalTerm);
        double val = simplifyAndEvalDouble(coeffExpr);
        if (Double.isNaN(val)) return 0.0;
        return val;
    }
    
    // --- 5. Determinante (Usado en VdP) ---
    
    public static String calculateDeterminant(List<List<String>> matrix) {
        try {
            String detCommand = "Det[{" + matrix.stream()
                .map(row -> "{" + String.join(", ", row) + "}")
                .collect(Collectors.joining(", ")) + "}]";
            IExpr det = EVALUATOR.eval(detCommand);
            return det.toString();
        } catch (Exception e) {
            return "det(W)";
        }
    }
    
    // --- 6. Solución Polinomial (Usado en Solución Homogénea) ---
    
    public static IExpr solvePolynomial(String polynomialStr) {
        try {
            String solveCommand = "Solve[" + polynomialStr + " = 0, r]";
            IExpr result = EVALUATOR.eval(solveCommand);
            return result;
        } catch (Exception e) {
            return F.Null;
        }
    }
}
    