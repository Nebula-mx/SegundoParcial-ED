package com.ecuaciones.diferenciales;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.ArrayList;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;

public class Main{
    
    public static void main(String[] args) {
        
        EcuationParser parser = new EcuationParser(); 
        ExpressionData data = null; 
        
        // Parsear argumentos de línea de comandos
        String ecuacion = null;
        String metodoSeleccionado = "UC"; // Por defecto UC
        List<String> condicionesIniciales = new ArrayList<>();
        
        // Si hay argumentos, usarlos; si no, solicitar interactivamente
        if (args.length > 0) {
            ecuacion = args[0].toLowerCase();
            if (args.length > 1) {
                metodoSeleccionado = args[1].toUpperCase();
            }
            // Condiciones iniciales: args[2], args[3], ...
            for (int i = 2; i < args.length; i++) {
                condicionesIniciales.add(args[i]);
            }
        }
        
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║     RESOLVEDOR INTERACTIVO DE ECUACIONES DIFERENCIALES     ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
            // Si no hay argumentos, solicitar interactivamente
            if (ecuacion == null) {
                System.out.println("\n📝 INGRESO DE DATOS:");
                System.out.print("   Ingresa una ecuación (Ej: y'' + 4y = 8cos(2x)): ");
                ecuacion = scanner.nextLine().toLowerCase();
                
                // Opción de método (UC o VP)
                System.out.print("\n❓ ¿Qué método prefieres? (UC/VP) [default=UC]: ");
                String metodoInput = scanner.nextLine().trim().toUpperCase();
                if ("VP".equals(metodoInput)) {
                    metodoSeleccionado = "VP";
                }
                
                // Opción de condiciones iniciales
                System.out.print("\n❓ ¿Deseas agregar condiciones iniciales? (s/n): ");
                String respuestCI = scanner.nextLine().trim().toLowerCase();
                
                if ("s".equals(respuestCI) || "si".equals(respuestCI)) {
                    System.out.println("\n📋 INGRESO DE CONDICIONES INICIALES:");
                    System.out.println("   Formato: y(0)=1, y'(0)=2, etc.");
                    System.out.println("   (Ingresa vacío cuando termines)");
                    
                    while (true) {
                        System.out.print("   CI: ");
                        String ci = scanner.nextLine().trim();
                        if (ci.isEmpty()) {
                            break;
                        }
                        condicionesIniciales.add(ci);
                    }
                    
                    if (!condicionesIniciales.isEmpty()) {
                        System.out.println("\n✅ Condiciones iniciales ingresadas: " + condicionesIniciales);
                    }
                }
            }

            if (!esEcuacionDiferencial(ecuacion)) {
                System.out.println("❌ ERROR: La ecuación ingresada NO es una ecuación diferencial.");
                return;
            }
            
            // 1. Parsear la ecuación y extraer datos
            data = parser.parse(ecuacion);
            
            Double[] coeffsArray = data.getCoefficients(); 
            int order = data.getOrder();
            
            if (coeffsArray == null || coeffsArray.length == 0 || order <= 0) {
                System.out.println("❌ ERROR: No se pudo extraer el polinomio característico o el orden es incorrecto.");
                return;
            }
            
            // Convertir Double[] a List<Double> para PolynomialSolver
            List<Double> coeffs = Arrays.asList(coeffsArray);

            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                   INFORMACIÓN EXTRAÍDA                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("   📐 Ecuación: " + ecuacion);
            System.out.println("   📊 Orden: " + order);
            System.out.print("   🔢 Coeficientes: ");
            System.out.println(Arrays.toString(coeffsArray)); 
            System.out.println("   🏠 Tipo: " + (data.getIsHomogeneous() ? "HOMOGÉNEA" : "NO-HOMOGÉNEA"));
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                System.out.println("   🔌 Forzamiento: " + g_x);
            }
            
            System.out.println("\n   📌 Método seleccionado: " + metodoSeleccionado);
            
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║             PASO 1: SOLUCIÓN HOMOGÉNEA (y_h)              ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
            // 1. Resolver raíces y generar y_h
            List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            System.out.println("\n🔍 Raíces del Polinomio Característico:");
            for (int i = 0; i < finalRoots.size(); i++) {
                Root r = finalRoots.get(i);
                System.out.println("   └─ Raíz " + (i+1) + ": " + r.toString());
            }

            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            System.out.println("\n✅ Solución Homogénea (y_h):");
            System.out.println("   y_h(x) = " + solution_h);

