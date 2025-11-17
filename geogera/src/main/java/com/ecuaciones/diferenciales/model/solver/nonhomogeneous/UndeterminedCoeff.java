package com.ecuaciones.diferenciales.model.solver.nonhomogeneous;

import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.templates.FunctionType;
import java.util.*;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.*;

/**
 * Genera la forma de la solución particular y_p*(x) para Coeficientes Indeterminados 
 * y la solución particular final (y_p). Genera TÉRMINOS ATÓMICOS para la matriz.
 */
public class UndeterminedCoeff {

    private final List<Root> homogeneousRoots;
    private static final DecimalFormat DF = new DecimalFormat("#.####", new java.text.DecimalFormatSymbols(Locale.US));
    private static final double TOLERANCE = 1e-9;

    // Lista de nombres de coeficientes (A, B, C...)
    private final List<String> solvedCoeffNames = new ArrayList<>();
    
    // --- LISTAS DE TÉRMINOS ATÓMICOS SEPARADAS ---
    // 1. Términos Base (para las FILAS de la matriz A|b y el vector b)
    //    (Ej: cos(2x), sin(2x))
    private final List<String> baseUCTerms = new ArrayList<>(); 
    // 2. Términos Propuestos (para las COLUMNAS de la matriz A)
    //    (Ej: x*cos(2x), x*sin(2x))
    private final List<String> ypStarTerms = new ArrayList<>(); 
    
    // Asumimos que FunctionAnalyzer está en el mismo paquete o importado
    private final FunctionAnalyzer analyzer = new FunctionAnalyzer(); 

    public UndeterminedCoeff(List<Root> roots) {
        this.homogeneousRoots = roots;
    }

    // --- GETTERS ACTUALIZADOS ---
    /**
     * Devuelve los términos del "UC Set" base (sin resonancia).
     * Se usa para las FILAS del sistema A|b y para analizar g(x).
     */
    public List<String> getBaseUCTerms() {
        return this.baseUCTerms;
    }
    
    /**
     * Devuelve los términos de la forma y_p* (con resonancia x^s aplicada).
     * Se usa para las COLUMNAS del sistema A|b (los términos que se derivan).
     */
    public List<String> getYpStarTerms() {
        return this.ypStarTerms;
    }
    // ----------------------------

    private String formatValue(double value) {
        // Manejar valores muy pequeños como 0
        if (Math.abs(value) < TOLERANCE) return "0";
        // Formatear enteros sin decimales
        if (Math.abs(value - Math.round(value)) < TOLERANCE) {
            return String.valueOf(Math.round(value));
        }
        return DF.format(value);
    }
    
    private String getXPower(int i) {
        if (i == 0) return "1";
        if (i == 1) return "x";
        return "x^" + i;
    }

    private Map.Entry<List<String>, Character> generateAtomicPolynomials(int degree, char startCoeff, int numPolys) {
        List<String> polynomialTerms = new ArrayList<>();
        char currentCoeff = startCoeff;

        for (int i = 0; i <= degree; i++) {
            polynomialTerms.add(getXPower(i));
        }
        
        for (int p = 0; p < numPolys; p++) {
             for (int i = 0; i <= degree; i++) {
                this.solvedCoeffNames.add(String.valueOf(currentCoeff++));
            }
        }
        
        // Usa la clase interna SimpleEntry de AbstractMap para compatibilidad
        return new java.util.AbstractMap.SimpleEntry<>(polynomialTerms, currentCoeff);
    }

    /**
     * Lógica de resonancia simplificada y correcta.
     * Comprueba si (alpha, beta) de g(x) coincide con una raíz homogénea.
     */
    private int findDuplicityFactor(double alpha, double beta) {
        int s = 0;
        double absBeta = Math.abs(beta);
        
        for (Root r : homogeneousRoots) {
            double rAlpha = r.getReal();
            double rBeta = Math.abs(r.getImaginary());  // Comparar magnitudes de beta
            
            // Comprueba si la raíz (alpha, beta) de g(x) coincide con una raíz homogénea (rAlpha, rBeta)
            if (Math.abs(alpha - rAlpha) < TOLERANCE && Math.abs(absBeta - rBeta) < TOLERANCE) {
                // s es la multiplicidad de esa raíz
                s = Math.max(s, r.getMultiplicity());
            }
        }
        return s;
    }

