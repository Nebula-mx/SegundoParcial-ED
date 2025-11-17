package com.ecuaciones.diferenciales.utils;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 🔧 SymbolicDifferentiator: Calcula derivadas simbólicas usando Symja
 * Delega todas las operaciones a la librería matheclipse-core
 */
public class SymbolicDifferentiator {

    private static final ExprEvaluator EVALUATOR = new ExprEvaluator();

    /**
     * Calcula la derivada simbólica de una expresión respecto a x
     * @param expression La expresión matemática (ej: "x^2 + 3*x + 2")
     * @param order El orden de la derivada (1 para primera, 2 para segunda, etc.)
     * @return La derivada simbólica como string
     */
    public static String differentiate(String expression, int order) {
        try {
            if (order <= 0) return expression;
            
            // Convertir a sintaxis Symja
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            
            // Usar notación string "D[expr, x]" directamente en Symja
            // Esto funciona mejor que F.D() porque usa la sintaxis de Mathematica
            try {
                String derivativeCommand = "D[" + symjaExpr + ", x]";
                for (int i = 1; i < order; i++) {
                    derivativeCommand = "D[" + derivativeCommand + ", x]";
                }
                
                IExpr result = EVALUATOR.eval(derivativeCommand);
                
                // Simplificar el resultado
                IExpr simplified = EVALUATOR.eval(F.Simplify(result));
                
                return simplified.toString();
            } catch (Exception e1) {
                // FALLBACK: Usar F.D() como método alternativo
                IExpr expr = EVALUATOR.parse(symjaExpr);
                
                // Calcular derivadas sucesivas
                for (int i = 0; i < order; i++) {
                    expr = F.D(expr, F.x);
                    expr = EVALUATOR.eval(expr);
                }
                
                // Simplificar el resultado
                IExpr simplified = EVALUATOR.eval(F.Simplify(expr));
                
                return simplified.toString();
            }
        } catch (Exception e) {
            System.err.println("❌ Error calculando derivada de: " + expression);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calcula la primera derivada (orden 1)
     */
    public static String firstDerivative(String expression) {
        return differentiate(expression, 1);
    }

    /**
     * Alias para differentiate (compatibilidad con código existente)
     */
    public static String calculateDerivative(String expression, int order) {
        return differentiate(expression, order);
    }

    /**
     * Calcula la segunda derivada (orden 2)
     */
    public static String secondDerivative(String expression) {
        return differentiate(expression, 2);
    }

    /**
     * Calcula la tercera derivada (orden 3)
     */
    public static String thirdDerivative(String expression) {
        return differentiate(expression, 3);
    }

    /**
     * Simplifica una expresión matemática
     * @param expression La expresión a simplificar
     * @return La expresión simplificada
     */
    public static String simplify(String expression) {
        try {
            // Convertir a sintaxis Symja
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            
            // Parsear y simplificar
            IExpr expr = EVALUATOR.parse(symjaExpr);
            IExpr simplified = EVALUATOR.eval(F.Simplify(expr));
            
            return simplified.toString();
        } catch (Exception e) {
            System.err.println("❌ Error simplificando: " + expression);
            return expression;
        }
    }

    /**
     * Expande una expresión (distribuye productos)
     */
    public static String expand(String expression) {
        try {
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            IExpr expr = EVALUATOR.parse(symjaExpr);
            IExpr expanded = EVALUATOR.eval(F.Expand(expr));
            return expanded.toString();
        } catch (Exception e) {
            System.err.println("❌ Error expandiendo: " + expression);
            return expression;
        }
    }

    /**
     * Factoriza una expresión
     */
    public static String factor(String expression) {
        try {
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            IExpr expr = EVALUATOR.parse(symjaExpr);
            IExpr factored = EVALUATOR.eval(F.Factor(expr));
            return factored.toString();
        } catch (Exception e) {
            System.err.println("❌ Error factorizando: " + expression);
            return expression;
        }
    }

    /**
     * Colecciona términos similares
     */
    public static String collect(String expression) {
        try {
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            IExpr expr = EVALUATOR.parse(symjaExpr);
            IExpr collected = EVALUATOR.eval(F.Collect(expr, F.x));
            return collected.toString();
        } catch (Exception e) {
            System.err.println("❌ Error coleccionando términos: " + expression);
            return expression;
        }
    }

    /**
     * Evalúa una expresión en un punto específico
     */
    public static String evaluate(String expression, String variable, String value) {
        try {
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            // Reemplazar la variable por el valor directamente en la expresión
            String evalExpr = symjaExpr.replace(variable, "(" + value + ")");
            IExpr result = EVALUATOR.parse(evalExpr);
            IExpr evaluated = EVALUATOR.eval(result);
            
            return evaluated.toString();
        } catch (Exception e) {
            System.err.println("❌ Error evaluando: " + expression + " en " + variable + "=" + value);
            return null;
        }
    }

    /**
     * Integra una expresión respecto a x
     */
    public static String integrate(String expression) {
        try {
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            IExpr expr = EVALUATOR.parse(symjaExpr);
            IExpr integrated = EVALUATOR.eval(F.Integrate(expr, F.x));
            return integrated.toString();
        } catch (Exception e) {
            System.err.println("❌ Error integrando: " + expression);
            return null;
        }
    }

    /**
     * Calcula el Wronskiano de dos funciones
     * W(f, g) = f*g' - f'*g
     */
    public static String wronskian(String f, String g) {
        try {
            String df = firstDerivative(f);
            String dg = firstDerivative(g);
            
            // W = f*g' - f'*g
            String wronskian = "(" + f + ")*(" + dg + ") - (" + df + ")*(" + g + ")";
            return simplify(wronskian);
        } catch (Exception e) {
            System.err.println("❌ Error calculando Wronskiano");
            return null;
        }
    }

    /**
     * Valida si una expresión es matemáticamente válida
     */
    public static boolean isValidExpression(String expression) {
        try {
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expression);
            EVALUATOR.parse(symjaExpr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el coeficiente de un término en una expresión
     * Ejemplo: extractCoeff("2*x + 3*x^2", "x") → 2.0
     * Delega a SymjaEngine para evitar duplicación de lógica
     */
    public static double extractCoeff(String expression, String term) {
        return SymjaEngine.extractCoefficient(expression, term);
    }
}
