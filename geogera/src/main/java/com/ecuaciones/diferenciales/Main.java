package com.ecuaciones.diferenciales;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolver;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;

public class Main{
    
    public static void main(String[] args) {
        EcuationParser parser = new EcuationParser(); 
        ExpressionData data = null; 
        
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("--- Ecuaciones Diferenciales. Proyecto Solver ---");
            System.out.print("Ingresa una ecuación (Ej: y'' + 4y = 8cos(2x)): ");
            String ecuacion = scanner.nextLine().toLowerCase();

            if (!esEcuacionDiferencial(ecuacion)) {
                System.out.println("La ecuación ingresada NO es una ecuación diferencial.");
                return;
            }
            
            // 1. Parsear la ecuación y extraer datos
            data = parser.parse(ecuacion);
            
            Double[] coeffsArray = data.getCoefficients(); 
            int order = data.getOrder();
            
            if (coeffsArray == null || coeffsArray.length == 0 || order <= 0) {
                System.out.println("ERROR: No se pudo extraer el polinomio característico o el orden es incorrecto.");
                return;
            }
            
            // Convertir Double[] a List<Double> para PolynomialSolver
            List<Double> coeffs = Arrays.asList(coeffsArray);

            System.out.print("\nDEBUG Coeficientes (an, an-1, ... a0): ");
            // Imprime los coeficientes para depuración
            System.out.println(Arrays.toString(coeffsArray)); 
            System.out.println("Orden de la EDO: " + order);
            System.out.println("----------------------------------------------------");
            
            System.out.println("--- 2. SOLUCIÓN HOMOGÉNEA (y_h) ---");
            
            // 1. Resolver raíces y generar y_h
            List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            System.out.println("1. Raíces del Polinomio Característico (r):");
            finalRoots.forEach(r -> System.out.println("   -> " + r));

            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            System.out.println("\n2. Solución Homogénea (y_h):");
            System.out.println("y_h(x) = " + solution_h);

            // --- FASE DE SOLUCIÓN PARTICULAR (y_p) ---
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                String solution_p = "";

                System.out.println("\n==============================================");
                System.out.println("--- Término no homogéneo g(x): " + g_x + " ---");
                System.out.println("==============================================");
                
                System.out.println("--- Seleccionar Método para y_p(x) ---");
                System.out.println("1. Coeficientes Indeterminados (UC)");
                System.out.println("2. Variación de Parámetros (VP)");
                System.out.print("Selecciona una opción (1 o 2): ");
                String opcion = scanner.nextLine();

                if ("1".equals(opcion)) {
                    // --- Método 1: Coeficientes Indeterminados (UC) ---
                    System.out.println("\n--- Usando Coeficientes Indeterminados (UC) ---");
                    
                    try {
                        // 1. Generar la forma (y el ucSolver la almacena)
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(finalRoots);
                        
                        String ypForm = ucSolver.getParticularSolutionForm(g_x); 
                        System.out.println("3. Forma Propuesta (y_p*): " + ypForm);
                        
                        List<String> ypCoeffNames = ucSolver.getCoeffNames(); 
                        System.out.println("4. Coeficientes a Resolver: " + ypCoeffNames);
                        
                        // 2. Instanciar el Resolver
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver); 
                        
                        // 3. Resolver el sistema A|b
                        Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients(); 
                        System.out.println("5. Coeficientes Resueltos: " + solvedCoeffs);
                        
                        // 4. Generar la solución final
                        solution_p = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        
                    } catch (ArithmeticException e) {
                         // Captura el error de sistema singular de LinearSystemSolver
                         System.err.println("\nERROR UC: El sistema A|b es singular. Causa probable: Resonancia no detectada o error de cálculo.");
                         System.err.println("Mensaje: " + e.getMessage());
                         solution_p = "ERROR: Sistema lineal singular. Revise la forma y_p* o intente VP.";
                    } catch (Exception e) {
                         System.err.println("\nERROR UC: Ocurrió un error inesperado al aplicar Coeficientes Indeterminados.");
                         System.err.println("Mensaje: " + e.getMessage());
                         solution_p = "ERROR: Fallo en UC.";
                    }

                } else if ("2".equals(opcion)) {
                    // --- Método 2: Variación de Parámetros (VP) ---
                    System.out.println("\n--- Usando Variación de Parámetros (VP) ---");
                    
                    if (order < 2) {
                        System.err.println("VP solo aplica a EDOs de orden >= 2.");
                        solution_p = "VP: Orden no soportado (n < 2)";
                    } else {
                        WronskianCalculator wc = new WronskianCalculator(finalRoots);
                        List<String> yFunctions = wc.generateFundamentalSet(); 
                        
                        // El coeficiente principal (a_n) es el primero en coeffsArray
                        double leadingCoeff = coeffsArray[0]; 
                        
                        VariationOfParametersSolver vpSolver = new VariationOfParametersSolver(yFunctions, g_x, leadingCoeff, order, wc);
                        String vpSteps = vpSolver.formulateVdpSolution();
                        
                        System.out.println(vpSteps);
                        // Mensaje claro: el proyecto solo llega hasta la formulación, no integra
                        solution_p = "Fórmulas de u_i(x) generadas (Ver arriba). No se realiza integración simbólica.";
                    }
                    
                } else {
                    System.err.println("Opción no válida. Se omite el cálculo de y_p.");
                    solution_p = ""; 
                }

