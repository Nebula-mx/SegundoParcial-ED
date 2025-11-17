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

    public static String convertToSymjaSyntax(String mathString) {
        // Estrategia: Primero reemplazar sin(...), cos(...), etc. con Sin[...], Cos[...], etc.
        // LUEGO hacer otros reemplazos.
        String result = mathString;
        
        // PASO 0: Normalizar E^x a e^(x) antes de cualquier conversión
        // Esto maneja el caso donde Symja retorna E^x y lo reprocesamos
        result = result.replaceAll("E\\^([^\\s\\+\\-\\*\\/\\)\\]]+)", "e^($1)");
        
        // Paso 1: Reemplazar funciones trigonométricas e exponenciales (estos SÍ necesitan corchetes)
        // Usar un approach que matchee los paréntesis correctamente
        result = result.replaceAll("sin\\(", "Sin[");
        result = result.replaceAll("cos\\(", "Cos[");
        result = result.replaceAll("tan\\(", "Tan[");
        result = result.replaceAll("exp\\(", "Exp[");
        result = result.replaceAll("ln\\(", "Log[");
        result = result.replaceAll("log\\(", "Log[");
        
        // Paso 2: Cerrar los corchetes que abrimos (reemplazar ) que cierra una función)
        // Esto es más complejo, necesitamos contar paréntesis
        result = closeMatchingBrackets(result);
        
        // Paso 3: Otros reemplazos
        result = result.replaceAll("e\\^([a-z0-9\\+\\-])", "Exp[$1]");
        result = result.replaceAll("e\\^\\(([^\\)]+)\\)", "Exp[$1]");
        result = result.replaceAll("([0-9])x", "$1*x");
        result = result.replaceAll("([x\\])])(\\()", "$1*$2");
        
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
            
            // Paso 3: Parsear como double
            return Double.parseDouble(numeric.toString());
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
     * Sustituye constantes en una expresión y simplifica.
     * Por ejemplo: applyConstantSubstitution("C1*e^x + C2*e^(2*x)", "C1", 3.0)
     * Resultado: "3.0*e^x + C2*e^(2*x)" simplificado
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

    public static double extractCoefficient(String expression, String functionalTerm) {
        String symjaExpr = convertToSymjaSyntax(expression);
        String symjaTerm = convertToSymjaSyntax(functionalTerm);
        try {
            // Comando: Coefficient[expresión, término]
            String coeffCommand = "Coefficient[" + symjaExpr + ", " + symjaTerm + "]";
            IExpr result = EVALUATOR.eval(coeffCommand);
            
            // Intentar obtener el valor numérico
            try {
                return Double.parseDouble(result.toString());
            } catch (NumberFormatException nfe) {
                return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
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
    