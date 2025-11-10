package com.ecuaciones.diferenciales.solver.homogeneous;

import com.ecuaciones.diferenciales.model.roots.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/**
 * Resuelve las raíces del polinomio característico a_n*r^n + ... + a_0 = 0.
 * Utiliza métodos analíticos para n<=2 y una aproximación numérica con deflación para n>2.
 * Devuelve una lista de raíces ya consolidadas (incluyendo su multiplicidad).
 */
public class PolynomialSolver {

    private static final double TOLERANCE = 1e-9;
    
    /**
     * Resuelve las raíces del polinomio y maneja la lógica de grado n.
     * @param coeffs Coeficientes del polinomio, desde el término de mayor grado a_n hasta a_0.
     * @return Una lista de objetos Root, cada uno con su multiplicidad.
     */
    public static List<Root> solve(List<Double> coeffs) {
        if (coeffs == null || coeffs.isEmpty()) return Collections.emptyList();

        // 1. Limpiar coeficientes iniciales cero y determinar el grado real
        List<Double> cleanCoeffs = removeLeadingZeros(coeffs);
        int degree = cleanCoeffs.size() - 1;

        if (degree <= 2) {
             // 2. Casos analíticos (Grados 1 y 2)
             List<Root> simpleRoots = solveLowDegree(cleanCoeffs);
             
             // Los casos de bajo grado ya manejan la multiplicidad internamente (ej: r1=r2)
             return simpleRoots; 
        } else {
            // 3. Caso numérico (Grado n > 2): Usar deflación
            List<Root> simpleRoots = solveHigherDegreeNumerically(cleanCoeffs);
            
            // CRÍTICO: El método numérico y de deflación debe devolver raíces simples (multiplicidad 1).
            // Por lo tanto, necesitamos consolidarlas al final, ya que el método analítico lo hizo antes.
            return RootConsolidator.consolidateRoots(simpleRoots);
        }
    }
    
    // --- Métodos de Ayuda para Grados <= 2 ---
    
    private static List<Root> solveLowDegree(List<Double> coefficients) {
        List<Root> roots = new ArrayList<>();
        int degree = coefficients.size() - 1;

        if (degree == 1) {
            double a1 = coefficients.get(0);
            double a0 = coefficients.get(1);
            if (Math.abs(a1) > TOLERANCE) {
                double r = -a0 / a1;
                roots.add(new Root(r, 0.0, 1));
            }
        } else if (degree == 2) {
            double a = coefficients.get(0);
            double b = coefficients.get(1);
            double c = coefficients.get(2);

            double discriminant = b * b - 4 * a * c;

            if (Math.abs(discriminant) < TOLERANCE) {
                // Raíces Reales Repetidas (Multiplicidad 2)
                double r = -b / (2 * a);
                roots.add(new Root(r, 0.0, 2));
            } else if (discriminant > 0) {
                // Raíces Reales Distintas (Multiplicidad 1)
                double sqrtDisc = Math.sqrt(discriminant);
                double r1 = (-b + sqrtDisc) / (2 * a);
                double r2 = (-b - sqrtDisc) / (2 * a);
                roots.add(new Root(r1, 0.0, 1));
                roots.add(new Root(r2, 0.0, 1));
            } else {
                // Raíces Complejas Conjugadas (Solo parte positiva, multiplicidad 1)
                double realPart = -b / (2 * a);
                double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
                
                // Nota: El constructor de Root normaliza el imaginario a su valor absoluto.
                roots.add(new Root(realPart, imaginaryPart, 1)); 
            }
        }
        
        return roots;
    }
    
    // --- Lógica de Grado n > 2 (Numérico con Deflación) ---

    private static List<Root> solveHigherDegreeNumerically(List<Double> coeffs) {
        List<Root> simpleRoots = new ArrayList<>();
        List<Double> currentCoeffs = new ArrayList<>(coeffs);
        
        while (currentCoeffs.size() > 3) { // Mientras el grado sea > 2
            
            // 1. Encontrar una Raíz Numérica (R)
            Root foundRoot = findNumericalRootPlaceholder(currentCoeffs);
            
            // CORRECCIÓN DE ERROR: Usar el constructor de 3 parámetros, asegurando multiplicidad 1.
            simpleRoots.add(new Root(foundRoot.getReal(), foundRoot.getImaginary(), 1)); 
            
            // Si la raíz es compleja, su conjugada también debe ser raíz
            if (!foundRoot.isReal()) {
                 // CORRECCIÓN DE ERROR: Usar el constructor de 3 parámetros, asegurando multiplicidad 1.
                 // Pasamos la parte imaginaria negativa, el constructor de Root se encarga de normalizarla a |beta|.
                 simpleRoots.add(new Root(foundRoot.getReal(), -foundRoot.getImaginary(), 1)); 
                 
                 // Deflación compleja
                 currentCoeffs = simulateComplexDeflation(currentCoeffs);
            } else {
                 // 2. Deflación del Polinomio (División Sintética)
                 currentCoeffs = syntheticDivision(currentCoeffs, foundRoot.getReal());
            }
        }

        // 3. Resolver el Polinomio Reducido (Grado 1 o 2)
        if (currentCoeffs.size() > 1) { 
             // solveLowDegree ya maneja las raíces y sus multiplicidades (que serán 1 o 2)
             simpleRoots.addAll(solveLowDegree(currentCoeffs));
        }
        
        return simpleRoots;
    }
    
    /** Placeholder: Simula la búsqueda de una raíz real o compleja. */
    private static Root findNumericalRootPlaceholder(List<Double> currentCoeffs) {
        // En un sistema real, este algoritmo numérico encontraría la primera raíz (r1).
        // Para este ejemplo, simularemos la raíz r=1.
        System.out.println("DEBUG: [Numérico] Simulación: Raíz encontrada r=1.0 para deflación.");
        return new Root(1.0, 0.0, 1); 
    }
    
    /** Placeholder: Simula la deflación compleja (no implementada completamente). */
    private static List<Double> simulateComplexDeflation(List<Double> coeffs) {
         System.out.println("DEBUG: [Numérico] Simulación: Deflación Compleja, reduciendo grado en 2.");
         // Simula la reducción de grado en 2 (elimina los dos últimos coeficientes)
         return coeffs.subList(0, coeffs.size() - 2); 
    }


    /**
     * Realiza la división sintética P(r) / (x - root) para raíces reales.
     */
    private static List<Double> syntheticDivision(List<Double> coeffs, double root) {
        List<Double> newCoeffs = new ArrayList<>();
        double currentResult = 0.0;
        
        for (int i = 0; i < coeffs.size(); i++) {
            double coeff = coeffs.get(i);
            double nextCoeff = coeff + currentResult;
            
            // Excepto por el último (residuo), agregar a los nuevos coeficientes
            if (i < coeffs.size() - 1) {
                newCoeffs.add(nextCoeff);
                currentResult = nextCoeff * root;
            } else {
                // El último valor es el residuo (debe ser ~0 para una raíz exacta)
                System.out.printf("DEBUG: Residuo de deflación: %.4e\n", nextCoeff);
            }
        }
        return newCoeffs;
    }

    private static List<Double> removeLeadingZeros(List<Double> coeffs) {
        int firstNonZero = 0;
        for (int i = 0; i < coeffs.size() - 1; i++) {
            if (Math.abs(coeffs.get(i)) > TOLERANCE) {
                firstNonZero = i;
                break;
            }
        }
        return coeffs.subList(firstNonZero, coeffs.size());
    }
}