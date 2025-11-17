package com.ecuaciones.diferenciales.utils;

import java.util.List;

/**
 * Resuelve un sistema de ecuaciones lineales usando Eliminación Gaussiana con Pivoteo (Gauss-Jordan).
 * Maneja sistemas singulares, indeterminados y cualquier matriz Ax = b.
 */
public class LinearSystemSolver {

    private static final double TOLERANCE = 1e-10;

    /**
     * Resuelve el sistema Ax = b usando Eliminación Gaussiana (Gauss-Jordan).
     * Funciona para cualquier tipo de sistema (determinado, indeterminado o singular).
     * 
     * @param A Matriz de coeficientes (List<List<Double>>).
     * @param b Vector de términos independientes (List<Double>).
     * @return Arreglo de soluciones x (double[]).
     * @throws ArithmeticException Si el sistema es inconsistente (sin solución).
     */
    public static double[] solve(List<List<Double>> A, List<Double> b) {
        int n = A.size();
        if (n == 0) return new double[0];
        int m = A.get(0).size();

        // Crear matriz aumentada [A | b]
        double[][] augmented = new double[n][m + 1];
        
        for (int i = 0; i < n; i++) {
            List<Double> row = A.get(i);
            for (int j = 0; j < m; j++) {
                augmented[i][j] = row.get(j);
            }
            augmented[i][m] = b.get(i);
        }

        // Aplicar Gauss-Jordan con pivoteo parcial
        gaussJordanPivoting(augmented);

        // Extraer soluciones usando sustitución hacia atrás
        // Después de Gauss-Jordan, la matriz está en forma escalonada
        // Para cada variable, buscamos el renglón donde aparece como pivote (1 en diagonal)
        double[] solutions = new double[m];
        
        for (int j = 0; j < m; j++) {
            // Encontrar la fila donde la columna j es el pivote (valor 1)
            int pivotRow = -1;
            for (int i = 0; i < n; i++) {
                if (Math.abs(augmented[i][j] - 1.0) < TOLERANCE) {
                    // Verificar que es la única columna no-cero en esta fila (hasta j)
                    boolean isPivot = true;
                    for (int k = 0; k < j; k++) {
                        if (Math.abs(augmented[i][k]) > TOLERANCE) {
                            isPivot = false;
                            break;
                        }
                    }
                    if (isPivot) {
                        pivotRow = i;
                        break;
                    }
                }
            }
            
            if (pivotRow != -1) {
                // La solución es el término independiente de la fila del pivote
                solutions[j] = augmented[pivotRow][m];
            } else {
                // Variable libre (o no aparece como pivote)
                solutions[j] = 0.0;
            }
        }

        return solutions;
    }

    /**
     * Eliminación Gaussiana con pivoteo parcial (Gauss-Jordan).
     * Convierte la matriz a forma escalonada reducida.
     */
    private static void gaussJordanPivoting(double[][] augmented) {
        int n = augmented.length;      // número de ecuaciones
        int m = augmented[0].length - 1; // número de variables

        int currentRow = 0;
        
        for (int col = 0; col < m && currentRow < n; col++) {
            // 1. Encontrar el pivote (elemento con mayor valor absoluto en la columna)
            int pivotRow = currentRow;
            double maxVal = Math.abs(augmented[currentRow][col]);
            
            for (int i = currentRow + 1; i < n; i++) {
                if (Math.abs(augmented[i][col]) > maxVal) {
                    maxVal = Math.abs(augmented[i][col]);
                    pivotRow = i;
                }
            }

            // Si el pivote es cero, la columna es dependiente, pasar a la siguiente
            if (Math.abs(augmented[pivotRow][col]) < TOLERANCE) {
                continue;
            }

            // 2. Intercambiar filas si es necesario
            if (pivotRow != currentRow) {
                swapRows(augmented, currentRow, pivotRow);
            }

            // 3. Normalizar la fila del pivote (hacer que el pivote sea 1)
            double pivot = augmented[currentRow][col];
            for (int j = col; j <= m; j++) {
                augmented[currentRow][j] /= pivot;
            }

            // 4. Eliminar la columna en todas las otras filas (incluyendo arriba)
            for (int i = 0; i < n; i++) {
                if (i != currentRow) {
                    double factor = augmented[i][col];
                    for (int j = col; j <= m; j++) {
                        augmented[i][j] -= factor * augmented[currentRow][j];
                    }
                }
            }

            currentRow++;
        }

        // Verificar inconsistencia: si hay una fila con [0 0 ... 0 | b] donde b != 0
        for (int i = 0; i < n; i++) {
            boolean allZero = true;
            for (int j = 0; j < m; j++) {
                if (Math.abs(augmented[i][j]) > TOLERANCE) {
                    allZero = false;
                    break;
                }
            }
            if (allZero && Math.abs(augmented[i][m]) > TOLERANCE) {
                throw new ArithmeticException(
                    "⚠️ Sistema inconsistente: fila " + i + " tiene todos ceros pero término independiente ≠ 0. Sin solución.");
            }
        }
    }

    /**
     * Intercambia dos filas de la matriz.
     */
    private static void swapRows(double[][] matrix, int row1, int row2) {
        double[] temp = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = temp;
    }
}