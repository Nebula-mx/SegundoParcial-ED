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

    private int findDuplicityFactor(double alpha, double beta) {
        int s = 0;
        double absBeta = Math.abs(beta);
        for (Root r : homogeneousRoots) {
            double rAlpha = r.getReal();
            double rBeta = r.getImaginary();
            
            if (Math.abs(alpha - rAlpha) < TOLERANCE && Math.abs(absBeta - rBeta) < TOLERANCE) {
                s = Math.max(s, r.getMultiplicity());
            }
        }
        // Si no encontró coincidencia exacta, al menos verificar resonancia simple (s=1)
        // Para el caso cos(bx) con raiz 0±bi, s debería ser 1
        if (s == 0 && Math.abs(alpha) < TOLERANCE) {
            for (Root r : homogeneousRoots) {
                if (Math.abs(r.getReal()) < TOLERANCE && Math.abs(Math.abs(r.getImaginary()) - absBeta) < TOLERANCE) {
                    s = 1;
                    break;
                }
            }
        }
        return s;
    }

    private Map.Entry<String, Character> getFormForSingleTerm(FunctionAnalyzer.AnalyzedTerm analyzedTerm, char startCoeff) {
        double alpha = analyzedTerm.alpha;
        double beta = analyzedTerm.beta;
        int deg = analyzedTerm.degree;
        char nextCoeff = startCoeff;
        
        int s = findDuplicityFactor(alpha, beta);
        // Para resonancia, AUMENTAR EL GRADO del polinomio en lugar de multiplicar por x
        // Esto evita problemas de simplificación en Symja con términos como x*e^(x)
        int adjustedDeg = deg + s;
        
        String expFactor = "";
        if (Math.abs(alpha) > TOLERANCE) {
            String alphaStr = formatValue(alpha);
            // Usar paréntesis para claridad: e^(alpha*x) para evitar ambigüedad
            expFactor = "e^(" + alphaStr + "*x)";
        }
        // Normalizado a * (sin espacios) para SymbolicDifferentiator
        String expFactorSuffix = expFactor.isEmpty() ? "" : "*" + expFactor; 

        StringBuilder visualFormSb = new StringBuilder();

        int numPolys = (Math.abs(beta) > TOLERANCE) ? 2 : 1;
        Map.Entry<List<String>, Character> poly = generateAtomicPolynomials(adjustedDeg, nextCoeff, numPolys);
        List<String> pTermsPure = poly.getKey(); // [1], [x], [x^2], etc.
        nextCoeff = poly.getValue();
        
        int coeffIndexStart = this.solvedCoeffNames.size() - (numPolys * (adjustedDeg + 1));

        if (Math.abs(beta) > TOLERANCE) { // Caso Trigonométrico
            
            double absBeta = Math.abs(beta);
            String betaStr = formatValue(absBeta);
            String cosTermCore = "cos(" + betaStr + "x)";
            String sinTermCore = "sin(" + betaStr + "x)";
            
            String polyA = "";
            String polyB = "";
            
            for (int i = 0; i <= adjustedDeg; i++) {
                String pTermPure = pTermsPure.get(i); // x^i
                String currentXPower = pTermPure.equals("1") ? "" : pTermPure;
                
                // --- TÉRMINOS BASE (FILAS) --- (sin x^s adicional)
                String baseAtomicTermCos = cleanTerm(currentXPower.isEmpty() ? cosTermCore + expFactorSuffix : currentXPower + "*" + cosTermCore + expFactorSuffix);
                String baseAtomicTermSin = cleanTerm(currentXPower.isEmpty() ? sinTermCore + expFactorSuffix : currentXPower + "*" + sinTermCore + expFactorSuffix);
                this.baseUCTerms.add(baseAtomicTermCos);
                this.baseUCTerms.add(baseAtomicTermSin);

                // --- TÉRMINOS YP* (COLUMNAS) --- (grado ajustado para resonancia)
                String ypAtomicTermCos = cleanTerm(currentXPower.isEmpty() ? cosTermCore + expFactorSuffix : currentXPower + "*" + cosTermCore + expFactorSuffix);
                String ypAtomicTermSin = cleanTerm(currentXPower.isEmpty() ? sinTermCore + expFactorSuffix : currentXPower + "*" + sinTermCore + expFactorSuffix);
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
            
            String polyVisualA = (adjustedDeg > 0) ? "(" + polyA + ")" : polyA;
            String polyVisualB = (adjustedDeg > 0) ? "(" + polyB + ")" : polyB;
            String combinedCore = "(" + polyVisualA + " * " + cosTermCore + " + " + polyVisualB + " * " + sinTermCore + ")";

            visualFormSb.setLength(0);
            if (!expFactor.isEmpty()) visualFormSb.append(expFactor).append(" * ");
            visualFormSb.append(combinedCore);

        } else { // Caso Polinomial o Exponencial
            
            StringBuilder pFormVisual = new StringBuilder();
            int currentCoeffIndex = coeffIndexStart; 

            for (int i = 0; i <= adjustedDeg; i++) {
                String pTermPure = pTermsPure.get(i); // x^i
                String currentXPower = pTermPure.equals("1") ? "" : pTermPure;

                // --- TÉRMINO BASE (FILA) --- (sin factor x adicional)
                String baseAtomicTerm = cleanTerm(currentXPower.isEmpty() ? expFactor : currentXPower + expFactorSuffix);
                if (baseAtomicTerm.isEmpty()) baseAtomicTerm = "1"; // Caso P(x)=C, e^0x
                this.baseUCTerms.add(baseAtomicTerm);

                // --- TÉRMINO YP* (COLUMNA) --- (misma forma, grado ajustado)
                String ypAtomicTerm = cleanTerm(currentXPower.isEmpty() ? expFactor : currentXPower + expFactorSuffix);
                if (ypAtomicTerm.isEmpty()) ypAtomicTerm = "1";
                this.ypStarTerms.add(ypAtomicTerm);

                // --- Construcción Visual ---
                String currentCoeffName = this.solvedCoeffNames.get(currentCoeffIndex++);
                if (pFormVisual.length() > 0) pFormVisual.append(" + ");
                pFormVisual.append(currentCoeffName); 
                if (!pTermPure.equals("1")) pFormVisual.append(" * ").append(pTermPure);
            }
            
            String polynomialVisual = pFormVisual.toString();
            if (adjustedDeg > 0) polynomialVisual = "(" + polynomialVisual + ")";
            
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
        
        for (Map.Entry<String, Double> entry : solvedCoeffs.entrySet()) {
            String coeffName = entry.getKey();
            double value = entry.getValue();
            
            // Reemplazar A, B, C... como palabras completas
            String patternToReplace = "\\b" + Pattern.quote(coeffName) + "\\b"; 
            String valueStr = formatValue(value); 
            finalYp = finalYp.replaceAll(patternToReplace, valueStr);
        }
        
        // Limpieza de la expresión final
        finalYp = finalYp.replaceAll("\\(0\\.[0-9]{4}\\)", "0");
        finalYp = finalYp.replaceAll("\\(0\\)", "0");
        finalYp = finalYp.replaceAll("0\\.[0-9]{4}\\s*\\*\\s*[^\\+\\-\\)]+", "0");
        finalYp = finalYp.replaceAll("\\b0\\s*\\*\\s*[^\\+\\-\\)]+", "0");
        finalYp = finalYp.replaceAll("\\s*[\\+\\-]\\s*0", "");
        
        finalYp = finalYp.replaceAll("\\b1\\.[0-9]{4}\\s*\\*\\s*", "");
        finalYp = finalYp.replaceAll("\\b1\\s*\\*\\s*", "");
        finalYp = finalYp.replaceAll("\\b-1\\.[0-9]{4}\\s*\\*\\s*", "-");
        finalYp = finalYp.replaceAll("\\b-1\\s*\\*\\s*", "-");
        
        finalYp = finalYp.replaceAll("\\(([\\-\\+]?[0-9\\.]+)\\)", "$1");
        
        Pattern zeroTerm = Pattern.compile("(\\s*[\\+\\-]\\s*0)");
        finalYp = zeroTerm.matcher(finalYp).replaceAll("");
        finalYp = finalYp.replaceAll("^0\\s*\\+\\s*", "");
        finalYp = finalYp.replaceAll("^\\s*\\+\\s*", "");
        
        finalYp = finalYp.trim().replaceAll("\\s+", " ");

        if (finalYp.isEmpty() || finalYp.matches("^\\s*[\\+\\-]\\s*0$")) return "0";
        
        return finalYp;
    }
}