            // --- FASE DE SOLUCIÓN PARTICULAR (y_p) ---
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                String solution_p = "";

                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║        PASO 2: SOLUCIÓN PARTICULAR (y_p)                  ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                System.out.println("   🔌 Forzamiento: g(x) = " + g_x);
                
                System.out.println("\n   ✅ Método: " + metodoSeleccionado);
                
                if ("UC".equals(metodoSeleccionado)) {
                    // --- Método 1: Coeficientes Indeterminados (UC) ---
                    System.out.println("\n   📌 Usando Coeficientes Indeterminados (UC)...");
                    
                    try {
                        // 1. Generar la forma (y el ucSolver la almacena)
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(finalRoots);
                        
                        String ypForm = ucSolver.getParticularSolutionForm(g_x); 
                        System.out.println("   ✓ Forma propuesta: y_p = " + ypForm);
                        
                        List<String> ypCoeffNames = ucSolver.getCoeffNames(); 
                        System.out.println("   ✓ Incógnitas a resolver: " + ypCoeffNames);
                        
                        // 2. Instanciar el Resolver
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver); 
                        
                        // 3. Resolver el sistema A|b
                        Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients(); 
                        System.out.println("   ✓ Sistema resuelto: " + solvedCoeffs);
                        
                        // 4. Generar la solución final
                        solution_p = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        
                    } catch (ArithmeticException e) {
                         System.err.println("   ⚠️ El sistema es singular (probablemente resonancia).");
                         System.err.println("   📝 Detalle: " + e.getMessage());
                         solution_p = "ERROR: Sistema singular";
                    } catch (Exception e) {
                         System.err.println("   ❌ Error inesperado: " + e.getMessage());
                         solution_p = "ERROR: Fallo en UC";
                    }

                } else if ("VP".equals(metodoSeleccionado)) {
                    // --- Método 2: Variación de Parámetros (VP) ---
                    System.out.println("\n   📌 Usando Variación de Parámetros (VP)...");
                    
                    if (order < 2) {
                        System.err.println("   ❌ VP solo aplica a EDOs de orden >= 2.");
                        solution_p = "VP: Orden no soportado";
                    } else {
                        WronskianCalculator wc = new WronskianCalculator(finalRoots);
                        List<String> yFunctions = wc.generateFundamentalSet(); 
                        
                        double leadingCoeff = coeffsArray[0]; 
                        
                        VariationOfParametersSolverV2 vpSolver = new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
                        String vpSteps = vpSolver.formulateVdpSolution();
                        
                        System.out.println(vpSteps);
                        solution_p = "Fórmulas generadas (Ver arriba)";
                    }
                    
                } else {
                    System.err.println("   ⚠️ Opción no válida.");
                    solution_p = ""; 
                }

                System.out.println("\n   ✅ Solución Particular: y_p = " + solution_p);
                
                // --- Ensamblaje de la Solución General ---
                String final_p = solution_p.trim();
                String final_h = solution_h.trim();
                String solution_final = final_h;
                
                // Solo concatenar si y_p es válido
                if (!final_p.isEmpty() && !final_p.startsWith("ERROR") && !final_p.contains("Fórmulas")) {
                    
                    if (!final_p.matches("^[\\+\\-]?\\s*0(\\.0+)?$")) { 
                        
                        String clean_p = final_p.replaceAll("^\\+", "").trim();

                        if (clean_p.startsWith("-")) {
                            solution_final += clean_p;
                        } else {
                            solution_final += " + " + clean_p;
                        }
                    }
                }
                
                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║              SOLUCIÓN GENERAL FINAL                        ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                System.out.println("   y(x) = y_h(x) + y_p(x)");
                System.out.println("   y(x) = " + solution_final.trim());
                
                // Si hay condiciones iniciales, mostrar mensaje
                if (!condicionesIniciales.isEmpty()) {
                    System.out.println("\n   📌 Nota: Condiciones iniciales ingresadas para futura integración web.");
                    System.out.println("   CI: " + condicionesIniciales);
                }

            } else {
                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║              SOLUCIÓN FINAL (HOMOGÉNEA)                    ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                System.out.println("   y(x) = " + solution_h);
                
                if (!condicionesIniciales.isEmpty()) {
                    System.out.println("\n   📌 Nota: Condiciones iniciales ingresadas para futura integración web.");
                    System.out.println("   CI: " + condicionesIniciales);
                }
            }
            
            System.out.println("\n✨ ¡Proceso completado exitosamente!");
            
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