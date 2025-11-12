package com.ecuaciones.diferenciales.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Proporciona utilidades para manejar la representación simbólica de la integración
 * requerida por el método de Variación de Parámetros (VdP).
 * * NOTA CRÍTICA: Esta clase NO realiza integración simbólica; solo formatea la 
 * representación matemática de la integral no resuelta.
 */
public class IntegrationUtils {

    // Patrones para identificar componentes de la función (para futuras expansiones)
    private static final Pattern EXP_PATTERN = Pattern.compile("e\\^\\(?([\\+\\-]?\\d*\\.?\\d*)x\\)?");
    private static final double TOLERANCE = 1e-9;
    
    /**
     * Devuelve una representación simbólica de la integral de u_k'(x).
     * * @param uk_prime_expression La expresión simbólica de u_k'(x) = W_k / W.
     * @return Una cadena de texto que representa la integral no resuelta (u_k(x)).
     */
    public static String getSymbolicIntegralForm(String uk_prime_expression) {
        // La fórmula de la integral es: u_k(x) = ∫ u_k'(x) dx
        return "∫ (" + uk_prime_expression + ") dx";
    }
    
    /**
     * Intenta resolver la integral para formas muy simples y conocidas (ej: e^ax).
     * Si no se reconoce, devuelve la forma simbólica no resuelta.
     */
    public static String trySimpleIntegration(String expression) {
        // --- 1. Caso Simulación de Integral Exponencial: ∫ e^(ax) dx = (1/a) e^(ax) ---
        Matcher mExp = EXP_PATTERN.matcher(expression);
        if (mExp.matches()) {
            double a = parseCoeff(mExp.group(1));
            if (Math.abs(a) > TOLERANCE) {
                 // Formato: (1/a) * e^(ax)
                 return "(1/" + String.valueOf(a) + ") * " + expression;
            }
        }
        
        // --- 2. Fallback: Integral simbólica ---
        return getSymbolicIntegralForm(expression);
    }
    
    private static double parseCoeff(String coeffStr) {
        if (coeffStr == null || coeffStr.isEmpty() || coeffStr.equals("+")) return 1.0;
        if (coeffStr.equals("-")) return -1.0;
        try {
            return Double.parseDouble(coeffStr);
        } catch (NumberFormatException e) {
            return 1.0; 
        }
    }
}