package com.ecuaciones.diferenciales.model.variation;

import com.ecuaciones.diferenciales.model.roots.Root; 
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator; 
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.text.DecimalFormat;

/**
 * Clase encargada de generar el Conjunto Fundamental de Soluciones (CFS) 
 * y la matriz del Wronskiano W, utilizando el diferenciador simbólico para las derivadas.
 */
public class WronskianCalculator {

    private final List<Root> finalRoots;
    private static final DecimalFormat DF = new DecimalFormat("#.####", new java.text.DecimalFormatSymbols(Locale.US));
    private static final double TOLERANCE = 1e-9;

    public WronskianCalculator(List<Root> finalRoots) {
        this.finalRoots = finalRoots;
    }

    private String formatCoeff(double coeff) {
        if (Math.abs(coeff - Math.round(coeff)) < TOLERANCE) {
            return String.valueOf((int) Math.round(coeff));
        }
        return String.format(Locale.US, "%.4f", coeff);
    }
    
    // ----------------------- generateFundamentalSet --------------------------

    public List<String> generateFundamentalSet() {
        List<String> yFunctions = new ArrayList<>();
        
        for (Root root : finalRoots) {
            double alpha = root.getReal();
            double beta = root.getImaginary();
            int m = root.getMultiplicity();
            
            // 1. Componente exponencial e^(alpha*x)
            String expTerm = "";
            if (Math.abs(alpha) > TOLERANCE) {
                 String alphaStr = (Math.abs(alpha - 1.0) < TOLERANCE) ? "x" : formatCoeff(alpha) + "x";
                 expTerm = "e^" + alphaStr;
            }
            
            // 2. Generar m términos para la multiplicidad
            for (int k = 0; k < m; k++) {
                // Genera x^k
                String xTerm = (k > 0) ? ((k == 1) ? "x" : "x^" + k) : "";
                
                // --- Construcción de la base y_i ---
                
                if (Math.abs(beta) < TOLERANCE) { // Caso Raíces Reales (y = x^k * e^ax)
                    
                    String term = xTerm;
                    if (!expTerm.isEmpty()) {
                        term = term.isEmpty() ? expTerm : term + "*" + expTerm;
                    } else if (term.isEmpty()) {
                        term = "1"; // Caso raíz r=0, m=1 (y=1)
                    }
                    yFunctions.add(term);
                    
                } else { // Caso Raíces Complejas (y = x^k * e^ax * cos/sin(Bx))
                    
                    double absBeta = Math.abs(beta);
                    String betaX = formatCoeff(absBeta) + "x";
                    
                    // Factor de producto: x^k * e^ax
                    String factor = xTerm;
                    if (!expTerm.isEmpty()) {
                        factor = factor.isEmpty() ? expTerm : factor + "*" + expTerm;
                    }
                    String factorSuffix = factor.isEmpty() ? "" : "*" + factor;

                    // Términos para el CFS (y_cos y y_sin)
                    String cosTerm = "cos(" + betaX + ")" + factorSuffix;
                    String sinTerm = "sin(" + betaX + ")" + factorSuffix;
                    
                    // Asegurar la limpieza y añadir
                    yFunctions.add(cosTerm.replaceAll("\\*\\*", "*").trim());
                    yFunctions.add(sinTerm.replaceAll("\\*\\*", "*").trim());
                }
            }
        }
        
        // Limpieza final para asegurar que SymbolicDifferentiator reciba un formato limpio.
        return yFunctions.stream()
                         .map(s -> s.replaceAll("\\*\\*", "*")) 
                         .map(s -> s.replaceAll("\\s+", "")) 
                         .map(String::trim)
                         .collect(Collectors.toList());
    }
    
    // ----------------------- generateWronskianMatrix --------------------------

