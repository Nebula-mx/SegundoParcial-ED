package com.ecuaciones.diferenciales.model.solver.homogeneous;

import com.ecuaciones.diferenciales.model.roots.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase encargada de agrupar las raíces simples (r_1, r_2, ...) 
 * en una lista final de objetos Root con multiplicidad correcta.
 * * CRÍTICO: Se aprovecha que Root.java normaliza la parte imaginaria (|beta|) en el constructor.
 */
public class RootConsolidator {

    private static final double TOLERANCE = 1e-9;

    /**
     * Procesa una lista de raíces simples (multiplicidad 1) y las consolida por valor.
     * @param simpleRoots Lista de raíces simples (output de PolynomialSolver).
     * @return Lista de raíces consolidadas, cada una con su multiplicidad final.
     */
    public static List<Root> consolidateRoots(List<Root> simpleRoots) {
        if (simpleRoots == null || simpleRoots.isEmpty()) {
            return new ArrayList<>();
        }

        List<Root> consolidated = new ArrayList<>();
        
        // Paso 1: Crear una copia mutable de la lista.
        // Las raíces ya están normalizadas (beta >= 0) gracias al constructor de Root.java.
        List<Root> rootsToProcess = new ArrayList<>(simpleRoots);

        // Paso 2: Agrupar por igualdad (usando tolerancia) y sumar multiplicidad
        
        while (!rootsToProcess.isEmpty()) {
            Root current = rootsToProcess.remove(0);
            int multiplicity = 1;
            
            // Iterar sobre el resto de la lista para encontrar coincidencias
            for (int i = 0; i < rootsToProcess.size(); i++) {
                Root next = rootsToProcess.get(i);
                
                // CRÍTICO: Comprobar igualdad en ambas partes (ya normalizadas a beta >= 0)
                boolean realMatch = Math.abs(current.getReal() - next.getReal()) < TOLERANCE;
                boolean imagMatch = Math.abs(current.getImaginary() - next.getImaginary()) < TOLERANCE;

                if (realMatch && imagMatch) {
                    multiplicity++;
                    rootsToProcess.remove(i); // Remover el duplicado
                    i--; // Ajustar el índice debido a la remoción
                }
            }
            
            // Añadir la raíz consolidada
            consolidated.add(new Root(current.getReal(), current.getImaginary(), multiplicity));
        }

        return consolidated;
    }
}