    /**
     * Genera la forma de yp* usando la REGLA MATEMÁTICA CORRECTA para la resonancia.
     * yp* = x^s * (Polinomio_grado_deg * ...)
     */
    private Map.Entry<String, Character> getFormForSingleTerm(FunctionAnalyzer.AnalyzedTerm analyzedTerm, char startCoeff) {
        double alpha = analyzedTerm.alpha;
        double beta = analyzedTerm.beta;
        int deg = analyzedTerm.degree; // Grado original del polinomio en g(x)
        char nextCoeff = startCoeff;
        
        // 1. OBTENER FACTOR DE RESONANCIA (s)
        int s = findDuplicityFactor(alpha, beta);
        String xResonanceFactor = (s == 0) ? "" : (s == 1) ? "x" : getXPower(s);
        String xResonanceSuffix = xResonanceFactor.isEmpty() ? "" : xResonanceFactor + "*";

        // 2. OBTENER FACTOR EXPONENCIAL
        String expFactor = "";
        if (Math.abs(alpha) > TOLERANCE) {
            String alphaStr = formatValue(alpha);
            // Usar paréntesis para Symja
            expFactor = "e^(" + alphaStr + "*x)"; 
        }
        String expFactorSuffix = expFactor.isEmpty() ? "" : "*" + expFactor; 

        StringBuilder visualFormSb = new StringBuilder();

        // 3. GENERAR POLINOMIOS (BASADOS EN 'deg', NO 'adjustedDeg')
        int numPolys = (Math.abs(beta) > TOLERANCE) ? 2 : 1; // 1 para real/exp, 2 para trig
        Map.Entry<List<String>, Character> poly = generateAtomicPolynomials(deg, nextCoeff, numPolys);
        List<String> pTermsPure = poly.getKey(); // [1, x, x^2]
        nextCoeff = poly.getValue();
        
        int coeffIndexStart = this.solvedCoeffNames.size() - (numPolys * (deg + 1));

        if (Math.abs(beta) > TOLERANCE) { // Caso Trigonométrico
            
            double absBeta = Math.abs(beta);
            String betaStr = formatValue(absBeta);
            // CORRECCIÓN de notación para Symja
            String trigArg = (betaStr.equals("1") || betaStr.isEmpty()) ? "x" : betaStr + "*x"; 
            
            String cosTermCore = "cos(" + trigArg + ")";
            String sinTermCore = "sin(" + trigArg + ")";
            
            String polyA = "";
            String polyB = "";
            
            for (int i = 0; i <= deg; i++) { // <-- BUCLE HASTA 'deg'
                String pTermPure = pTermsPure.get(i); // x^i
                String currentXPower = pTermPure.equals("1") ? "" : pTermPure;
                String xPowerSuffix = currentXPower.isEmpty() ? "" : currentXPower + "*";
                
                // --- TÉRMINOS BASE (FILAS) --- (SIN resonancia x^s)
                String baseAtomicTermCos = cleanTerm(xPowerSuffix + cosTermCore + expFactorSuffix);
                String baseAtomicTermSin = cleanTerm(xPowerSuffix + sinTermCore + expFactorSuffix);
                this.baseUCTerms.add(baseAtomicTermCos);
                this.baseUCTerms.add(baseAtomicTermSin);

                // --- TÉRMINOS YP* (COLUMNAS) --- (CON resonancia x^s)
                // Se multiplica el término base por x^s
                String ypAtomicTermCos = cleanTerm(xResonanceSuffix + baseAtomicTermCos);
                String ypAtomicTermSin = cleanTerm(xResonanceSuffix + baseAtomicTermSin);
                this.ypStarTerms.add(ypAtomicTermCos);
                this.ypStarTerms.add(ypAtomicTermSin);

                // --- Construcción Visual ---
                String coeffNameA = this.solvedCoeffNames.get(coeffIndexStart + 2*i);
                String coeffNameB = this.solvedCoeffNames.get(coeffIndexStart + 2*i + 1);

                if (polyA.length() > 0) polyA += " + ";
                polyA += coeffNameA;
                if (!currentXPower.isEmpty()) polyA += " * " + currentXPower;

                if (polyB.length() > 0) polyB += " + ";
                polyB += coeffNameB;
                if (!currentXPower.isEmpty()) polyB += " * " + currentXPower;
            }
            
            String polyVisualA = (deg > 0) ? "(" + polyA + ")" : polyA;
            String polyVisualB = (deg > 0) ? "(" + polyB + ")" : polyB;
            String combinedCore = "(" + polyVisualA + " * " + cosTermCore + " + " + polyVisualB + " * " + sinTermCore + ")";

            visualFormSb.setLength(0);
            if (!xResonanceFactor.isEmpty()) visualFormSb.append(xResonanceFactor).append(" * ");
            if (!expFactor.isEmpty()) visualFormSb.append(expFactor).append(" * ");
            visualFormSb.append(combinedCore);

        } else { // Caso Polinomial o Exponencial
            
            StringBuilder pFormVisual = new StringBuilder();
            int currentCoeffIndex = coeffIndexStart; 

            for (int i = 0; i <= deg; i++) { // <-- BUCLE HASTA 'deg'
                String pTermPure = pTermsPure.get(i); // x^i
                String currentXPower = pTermPure.equals("1") ? "" : pTermPure;
                String xPowerSuffix = currentXPower.isEmpty() ? "" : currentXPower + "*";

                // --- TÉRMINO BASE (FILA) --- (SIN resonancia x^s)
                String baseAtomicTerm = cleanTerm(xPowerSuffix + expFactor);
                if (baseAtomicTerm.isEmpty()) baseAtomicTerm = "1"; // Caso P(x)=C, e^0x
                this.baseUCTerms.add(baseAtomicTerm);

                // --- TÉRMINO YP* (COLUMNA) --- (CON resonancia x^s)
                String ypAtomicTerm = cleanTerm(xResonanceSuffix + baseAtomicTerm);
                if (ypAtomicTerm.isEmpty()) ypAtomicTerm = "1";
                this.ypStarTerms.add(ypAtomicTerm);

                // --- Construcción Visual ---
                String currentCoeffName = this.solvedCoeffNames.get(currentCoeffIndex++);
                if (pFormVisual.length() > 0) pFormVisual.append(" + ");
                pFormVisual.append(currentCoeffName); 
                if (!pTermPure.equals("1")) pFormVisual.append(" * ").append(pTermPure);
            }
            
            String polynomialVisual = pFormVisual.toString();
            if (deg > 0) polynomialVisual = "(" + polynomialVisual + ")";
            
            // Ensamblaje Visual: x^s * P(x) * e^(ax)
            visualFormSb.setLength(0);
            if (!xResonanceFactor.isEmpty()) visualFormSb.append(xResonanceFactor).append(" * ");
            visualFormSb.append(polynomialVisual);
            if (!expFactor.isEmpty()) visualFormSb.append(" * ").append(expFactor);
        }
        
        return new java.util.AbstractMap.SimpleEntry<>(cleanTerm(visualFormSb.toString()), nextCoeff);
    }
    
