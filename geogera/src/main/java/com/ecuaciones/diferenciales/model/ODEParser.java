package com.ecuaciones.diferenciales.model;

import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase base abstracta para todos los parsers de Ecuaciones Diferenciales Ordinarias (ODE).
 * Define el contrato principal de parseo y provee métodos de utilidad para normalización.
 */
public abstract class ODEParser {

    /**
     * Define el método principal de parseo, que debe ser implementado por subclases
     * para convertir la cadena de la ecuación en un objeto ExpressionData.
     * @param rawEcuacion La cadena de texto de la ecuación de entrada.
     * @return El objeto ExpressionData con los datos extraídos.
     */
    public abstract ExpressionData parse(String rawEcuacion);

    /**
     * Convierte la notación de Leibniz (d^ny/dx^n) y Operador D (D^ny)
     * a notación de comillas (y', y'', y''') o superíndice (y^(n)).
     * @param ecuacion La subcadena de la ecuación a normalizar.
     * @return La cadena con las notaciones reemplazadas.
     */
    protected String normalizeDerivativeNotation(String ecuacion) {
        
        // 1. Normalización de Leibniz: d^ny/dx^n 
        // Captura: d seguido del orden n (opcional, para dy/dx) y/dx seguido del orden n (opcional)
        // Patrón: d(n?)y/dx(n?)
        Pattern leibnizPattern = Pattern.compile("d(\\d*)y/dx(\\d*)");
        Matcher matcher = leibnizPattern.matcher(ecuacion);
        
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            
            // Si group(1) (el orden) es vacío, se asume 1 (dy/dx)
            String orderStr = matcher.group(1).isEmpty() ? "1" : matcher.group(1);
            
            try {
                int order = Integer.parseInt(orderStr);
                String replacement;

                if (order == 0) {
                    replacement = "y";
                } else if (order == 1) {
                    replacement = "y'";
                } else if (order == 2) {
                     replacement = "y''"; 
                } else if (order == 3) {
                     replacement = "y'''";
                } else {
                     // Caso >= 4: y^(n)
                     // CRÍTICO: Se usan dos backslashes para escapar el '^' y los paréntesis en quoteReplacement.
                     replacement = "y\\^(" + order + ")";
                }
                
                // Realiza el reemplazo seguro.
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } catch (NumberFormatException e) {
                // Si el orden no es un número válido, se mantiene el texto original.
                matcher.appendReplacement(result, matcher.group(0));
            }
        }
        matcher.appendTail(result);
        
        // 2. Normalización de otras notaciones sobre la cadena parcialmente procesada
        String finalResult = result.toString();
        
        // a) Reemplazar y(n) (notación y(orden)) con y^(n)
        // Ejemplo: y(4) -> y^(4)
        finalResult = finalResult.replaceAll("y\\((\\d+)\\)", "y\\^($1)");
        
        // b) Reemplazar Dny (Operador D) con y^(n) o y'
        // Ejemplo: D^4y -> y^(4), D3y -> y^(3)
        finalResult = finalResult.replaceAll("D\\^(\\d+)y", "y\\^($1)");
        finalResult = finalResult.replaceAll("D(\\d+)y", "y\\^($1)");
        // Ejemplo: Dy -> y'
        finalResult = finalResult.replaceAll("Dy", "y'");

        return finalResult;
    }
}