package com.ecuaciones.diferenciales.model.solver.nonhomogeneous;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.util.Locale;

import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;

/**
 * Clase para resolver la EDO usando el método de Variación de Parámetros (VdP),
 * generalizado para EDOs de orden 'n' con coeficientes constantes.
 */
public class VariationOfParametersSolver {

    private final List<String> yFunctions;
    private final String gX;
    private final double leadingCoeff; // Coeficiente principal (a_n)
    private final int order; 
    private final WronskianCalculator wc; 
    
    private static final double TOLERANCE = 1e-9;

    public VariationOfParametersSolver(List<String> yFuncs, String nonHomogeneousTerm, double leadingCoeff, int order, WronskianCalculator wc) {
        this.yFunctions = yFuncs;
        this.gX = nonHomogeneousTerm.trim().replaceAll("[\\{\\}]", ""); // Limpieza inicial de g(x)
        this.leadingCoeff = leadingCoeff; 
        this.order = order;
        this.wc = wc;
        
        if (yFuncs.size() != order) {
             System.err.println("ADVERTENCIA: El número de funciones base (" + yFuncs.size() + ") no coincide con el orden de la EDO (" + order + ").");
        }
    }

    private List<List<String>> generateWronskianMatrixW() {
        // Asumimos que WronskianCalculator puede generar matrices con derivadas simbólicas
        return wc.generateWronskianMatrix(yFunctions, order);
    }
    
    /**
     * Genera la matriz W_i para la regla de Cramer, reemplazando la columna 'i' 
     * con el vector (0, 0, ..., f(x)), donde f(x) = g(x) / a_n.
     */
    private List<List<String>> generateCramerMatrixWi(List<List<String>> W, int columnIndex, String fX) {
        List<List<String>> Wi = new ArrayList<>();
        // Copiar la matriz W
        for (List<String> row : W) {
            Wi.add(new ArrayList<>(row));
        }

        // Vector de sustitución: (0, 0, ..., f(x))
        for (int i = 0; i < order; i++) {
            if (i < order - 1) {
                // Fila 1 hasta n-1: el elemento de la columna es 0
                Wi.get(i).set(columnIndex, "0"); 
            } else {
                // Última fila (n): el elemento de la columna es f(x)
                Wi.get(i).set(columnIndex, fX);
            }
        }
        return Wi;
    }

    public String formulateVdpSolution() {
        if (order < 2) {
             return "ERROR: VP solo aplica a EDOs de orden >= 2.";
        }

        // --- PASO CRÍTICO: Normalizar g(x) a f(x) = g(x) / a_n ---
        String fX;
        if (Math.abs(leadingCoeff - 1.0) < TOLERANCE) {
             fX = gX;
        } else {
             // f(x) = g(x) / a_n. Se utiliza la forma de división simbólica
             fX = "("+ gX + ") / " + String.valueOf(this.leadingCoeff);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- 🛠️ Variación de Parámetros (Orden ").append(order).append(") ---\n");
        sb.append("Funciones Base (y_i): ").append(yFunctions).append("\n");
        sb.append("Término Normalizado (f(x) = g(x)/a_n): ").append(fX).append("\n");
        
        List<List<String>> WMatrix = generateWronskianMatrixW();
        // Asume que calculateWronskianFormula solo regresa la fórmula del determinante
        String WronskianFormula = this.wc.calculateWronskianFormula(WMatrix); 
        sb.append("Wronskiano W: det(W) = ").append(WronskianFormula).append("\n\n");
        
        List<String> ypTerms = new ArrayList<>();
        
        for (int i = 0; i < order; i++) {
            // Generar W_i usando f(x) normalizado
            List<List<String>> WiMatrix = generateCramerMatrixWi(WMatrix, i, fX);
            
            // Asumir que el cálculo de W_i retorna una fórmula simbólica
            String WiFormula = this.wc.calculateWronskianFormula(WiMatrix);
            
            // u_i'(x) = W_i(x) / W(x)
            String uPrimeFormula = "(" + WiFormula + ") / (" + WronskianFormula + ")";

            sb.append("| Cálculo para u").append(i + 1).append(":\n");
            sb.append("| Matriz W_").append(i + 1).append("(x):\n");
            // Mostrar la matriz Wi (ej: línea a línea)
            for (List<String> row : WiMatrix) {
                 sb.append("|   ").append(row).append("\n");
            }
            sb.append("| u").append(i + 1).append("'(x) = W_").append(i + 1).append("(x) / W(x)\n");
            sb.append("| u").append(i + 1).append("'(x) = ").append(uPrimeFormula).append("\n");
            sb.append("| u").append(i + 1).append("(x) = ∫ u").append(i + 1).append("'(x) dx\n\n");
            
            // La solución final y_p es la suma de u_i(x) * y_i(x)
            String integrationPlaceholder = "∫ (" + uPrimeFormula + ") dx";
            String yTerm = "(" + integrationPlaceholder + ") * (" + yFunctions.get(i).trim() + ")";
            ypTerms.add(yTerm);
        }

        String yp = String.join(" + ", ypTerms);

        return sb.toString() + "\n==============================================\n" + 
               "          Solución Particular (y_p)          \n" + 
               "==============================================\n" + 
               "y_p(x) = " + yp;
    }
}