    private String cleanTerm(String term) {
        // Normaliza a x*y*z (sin espacios)
        return term.trim()
                   .replaceAll("\\s*\\*\\s*$", "")
                   .replaceAll("^\\s*\\*\\s*", "")
                   .replaceAll("\\s*\\*\\s*", "*") 
                   .replaceAll("e\\^0\\.[0-9]{4}x", "")
                   .replaceAll("\\*1", "") 
                   .replaceAll("1\\*", "") 
                   .replaceAll("^\\s*|\\s*$", "")
                   .replaceAll("^\\*|\\*$", "") 
                   .trim();
    }
    
    public String getParticularSolutionForm(String gX) { 
        this.baseUCTerms.clear();
        this.ypStarTerms.clear();
        this.solvedCoeffNames.clear();
        
        List<FunctionAnalyzer.AnalyzedTerm> analyzedTerms = analyzer.analyze(gX);
        
        StringBuilder result = new StringBuilder();
        char currentCoeff = 'A';
        
        for (FunctionAnalyzer.AnalyzedTerm analyzedTerm : analyzedTerms) {
            
            if (analyzedTerm.type == FunctionType.UNKNOWN) continue; 

            Map.Entry<String, Character> r = getFormForSingleTerm(analyzedTerm, currentCoeff);
            String form = r.getKey();
            currentCoeff = r.getValue();
            
            if (result.length() > 0) {
                result.append(" + ");
            }
            result.append(form);
        }
        
        // Deduplicación FINAL de ambas listas
        Set<String> uniqueBaseTerms = new LinkedHashSet<>(this.baseUCTerms);
        this.baseUCTerms.clear();
        this.baseUCTerms.addAll(uniqueBaseTerms);
        
        Set<String> uniqueYpStarTerms = new LinkedHashSet<>(this.ypStarTerms);
        this.ypStarTerms.clear();
        this.ypStarTerms.addAll(uniqueYpStarTerms);
        
        // DEBUG: mostrar listas generadas
        System.out.println("\n   [DEBUG UC] baseUCTerms (" + baseUCTerms.size() + "): " + baseUCTerms);
        System.out.println("   [DEBUG UC] ypStarTerms (" + ypStarTerms.size() + "): " + ypStarTerms);
        System.out.println("   [DEBUG UC] coeffNames (" + solvedCoeffNames.size() + "): " + solvedCoeffNames);
        
        // Reemplaza * con " * " para una visualización limpia
        return result.toString().replaceAll("\\*", " * "); 
    }
    
