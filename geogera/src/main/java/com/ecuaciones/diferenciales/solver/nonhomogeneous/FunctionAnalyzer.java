package com.ecuaciones.diferenciales.solver.nonhomogeneous;

import com.ecuaciones.diferenciales.model.templates.FunctionType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FunctionAnalyzer {

    // Clase interna para almacenar el resultado del análisis de un solo término (CORRECTO)
    public static class AnalyzedTerm {
        public FunctionType type;
        public String expression; // Expresión original o fusionada
        public int degree;   
        public double alpha; 
        public double beta;  

        public AnalyzedTerm(FunctionType type, String expression) {
            this.type = type;
            this.expression = expression;
            this.degree = 0;
            this.alpha = 0.0;
            this.beta = 0.0;
        }
        
        // Clave de Agrupación para UC
        public String getUCKey() {
             // Agrupar por (alpha, |beta|). El grado se maneja en la fusión.
             return String.format("%.4f|%.4f", alpha, Math.abs(beta));
        }

        @Override
        public String toString() {
            return String.format("{%s, expr='%s', deg=%d, alpha=%.4f, beta=%.4f}", type, expression, degree, alpha, beta);
        }
    }

    // --- PATRONES REGEX CORREGIDOS Y ROBUSTOS ---
    
    // Captura alpha en e^(alpha*x)
    private static final Pattern EXP_PATTERN = Pattern.compile(
        "e\\^\\(?([+\\-]?(?:\\d*\\.?\\d*))\\s*x\\)?", Pattern.CASE_INSENSITIVE
    );
    
    // Captura beta en sin/cos(beta*x)
    private static final Pattern SIN_COS_PATTERN = Pattern.compile(
        "(?:sin|cos)\\s*\\(?\\s*([+\\-]?(?:\\d*\\.?\\d*|))\\s*x\\)?", Pattern.CASE_INSENSITIVE
    );
    
    // Patrón interno para detectar el grado del polinomio (no se usa en la función principal)
    // private static final Pattern POLY_PATTERN = Pattern.compile(
    //     "([+\\-]?(?:\\d*\\.?\\d*))\\*?x\\^?(\\d*)", Pattern.CASE_INSENSITIVE
    // );
    // ---------------------------------------------


    public List<AnalyzedTerm> analyze(String g_x) {
        String normalizedG = g_x.replaceAll("\\s+", "").toLowerCase();
        
        List<String> rawTerms = findAllTerms(normalizedG);

        List<AnalyzedTerm> terms = rawTerms.stream()
            .map(this::analyzeSingleTerm)
            .filter(a -> a.type != FunctionType.UNKNOWN)
            .collect(Collectors.toList());
        
        // --- CRÍTICO: AGREGAR EL PASO DE AGRUPACIÓN (FUSIÓN) ---
        return mergeTrigonometricTerms(terms);
    }
    
    /**
     * Fusión de términos trigonométricos (cos/sin) que comparten (alpha, |beta|)
     */
    private List<AnalyzedTerm> mergeTrigonometricTerms(List<AnalyzedTerm> terms) {
        
        Map<String, List<AnalyzedTerm>> groupedTerms = terms.stream()
            // Agrupar por (alpha, |beta|). Esta es la clave UCKey.
            .collect(Collectors.groupingBy(AnalyzedTerm::getUCKey));
        
        List<AnalyzedTerm> finalTerms = new ArrayList<>();
        
        for (Map.Entry<String, List<AnalyzedTerm>> entry : groupedTerms.entrySet()) {
            List<AnalyzedTerm> group = entry.getValue();
            
            // Si beta es cero (Exponencial o Polinomial puro), no hay fusión de sin/cos
            if (Math.abs(group.get(0).beta) < 1e-9) {
                finalTerms.addAll(group);
                continue;
            }
            
            // Si hay fusión (sin/cos):
            
            // 1. Encontrar el grado máximo del polinomio P(x) en el grupo
            int maxDegree = group.stream().mapToInt(t -> t.degree).max().orElse(0);
            
            // 2. Crear el término fusionado (usando el primer término como base)
            AnalyzedTerm mergedTerm = group.get(0);
            mergedTerm.degree = maxDegree;
            
            // Aseguramos el tipo correcto para la forma propuesta
            mergedTerm.type = (Math.abs(mergedTerm.alpha) > 1e-9) ? FunctionType.PRODUCT : FunctionType.SINUSOIDAL;
            
            finalTerms.add(mergedTerm);
        }
        
        return finalTerms;
    }


    // --- findAllTerms: Separar g(x) por signos (+/-) ---
    private List<String> findAllTerms(String input) {
        // Asegurar que la cadena empiece con signo para el regex
        if (!input.matches("^[+\\-].*")) {
            input = "+" + input; 
        }
        // Captura: Signo (Grupo 1) y Término (Grupo 2)
        Pattern p = Pattern.compile("([+\\-])([^+\\-]+)");
        Matcher m = p.matcher(input);
        List<String> matches = new ArrayList<>();
        
        while (m.find()) {
             // Devolvemos solo el término (sin signo)
             matches.add(m.group(2).trim()); 
        }
        return matches;
    }
    
    // --- LÓGICA CENTRAL DE ANÁLISIS ---
    private AnalyzedTerm analyzeSingleTerm(String term) {
        
        String baseTerm = term; 
        double alpha = 0.0;
        double beta = 0.0;

        // 1. Encontrar y extraer Alpha
        Matcher mExp = EXP_PATTERN.matcher(baseTerm);
        if (mExp.find()) {
            alpha = parseCoefficient(mExp.group(1));
            // Eliminar el factor exponencial para aislar el resto del término
            baseTerm = mExp.replaceAll("").trim().replaceAll("^\\*|\\*$", "").replaceAll("\\s*\\*\\s*", "*");
        }
        
        // 2. Encontrar y extraer Beta
        Matcher mSinCos = SIN_COS_PATTERN.matcher(baseTerm);
        if (mSinCos.find()) {
            beta = parseCoefficient(mSinCos.group(1));
            // Eliminar el factor trigonométrico para aislar el polinomio
            baseTerm = mSinCos.replaceAll("").trim().replaceAll("^\\*|\\*$", "").replaceAll("\\s*\\*\\s*", "*");
        }
        
        // 3. Determinar el Polinomio P(x) (el residuo)
        // El residuo baseTerm contiene C*P(x) (ej: 8*x^2 o solo 8)
        int maxDegree = determinePolynomialDegree(baseTerm);

        // 4. Clasificación y Ensamblaje
        AnalyzedTerm a = new AnalyzedTerm(FunctionType.PRODUCT, term);
        a.alpha = alpha;
        a.beta = beta;
        a.degree = maxDegree;
        
        if (Math.abs(alpha) < 1e-9 && Math.abs(beta) < 1e-9) {
            // Caso 1: Polinomial puro
            a.type = maxDegree == 0 ? FunctionType.CONSTANT : FunctionType.POLYNOMIAL;
        } else if (Math.abs(beta) < 1e-9) {
            // Caso 2: Exponencial pura (incluye P(x)e^ax)
            a.type = FunctionType.EXPONENTIAL;
        } else if (Math.abs(alpha) < 1e-9) {
            // Caso 3: Sinusoidal pura (incluye P(x)sin/cos(bx))
            a.type = FunctionType.SINUSOIDAL;
        } else {
             // Caso 4: Producto (P(x)e^ax * sin/cos(bx))
             a.type = FunctionType.PRODUCT; 
        }
        
        return a;
    }
    
    // --- determinePolynomialDegree: Determina el grado máximo del polinomio residuo ---
    private int determinePolynomialDegree(String polyTerm) {
        int maxDegree = 0;
        // Eliminar multiplicadores '*' y espacios para facilitar la búsqueda
        String normalizedPoly = polyTerm.replaceAll("\\s*\\*\\s*", "").toLowerCase();

        // Patrón para capturar x o x^n
        // Captura: 'x' seguido de ^(n) opcional (Grupo 1 es 'n')
        Pattern p = Pattern.compile("x(?:\\^(\\d+))?");
        Matcher m = p.matcher(normalizedPoly);
        
        // Iterar sobre todos los monomios que contengan 'x'
        while(m.find()) {
            String exponentStr = m.group(1);
            int currentDegree = 1; 
            
            if (exponentStr != null && !exponentStr.isEmpty()) {
                // Si encontramos 'x^n', capturamos n
                try {
                    currentDegree = Integer.parseInt(exponentStr);
                } catch (NumberFormatException e) {
                    currentDegree = 1; // Fallback
                }
            }
            maxDegree = Math.max(maxDegree, currentDegree);
        }
        
        // Si la cadena contiene 'x' (p. ej., '5x' o 'x') y no se encontró un grado mayor, 
        // el bucle de arriba debería haber establecido maxDegree a 1.
        
        return maxDegree;
    }

    /**
     * Parsea el coeficiente extraído del regex. Maneja implicit 1 y -1.
     */
    private double parseCoefficient(String coeffStr) {
        coeffStr = coeffStr.replaceAll("\\s", "");
        if (coeffStr.isEmpty() || coeffStr.equals("+")) return 1.0;
        if (coeffStr.equals("-")) return -1.0;
        try {
            return Double.parseDouble(coeffStr);
        } catch (NumberFormatException e) {
            return 1.0; 
        }
    }
}