package com.ecuaciones.diferenciales.utils;

import java.util.List;
import java.util.ArrayList;

/**
 * Resuelve un sistema de ecuaciones lineales usando la Regla de Cramer.
 * Requiere la clase MatrixSolver para el cálculo de determinantes.
 */
public class LinearSystemSolver {

    private static final double TOLERANCE = 1e-9;

    /**
     * Resuelve el sistema Ax = b usando la Regla de Cramer.
     * @param A Matriz de coeficientes (List<List<Double>>).
     * @param b Vector de términos independientes (List<Double>).
     * @return Arreglo de soluciones x (double[]).
     * @throws ArithmeticException Si el determinante de A es cero (sistema singular o indeterminado).
     */
    public static double[] solve(List<List<Double>> A, List<Double> b) {
        int n = A.size();
        if (n == 0) return new double[0];

        // 1. Calcular el determinante de A
        double detA = MatrixSolver.determinant(A);

        if (Math.abs(detA) < TOLERANCE) {
            throw new ArithmeticException("El sistema de ecuaciones es singular o indeterminado.");
        }

        double[] solutions = new double[n];

        // 2. Calcular el determinante de A_j y resolver x_j
        for (int j = 0; j < n; j++) {
            // Crea la matriz A_j sustituyendo la columna j con el vector b
            List<List<Double>> Aj = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                List<Double> row = new ArrayList<>(A.get(i));
                row.set(j, b.get(i)); // Sustituye el elemento en la columna j
                Aj.add(row);
            }

            double detAj = MatrixSolver.determinant(Aj);
            
            // Regla de Cramer: x_j = det(A_j) / det(A)
            solutions[j] = detAj / detA;
        }

        return solutions;
    }
}