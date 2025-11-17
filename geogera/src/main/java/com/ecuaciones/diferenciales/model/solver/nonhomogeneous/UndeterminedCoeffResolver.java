package com.ecuaciones.diferenciales.model.solver.nonhomogeneous;

import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.utils.LinearSystemSolver;
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;
import com.ecuaciones.diferenciales.utils.SymjaEngine;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Resuelve los coeficientes de la forma y_p* (A, B, C, ...) y genera el sistema lineal A|b.
 * Utiliza listas separadas para las filas (base) y las columnas (propuesta y_p*).
 */
public class UndeterminedCoeffResolver {

    private final ExpressionData data;
    // --- LISTAS CORREGIDAS ---
    private final List<String> baseUCTerms; // Para FILAS (i) y vector b
    private final List<String> ypStarTerms; // Para COLUMNAS (j)
    // -------------------------
    private final List<String> coeffNames; 
    private final Double[] coefficients; 
    private final int order;
    private final String gX;
    
    private static final double TOLERANCE = 1e-9;

    public UndeterminedCoeffResolver(ExpressionData data, UndeterminedCoeff ucSolver) {
        this.data = data;
        this.coefficients = data.getCoefficients();
        this.order = data.getOrder();
        this.gX = data.getIndependentTerm().get("g(x)");
        
        // --- ASIGNACIÓN CORREGIDA ---
        this.coeffNames = ucSolver.getCoeffNames(); 
        this.baseUCTerms = ucSolver.getBaseUCTerms(); // Términos Base (Filas)
        this.ypStarTerms = ucSolver.getYpStarTerms(); // Términos Propuestos (Columnas)
    }
    
    // ----------------------- MÉTODO PRINCIPAL DE RESOLUCIÓN --------------------------

    public Map<String, Double> resolveCoefficients() {
        
        int numCoeffs = coeffNames.size(); // Nombres (A, B...)
        int numCols = ypStarTerms.size();  // Columnas (Términos Yp*)
        int numRows = baseUCTerms.size();  // Filas (Términos Base)
        
        // El número de Coeficientes (A, B) debe ser igual al número de Términos de Columna (yp*)
        // El número de Filas (Términos Base) debe ser igual al número de Columnas (Términos Yp*)
        if (numCoeffs != numCols || numRows != numCols || numCoeffs == 0) {
             throw new IllegalStateException(
                 "Error: El sistema A|b está mal construido. Las dimensiones no coinciden. \n" +
                 "Coeficientes (Nombres): " + numCoeffs + "\n" +
                 "Términos Yp* (Columnas): " + numCols + "\n" +
                 "Términos Base (Filas): " + numRows
             );
        }

        double[][] matrixA = new double[numRows][numCols];
        double[] vectorB = new double[numRows];
        
        // 3. Llenar la Matriz A y el Vector B
        // i = Fila (Término Base)
        for (int i = 0; i < numRows; i++) { 
            String baseTerm_i = baseUCTerms.get(i); // Ej: cos(2x)
            
            // j = Columna (Término Yp*)
            for (int j = 0; j < numCols; j++) {
                String ypTerm_j = ypStarTerms.get(j); // Ej: x*cos(2x)

                // Construir la suma simbólica Σ a_k * coeff_k(x) como expresión Symja
                StringBuilder symbolicSum = new StringBuilder("0");

                // Aplicar el operador L[ypTerm_j]
                for (int k = 0; k <= order; k++) {
                    double a_k = coefficients[order - k];

                    String derived_tj = SymbolicDifferentiator.calculateDerivative(ypTerm_j, k);
                    // Expandir antes de extraer coeficiente
                    String expanded_derived = SymbolicDifferentiator.expand(derived_tj);

                    // Extraer el coeficiente SIMBÓLICO del término base (como expresión Symja)
                    String coeffExpr = SymjaEngine.extractCoefficientExpr(expanded_derived, baseTerm_i);
                    if (coeffExpr == null || coeffExpr.equals("0")) continue;

                    // Añadir a la suma: a_k * (coeffExpr)
                    symbolicSum.append(" + (").append(a_k).append(")*(").append(coeffExpr).append(")");
                }

                // Simplificar la suma y tratar de evaluarla numéricamente
                double totalVal = SymjaEngine.simplifyAndEvalDouble(symbolicSum.toString());
                if (Double.isNaN(totalVal)) {
                    // No se pudo reducir a constante; UC no es aplicable aquí
                    throw new IllegalStateException("Coeficiente simbólico no numérico en UC; fallback a VP requerido");
                }

                matrixA[i][j] = totalVal;
            }
            
            // Llenar el Vector B
            // Extraer el coeficiente simbólico del término base (FILA i) de g(x) y evaluar
            String bCoeffExpr = SymjaEngine.extractCoefficientExpr(gX, baseTerm_i);
            double bVal = SymjaEngine.simplifyAndEvalDouble(bCoeffExpr);
            if (Double.isNaN(bVal)) {
                throw new IllegalStateException("Término independiente contiene simbolos respecto a base; fallback a VP requerido");
            }
            vectorB[i] = bVal;
        }
        
        // DEBUG: mostrar matriz antes de resolver
        System.out.println("\n   [DEBUG RESOLVER] Matriz A (" + numRows + "x" + numCols + "):");
        for (int row = 0; row < numRows; row++) {
            System.out.print("      [");
            for (int col = 0; col < numCols; col++) {
                System.out.printf("%.3f ", matrixA[row][col]);
            }
            System.out.printf("| %.3f]\n", vectorB[row]);
        }
        
        // 4. Resolver el sistema lineal
        List<List<Double>> listA = new ArrayList<>();
        for (double[] row : matrixA) {
            List<Double> listRow = new ArrayList<>();
            for (double val : row) listRow.add(val);
            listA.add(listRow);
        }
        List<Double> listB = new ArrayList<>();
        for (double val : vectorB) listB.add(val);
        
        double[] solutions = LinearSystemSolver.solve(listA, listB);

        // 5. Mapear resultados
        Map<String, Double> solvedCoeffs = new HashMap<>();
        for (int idx = 0; idx < solutions.length; idx++) {
            double solution = Math.abs(solutions[idx]) < TOLERANCE ? 0.0 : solutions[idx]; 
            solvedCoeffs.put(coeffNames.get(idx), solution);
        }
        
        return solvedCoeffs;
    }
    
    public List<String> getSolvedCoeffNames() {
        return coeffNames;
    }
}

