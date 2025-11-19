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

        // Paso 2: Agrupar raíces reales por valor y agrupar pares complejos conjugados en UNA entrada
        while (!rootsToProcess.isEmpty()) {
            Root current = rootsToProcess.remove(0);
            double curReal = current.getReal();
            double curImag = current.getImaginary();
            int multiplicity = current.getMultiplicity();

            // Si es raíz real (imag ~= 0), agrupar todas las raíces reales iguales
            if (Math.abs(curImag) < TOLERANCE) {
                for (int i = 0; i < rootsToProcess.size(); i++) {
                    Root next = rootsToProcess.get(i);
                    if (Math.abs(next.getImaginary()) < TOLERANCE && Math.abs(curReal - next.getReal()) < TOLERANCE) {
                        multiplicity += next.getMultiplicity();
                        rootsToProcess.remove(i);
                        i--;
                    }
                }
                consolidated.add(new Root(curReal, 0.0, multiplicity));
            } else {
                // Raíz compleja: buscar conjugado (misma parte real, parte imaginaria con signo opuesto)
                int conjugateIndex = -1;
                for (int i = 0; i < rootsToProcess.size(); i++) {
                    Root next = rootsToProcess.get(i);
                    if (Math.abs(curReal - next.getReal()) < TOLERANCE && Math.abs(curImag + next.getImaginary()) < TOLERANCE) {
                        conjugateIndex = i;
                        // Para pares conjugados, la multiplicidad algebraica del par es la misma
                        // en ambos elementos; tomar el máximo evita duplicar (sumar) contadores.
                        multiplicity = Math.max(multiplicity, next.getMultiplicity());
                        break;
                    }
                }

                // Si encontramos el conjugado, removerlo
                if (conjugateIndex >= 0) {
                    rootsToProcess.remove(conjugateIndex);
                }

                // Normalizar la parte imaginaria a su magnitud positiva para la representación canónica
                consolidated.add(new Root(curReal, Math.abs(curImag), multiplicity));
            }
        }

        return consolidated;
    }
}