    public List<List<String>> generateWronskianMatrix(List<String> fundamentalSet, int order) {
        int n = fundamentalSet.size();
        if (n == 0) return new ArrayList<>();
        if (order != n) order = n; 

        List<List<String>> matrix = new ArrayList<>();
        
        for (int i = 0; i < order; i++) { // i = orden de la derivada (0 a n-1)
            List<String> row = new ArrayList<>();
            for (String y_i : fundamentalSet) { // y_i = función base
                
                if (i == 0) {
                    row.add(y_i); // y^(0) = y
                } else {
                    // Usa el diferenciador simbólico para la derivada i
                    row.add(SymbolicDifferentiator.calculateDerivative(y_i, i)); 
                }
            }
            matrix.add(row);
        }
        return matrix;
    }

    // ----------------------- CÁLCULO SIMBÓLICO DEL DETERMINANTE --------------------------

    /**
     * Retorna la fórmula simbólica del determinante W (o W_i).
     * Usa expansión por cofactores con manipulación de Strings.
     */
    public String calculateWronskianFormula(List<List<String>> WMatrix) {
        int n = WMatrix.size();
        if (n == 0) return "0";
        if (n == 1) return WMatrix.get(0).get(0);
        
        // Caso Base 2x2: a*d - b*c
        if (n == 2) {
            String a = WMatrix.get(0).get(0);
            String b = WMatrix.get(0).get(1);
            String c = WMatrix.get(1).get(0);
            String d = WMatrix.get(1).get(1);
            
            String term1 = wrap(a) + " * " + wrap(d);
            String term2 = wrap(b) + " * " + wrap(c);

            return simplifyFormula(term1 + " - " + term2);
        }

        // Caso General (n >= 3): Expansión por cofactores (Fila 0)
        StringBuilder det = new StringBuilder();
        for (int j = 0; j < n; j++) {
            String element = WMatrix.get(0).get(j);
            String sign = (j % 2 == 0) ? "+" : "-";
            
            // Crea la submatriz (menor)
            List<List<String>> submatrix = getSubmatrixFormula(WMatrix, 0, j);
            
            // Llama recursivamente
            String subDetFormula = calculateWronskianFormula(submatrix);
            
            // Solo agregar términos si el elemento no es '0'
            if (!element.equals("0")) {
                 if (det.length() == 0) sign = (sign.equals("+") ? "" : "-");
                 
                 det.append(" ").append(sign).append(" ")
                    .append(wrap(element)).append(" * ")
                    .append(wrap(subDetFormula));
            }
        }

        return simplifyFormula(det.toString().trim());
    }

    /**
     * Crea la submatriz (menor) M_ij eliminando la fila i y la columna j.
     */
    private List<List<String>> getSubmatrixFormula(List<List<String>> matrix, int rowToRemove, int colToRemove) {
        List<List<String>> submatrix = new ArrayList<>();
        
        for (int i = 0; i < matrix.size(); i++) {
            if (i == rowToRemove) continue;

            List<String> newRow = new ArrayList<>();
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if (j == colToRemove) continue;
                newRow.add(matrix.get(i).get(j));
            }
            submatrix.add(newRow);
        }
        return submatrix;
    }
    
    // --- Utilidades de Formato ---

    /** Envuelve una expresión en paréntesis si contiene operadores (+, -, *) */
    private String wrap(String s) {
        s = s.trim();
        // Usamos " * " (con espacios) para diferenciarlo de la multiplicación implícita en las funciones (ej: 2*x)
        if (s.contains("+") || s.contains("-") || s.contains(" * ")) {
            return "(" + s + ")";
        }
        return s;
    }
    
    /** Limpia la fórmula final: +- -> -, elimina + inicial */
    private String simplifyFormula(String formula) {
        return formula.replaceAll("\\s*\\-\\s*\\-\\s*", " + ")
                      .replaceAll("\\s*\\+\\s*\\-\\s*", " - ")
                      .replaceAll("^\\s*\\+\\s*", "")
                      .trim();
    }
}