    public List<String> getCoeffNames() {
        // Sincronizar: si hay discrepancia, truncar al mínimo
        int minSize = Math.min(solvedCoeffNames.size(), ypStarTerms.size());
        if (solvedCoeffNames.size() != minSize) {
            solvedCoeffNames.retainAll(new ArrayList<>(solvedCoeffNames.subList(0, minSize)));
        }
        if (ypStarTerms.size() != minSize) {
            ypStarTerms.retainAll(new ArrayList<>(ypStarTerms.subList(0, minSize)));
        }
        return new ArrayList<>(solvedCoeffNames);
    }
    
    public String generateParticularSolution(String ypForm, Map<String, Double> solvedCoeffs) {
        String finalYp = ypForm;
        
        // PASO 1: Reemplazar cada coeficiente por su valor
        for (Map.Entry<String, Double> entry : solvedCoeffs.entrySet()) {
            String coeffName = entry.getKey();
            double value = entry.getValue();
            
            // Reemplazar A, B, C... como palabras completas
            String patternToReplace = "\\b" + Pattern.quote(coeffName) + "\\b"; 
            String valueStr = formatValue(value); 
            finalYp = finalYp.replaceAll(patternToReplace, valueStr);
        }
        
        // PASO 2: Remover paréntesis más externos (si existen)
        if (finalYp.startsWith("(") && finalYp.endsWith(")")) {
            finalYp = finalYp.substring(1, finalYp.length() - 1).trim();
        }
        
        // PASO 3: Eliminar términos multiplicados por 0
        // Patrón: "0 * expr" o "0.0 * expr" → eliminar
        finalYp = finalYp.replaceAll("\\+\\s*0(?:\\.0+)?\\s*\\*\\s*[^\\+\\-]+", "");     // + 0*expr
        finalYp = finalYp.replaceAll("\\-\\s*0(?:\\.0+)?\\s*\\*\\s*[^\\+\\-]+", "");     // - 0*expr
        finalYp = finalYp.replaceAll("^0(?:\\.0+)?\\s*\\*\\s*[^\\+\\-]+", "");           // 0*expr (al inicio)
        
        // PASO 4: Normalizar coeficiente 1
        finalYp = finalYp.replaceAll("\\+\\s*1(?:\\.0+)?\\s*\\*\\s*", " + ");            // + 1*expr → + expr
        finalYp = finalYp.replaceAll("\\-\\s*1(?:\\.0+)?\\s*\\*\\s*", " - ");            // - 1*expr → - expr
        finalYp = finalYp.replaceAll("^1(?:\\.0+)?\\s*\\*\\s*", "");                     // 1*expr (inicio) → expr
        
        // Normalizar -1
        finalYp = finalYp.replaceAll("\\+\\s*-1(?:\\.0+)?\\s*\\*\\s*", " - ");           // + -1*expr → - expr
        finalYp = finalYp.replaceAll("^-1(?:\\.0+)?\\s*\\*\\s*", "-");                   // -1*expr (inicio) → -expr
        
        // PASO 5: Limpiar operadores múltiples y espacios innecesarios
        finalYp = finalYp.replaceAll("\\+\\s*-", " - ");                                 // +- → -
        finalYp = finalYp.replaceAll("\\s*\\+\\s*", " + ").replaceAll("\\s*-\\s*", " - ");
        finalYp = finalYp.replaceAll("^\\s*\\+\\s*", "");                                // Remover + al inicio
        finalYp = finalYp.replaceAll("\\s+", " ").trim();                                // Normalizar espacios
        
        // PASO 6: Remover paréntesis internos que no sean necesarios
        finalYp = finalYp.replaceAll("\\(\\s*([^\\(\\)]+)\\s*\\)", "$1");
        
        // PASO 7: Validar resultado final
        if (finalYp.isEmpty() || finalYp.matches("^[\\+\\-]?\\s*0(?:\\.0+)?\\s*$")) {
            return "0";
        }
        
        return finalYp;
    }
}