                System.out.println("\n==============================================");
                System.out.println("         Solución Particular (y_p)            ");
                System.out.println("==============================================");
                System.out.println("y_p(x) = " + solution_p);
                
                // --- Ensamblaje de la Solución General ---
                String final_p = solution_p.trim();
                String final_h = solution_h.trim();
                String solution_final = final_h;
                
                // Solo concatenar si y_p es válido, no es un mensaje de error o solo la fórmula VP
                if (!final_p.isEmpty() && !final_p.startsWith("ERROR") && !final_p.contains("Fórmulas")) {
                    
                    // Elimina '0' o '0.0' si y_p es solo eso
                    if (!final_p.matches("^[\\+\\-]?\\s*0(\\.0+)?$")) { 
                        
                        // Simplificación: Limpiar el '+' inicial si existe
                        String clean_p = final_p.replaceAll("^\\+", "").trim();

                        // Concatenar: si y_h termina en un espacio, si y_p empieza con '-', concatenar directo
                        if (clean_p.startsWith("-")) {
                            solution_final += clean_p;
                        } else {
                            // Si no empieza con '-', añadir un '+'
                            solution_final += " + " + clean_p;
                        }
                    }
                }
                
                System.out.println("\n==============================================");
                System.out.println("         SOLUCIÓN GENERAL FINAL               ");
                System.out.println("==============================================");
                System.out.println("y(x) = y_h(x) + y_p(x)");
                System.out.println("y(x) = " + solution_final.trim());

            } else {
                System.out.println("\n==============================================");
                System.out.println("SOLUCIÓN FINAL (Homogénea): y(x) = " + solution_h);
                System.out.println("==============================================");
            }
            
        } catch (Exception e) {
            System.err.println("\nOcurrió un error crítico durante la ejecución: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    // --- MÉTODOS AUXILIARES ---
    /**
     * Verifica de forma simple si la cadena de entrada contiene notación de derivada o 'y'.
     */
    public static boolean esEcuacionDiferencial(String ecuacion) {
        // Patrones de derivada (y', y'', d2y/dx2, etc.)
        String[] derivativePatterns = { "dy/dx", "d2y/dx2", "y'", "y''", "y'''" }; 
        for (String pattern : derivativePatterns) {
            if (ecuacion.contains(pattern)) return true;
        }
        
        // Patrón para verificar la existencia del término 'y' (con o sin coeficiente) y el signo '='
        // Esto cubre y'' + 4y = ...
        if (ecuacion.contains("y") && ecuacion.contains("=")) return true;

        return false;
    }
}