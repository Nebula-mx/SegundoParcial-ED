package com.ecuaciones.diferenciales.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para operaciones matriciales, crucial para la generalización a orden n.
 * Implementa el cálculo del determinante usando el método de expansión por cofactores (recursivo).
 */
public class MatrixSolver {

    /**
     * Calcula el determinante de una matriz cuadrada.
     * @param matrix La matriz de números (List<List<Double>>).
     * @return El valor del determinante.
     */
    public static double determinant(List<List<Double>> matrix) {
        int n = matrix.size();
        if (n == 0) return 0.0;
        if (n == 1) return matrix.get(0).get(0);
        
        // Caso base 2x2: ad - bc
        if (n == 2) {
            return matrix.get(0).get(0) * matrix.get(1).get(1) - 
                   matrix.get(0).get(1) * matrix.get(1).get(0);
        }

        double det = 0;
        // Expansión por cofactores (usando la primera fila)
        for (int j = 0; j < n; j++) {
            // El elemento es matrix.get(0).get(j)
            // El signo es (-1)^(0+j)
            double sign = (j % 2 == 0) ? 1.0 : -1.0;
            
            // Crea la submatriz (menor)
            List<List<Double>> submatrix = getSubmatrix(matrix, 0, j);
            
            // Suma al determinante: (-1)^(i+j) * a_ij * det(M_ij)
            det += sign * matrix.get(0).get(j) * determinant(submatrix);
        }

        return det;
    }

    /**
     * Crea la submatriz (menor) M_ij eliminando la fila i y la columna j.
     * @param matrix Matriz original.
     * @param rowToRemove Fila a eliminar.
     * @param colToRemove Columna a eliminar.
     * @return La nueva submatriz.
     */
    public static List<List<Double>> getSubmatrix(List<List<Double>> matrix, int rowToRemove, int colToRemove) {
        List<List<Double>> submatrix = new ArrayList<>();
        
        for (int i = 0; i < matrix.size(); i++) {
            if (i == rowToRemove) continue;

            List<Double> newRow = new ArrayList<>();
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if (j == colToRemove) continue;
                newRow.add(matrix.get(i).get(j));
            }
            submatrix.add(newRow);
        }
        return submatrix;
    }
}