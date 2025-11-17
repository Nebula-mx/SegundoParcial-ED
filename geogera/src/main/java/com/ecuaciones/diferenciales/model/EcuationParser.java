package com.ecuaciones.diferenciales.model;

import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * Clase encargada de parsear una Ecuación Diferencial Lineal de Coeficientes Constantes
 * de la forma an*y^(n) + ... + a1*y' + a0*y = g(x).
 */
public class EcuationParser extends ODEParser {

    // Patrón simple: Captura el término del coeficiente seguido de la función y su derivada.
    // Grupo 1: Coeficiente con signo (ej: +1, -4.5, -1)
    // Grupo 2: Función (y''', y'', y', y^(n), y)
    // El \*? permite un asterisco opcional entre el coeficiente y la derivada (ej: -3*y' o -3y')
    private static final Pattern TERM_PATTERN = Pattern.compile(
        "([+-]\\s*\\d*\\.?\\d+\\s*)" + 
        "\\*?\\s*" +
        "(y'''|y''|y'|y\\^\\(\\d+\\)|y)"
    );
    
    // --- Lógica de normalización ---
    
    /**
     * Normaliza la cadena para asegurar que todos los términos de la derivada 
     * tengan un coeficiente numérico explícito (+1, -1, etc.).
     */
    private String normalize(String ecuacion) {
        // Paso 1: Normalizar notación (ej: d2y/dx2 -> y'')
        String normalized = normalizeDerivativeNotation(ecuacion); 
        
        // Paso 2: Asegurar coeficientes '1' explícitos.
        // Captura: Signo (+ o -) seguido de una derivada o 'y' que NO tiene un número delante.
        // Normalizar: [+-]y... -> [+-]1y...
        String derivTerms = "(y'''|y''|y'|y\\^\\(\\d+\\)|y)";
        normalized = normalized.replaceAll("([+-])(?!\\d|\\.)" + derivTerms, "$11$2");
        
        return normalized;
    }
    
    /**
     * Implementación del método abstracto parse.
     */
    @Override
    public ExpressionData parse(String rawEcuacion) {
        // CRÍTICO: Se crea una nueva instancia limpia de ExpressionData por cada llamada.
        ExpressionData data = new ExpressionData(); 

        // 1. Separar la ecuación en lado izquierdo (EDO) y lado derecho (g(x))
        String[] parts = rawEcuacion.toLowerCase().split("=");
        String edoPart = parts[0];
        String gxPart = parts.length > 1 ? parts[1].trim() : "0";
        
        // --- LIMPIEZA CRÍTICA DE SIGNOS Y ESPACIOS ---
        edoPart = edoPart.replaceAll("\\s", ""); 
        // Convertir restas a sumas de negativos (ej: y''-4y -> y''+-4y)
        edoPart = edoPart.replaceAll("-", "+-"); 
        
        // Limpiar el inicio de la cadena si hay un doble signo (ej: +-)
        if (edoPart.startsWith("+-")) {
            edoPart = edoPart.substring(2); 
        }
        
        // Asegura que el primer término siempre tenga signo para el REGEX (ej: y'' -> +y'')
        if (!edoPart.startsWith("+") && !edoPart.startsWith("-")) {
            edoPart = "+" + edoPart; 
        }
        
        String normalizedEdo = normalize(edoPart);
        
        Map<Integer, Double> coeffMap = new HashMap<>(); 
        int maxOrder = 0;
        
        // 2. Extraer coeficientes del lado izquierdo (EDO)
        Matcher matcher = TERM_PATTERN.matcher(normalizedEdo);
        
        while (matcher.find()) {
            String coeffStr = matcher.group(1).trim();
            String derivativeStr = matcher.group(2); 
            
            // Determinar el orden
            int order = 0;
            if (derivativeStr.contains("'''")) { order = 3;
            } else if (derivativeStr.contains("''")) { order = 2;
            } else if (derivativeStr.contains("'")) { order = 1;
            } else if (derivativeStr.matches("y\\^\\((\\d+)\\)")) {
                 // Capturar el orden de la notación y^(n)
                 Pattern p = Pattern.compile("y\\^\\((\\d+)\\)");
                 Matcher m = p.matcher(derivativeStr);
                 if (m.find()) {
                     order = Integer.parseInt(m.group(1));
                 }
            } else if (derivativeStr.equals("y")) { order = 0;
            }
            maxOrder = Math.max(maxOrder, order);
            
            double coeff; 
            try {
                // El String coeffStr ya tiene el signo y el valor (ej: "+1", "-4.5").
                coeff = Double.parseDouble(coeffStr);
            } catch (NumberFormatException e) {
                // Esto solo debería ocurrir si la normalización falló por completo.
                coeff = 0.0; 
            }
            
            // Sumar coeficientes para derivadas del mismo orden
            coeffMap.put(order, coeffMap.getOrDefault(order, 0.0) + coeff);
        }

        // 3. Crear el array final de coeficientes (an, an-1, ..., a0)
        Double[] finalCoeffs = new Double[maxOrder + 1]; 
        // Llenar el array en orden decreciente (an, an-1, ..., a0)
        for (int i = 0; i <= maxOrder; i++) {
            finalCoeffs[maxOrder - i] = coeffMap.getOrDefault(i, 0.0); 
        }

        // 4. Llenar ExpressionData
        data.setCoefficients(finalCoeffs); 
        data.setOrder(maxOrder);
        data.setIsHomogeneous(gxPart.trim().equals("0"));
        
        Map<String, String> independentTerm = new HashMap<>();
        independentTerm.put("g(x)", gxPart.trim());
        data.setIndependentTerm(independentTerm);
        
        return data;
    }
}