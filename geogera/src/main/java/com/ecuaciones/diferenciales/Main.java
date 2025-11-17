package com.ecuaciones.diferenciales;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.VariationOfParametersSolverV2;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;
import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver;
import com.ecuaciones.diferenciales.dto.StepResponse;
import com.ecuaciones.diferenciales.service.StepByStepSolver;

public class Main {
    
    private static final String SEPARATOR = "╔════════════════════════════════════════════════════════════╗";
    private static final String SEPARATOR_END = "╚════════════════════════════════════════════════════════════╝";
    
    public static void main(String[] args) {
        EcuationParser parser = new EcuationParser();
        
        try (Scanner scanner = new Scanner(System.in)) {
            mostrarMenuPrincipal();
            
            // Menú principal - loop continuo
            boolean continuarPrograma = true;
            int numeroEcuacion = 1;
            
            while (continuarPrograma) {
                System.out.print("\n¿Deseas resolver una ecuación diferencial? (s/n): ");
                String respuesta = scanner.nextLine().trim().toLowerCase();
                
                if (!"s".equals(respuesta) && !"si".equals(respuesta)) {
                    System.out.println("\n👋 ¡Gracias por usar el resolvedor de EDOs!");
                    System.out.println("   📊 Total de ecuaciones resueltas: " + (numeroEcuacion - 1));
                    break;
                }
                
                // Variables para cada iteración
                String ecuacion = null;
                String metodoSeleccionado = "UC";
                List<String> condicionesIniciales = new ArrayList<>();
                
                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║  ECUACIÓN #" + numeroEcuacion + "                                            ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                
                // Ingreso de ecuación con validación
                ecuacion = ingresarEcuacion(scanner);
                if (ecuacion == null) {
                    continue;
                }
                numeroEcuacion++;
                
                // Seleccionar método
                metodoSeleccionado = seleccionarMetodo(scanner, ecuacion);
                
                // Ingresar condiciones iniciales
                condicionesIniciales = ingresarCondicionesIniciales(scanner);

                // Resolver la ecuación
                try {
                    resolverEcuacion(parser, ecuacion, metodoSeleccionado, condicionesIniciales);
                } catch (Exception e) {
                    System.err.println("\n❌ Error durante la resolución: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ Ocurrió un error crítico: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Resuelve una ecuación diferencial con la información proporcionada
     */
    private static void resolverEcuacion(EcuationParser parser, String ecuacion, 
                                         String metodo, List<String> condicionesIniciales) {
        
        if (!esEcuacionDiferencial(ecuacion)) {
            System.out.println("❌ ERROR: La ecuación ingresada NO es una ecuación diferencial.");
            System.out.println("   Asegúrate de que contiene: y, y', y'', etc.");
            return;
        }
        
        try {
            // 1. Parsear la ecuación y extraer datos
            ExpressionData data = parser.parse(ecuacion);
            
            if (data == null) {
                System.out.println("❌ ERROR: No se pudo parsear la ecuación.");
                return;
            }
            
            Double[] coeffsArray = data.getCoefficients(); 
            int order = data.getOrder();
            
            if (coeffsArray == null || coeffsArray.length == 0 || order <= 0) {
                System.out.println("❌ ERROR: No se pudo extraer el polinomio característico.");
                return;
            }
            
            // Convertir Double[] a List<Double>
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
                System.out.println("   🔌 Forzamiento: g(x) = " + g_x);
            }
            
            if (!"HOMOGENEA".equals(metodo)) {
                System.out.println("   📌 Método seleccionado: " + metodo);
            }
            
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║             PASO 1: SOLUCIÓN HOMOGÉNEA (y_h)              ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
            // Resolver raíces y generar y_h
            List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            System.out.println("\n🔍 Raíces del Polinomio Característico:");
            for (int i = 0; i < finalRoots.size(); i++) {
                Root r = finalRoots.get(i);
                System.out.println("   └─ Raíz " + (i+1) + ": " + r.toString());
            }

            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            System.out.println("\n✅ Solución Homogénea:");
            System.out.println("   y_h(x) = " + solution_h);

            // --- FASE DE SOLUCIÓN PARTICULAR (y_p) ---
            String solution_p = null;  // Declarar a nivel superior para usarlo en PVI
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                solution_p = "";

                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║        PASO 2: SOLUCIÓN PARTICULAR (y_p)                  ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                System.out.println("   🔌 Forzamiento: g(x) = " + g_x);
                
                // Si metodo es AUTO, intentar UC primero
                String metodoActual = metodo;
                if ("AUTO".equals(metodo)) {
                    metodoActual = "UC";
                    System.out.println("   ✅ Estrategia: Intentar UC primero, fallback a VP");
                }
                
                System.out.println("   📌 Método inicial: " + metodoActual);
                
                boolean metodoPrincipalFallo = false;
                
                if ("UC".equals(metodoActual)) {
                    // --- Método 1: Coeficientes Indeterminados ---
                    System.out.println("\n   📌 Resolviendo con Coeficientes Indeterminados...");
                    
                    try {
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(finalRoots);
                        String ypForm = ucSolver.getParticularSolutionForm(g_x); 
                        System.out.println("   ✓ Forma propuesta: y_p = " + ypForm);
                        
                        List<String> ypCoeffNames = ucSolver.getCoeffNames(); 
                        System.out.println("   ✓ Incógnitas: " + ypCoeffNames);
                        
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver); 
                        Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients(); 
                        System.out.println("   ✓ Coeficientes calculados: " + solvedCoeffs);
                        
                        solution_p = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        System.out.println("   ✅ UC fue exitoso");
                        
                    } catch (ArithmeticException e) {
                        // UC maneja resonancia internamente - no cambiar de método
                        System.out.println("   ⚠️ Sistema singular detectado (posible RESONANCIA)");
                        System.out.println("   ℹ️ UC resuelve resonancia analíticamente...");
                        metodoPrincipalFallo = false;  // No es un fallo, UC lo maneja
                        
                    } catch (Exception e) {
                        metodoPrincipalFallo = true;
                        System.out.println("   ⚠️ Error en UC: " + e.getMessage());
                        if ("AUTO".equals(metodo)) {
                            // Si era AUTO, intentar VP
                            System.out.println("   ℹ️ Switcheando a Variación de Parámetros...");
                        } else {
                            // Si user seleccionó UC específicamente, mostrar error pero NO cambiar
                            solution_p = "ERROR: " + e.getMessage();
                        }
                    }
                }
                
                // Si UC falló o es VP directamente
                if (metodoPrincipalFallo || "VP".equals(metodoActual)) {
                    System.out.println("\n   📌 Usando Variación de Parámetros (VP)...\n");
                    
                    if (order < 2) {
                        System.out.println("   ❌ VP requiere orden >= 2");
                        solution_p = "ERROR: VP requiere orden >= 2";
                    } else {
                        try {
                            WronskianCalculator wc = new WronskianCalculator(finalRoots);
                            List<String> yFunctions = wc.generateFundamentalSet(); 
                            double leadingCoeff = coeffsArray[0]; 
                            
                            VariationOfParametersSolverV2 vpSolver = 
                                new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
                            String vpSteps = vpSolver.formulateVdpSolution();
                            
                            System.out.println(vpSteps);
                            solution_p = vpSolver.getYpFormula();
                            System.out.println("   ✅ VP fue exitoso");
                        } catch (Exception ex) {
                            System.err.println("   ❌ Error en VP: " + ex.getMessage());
                            solution_p = "ERROR";
                        }
                    }
                }

                System.out.println("\n   ✅ Solución Particular: y_p = " + solution_p);
                
                // --- Ensamblaje Final ---
                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║              SOLUCIÓN GENERAL FINAL                        ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                
                System.out.println("\n   📌 Solución Homogénea:");
                System.out.println("      y_h(x) = " + solution_h);
                
                String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                if (!cleanedYp.isEmpty() && !cleanedYp.startsWith("ERROR")) {
                    System.out.println("\n   📌 Solución Particular:");
                    System.out.println("      y_p(x) = " + cleanedYp);
                    System.out.println("\n   📌 Solución General:");
                    System.out.println("      y(x) = (" + solution_h + ") + (" + cleanedYp + ")");
                } else {
                    System.out.println("      y(x) = " + solution_h);
                }
                
            } else {
                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║              SOLUCIÓN FINAL (HOMOGÉNEA)                    ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                System.out.println("   y(x) = " + solution_h);
            }
            
            // Aplicar condiciones iniciales
            if (!condicionesIniciales.isEmpty()) {
                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                System.out.println("║         PASO 3: APLICACIÓN DE CONDICIONES INICIALES        ║");
                System.out.println("╚════════════════════════════════════════════════════════════╝");
                
                System.out.println("\n   📌 Condiciones Iniciales Ingresadas:");
                for (String ci : condicionesIniciales) {
                    System.out.println("      • " + ci);
                }
                
                try {
                    // Construir la solución general COMPLETA (y_h + y_p) para pasar al solver
                    String generalSolutionComplete;
                    if (!data.getIsHomogeneous() && solution_p != null && !solution_p.startsWith("ERROR")) {
                        String cleanedYpForCI = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                        // Construir sin "y(x) = " porque InitialConditionsSolver lo añade internamente
                        generalSolutionComplete = "(" + solution_h + ") + (" + cleanedYpForCI + ")";
                    } else {
                        // Solo solución homogénea
                        generalSolutionComplete = solution_h;
                    }
                    
                    // Crear solver de CI con la solución general COMPLETA
                    InitialConditionsSolver ciSolver = new InitialConditionsSolver(generalSolutionComplete, order);
                    
                    // Parsear condiciones
                    List<InitialConditionsSolver.InitialCondition> parsedConditions = 
                        InitialConditionsSolver.parseConditions(condicionesIniciales);
                    
                    // Resolver sistema
                    Map<String, Double> solvedConstants = ciSolver.solveInitialConditions(parsedConditions);
                    
                    System.out.println("\n   🔧 Sistema de Ecuaciones Resuelto:");
                    System.out.println("      Constantes calculadas:");
                    for (Map.Entry<String, Double> entry : solvedConstants.entrySet()) {
                        String formatted = formatConstantValue(entry.getValue());
                        System.out.println("         " + entry.getKey() + " = " + formatted);
                    }
                    
                    // Aplicar constantes a la solución general completa
                    String particularSolution = ciSolver.applyConstants(solvedConstants);
                    
                    System.out.println("\n╔════════════════════════════════════════════════════════════╗");
                    System.out.println("║              SOLUCIÓN PARTICULAR (CON CI)                  ║");
                    System.out.println("╚════════════════════════════════════════════════════════════╝");
                    System.out.println("   y(x) = " + particularSolution);
                    
                } catch (Exception e) {
                    System.out.println("\n   ⚠️  No se pudieron aplicar las CI: " + e.getMessage());
                    System.out.println("       La solución general sigue siendo válida.");
                    e.printStackTrace();  // Mostrar stack trace para debugging
                }
            }
            
            mostrarResumenExitoso();
            
        } catch (Exception e) {
            System.err.println("\n❌ Error crítico durante la resolución:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra el menú principal del programa
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     🎓 RESOLVEDOR INTERACTIVO DE ECUACIONES DIFERENCIALES 🎓 ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                            ║");
        System.out.println("║  Este programa resuelve:                                   ║");
        System.out.println("║  ✅ Ecuaciones Homogéneas (cualquier orden)               ║");
        System.out.println("║  ✅ No-Homogéneas por Coeficientes Indeterminados (UC)   ║");
        System.out.println("║  ✅ No-Homogéneas por Variación de Parámetros (VP)       ║");
        System.out.println("║  ✅ Detección automática de resonancia                    ║");
        System.out.println("║  ✅ Aplicación de Condiciones Iniciales                   ║");
        System.out.println("║                                                            ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  FORMATOS SOPORTADOS:                                      ║");
        System.out.println("║  • y' + 2y = 4                 (primer orden)              ║");
        System.out.println("║  • y'' - 5y' + 6y = 0          (segundo orden, homogénea) ║");
        System.out.println("║  • y'' + 4y = 2*sin(x)         (no-homogénea)            ║");
        System.out.println("║  • y^(4) - 5y'' + 4y = e^(x)  (orden superior)            ║");
        System.out.println("║  • y(0)=1, y'(0)=2             (condiciones iniciales)    ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    /**
     * Verifica si la cadena es una ecuación diferencial válida
     */
    public static boolean esEcuacionDiferencial(String ecuacion) {
        String[] derivativePatterns = { "dy/dx", "d2y/dx2", "y'", "y''", "y'''" }; 
        for (String pattern : derivativePatterns) {
            if (ecuacion.contains(pattern)) return true;
        }
        
        if (ecuacion.contains("y") && ecuacion.contains("=")) return true;
        if (ecuacion.contains("y^")) return true;

        return false;
    }
    
    /**
     * Ingresa y valida una ecuación diferencial
     */
    private static String ingresarEcuacion(Scanner scanner) {
        System.out.println("\n📝 INGRESO DE ECUACIÓN:");
        System.out.println("   💡 Ejemplos válidos:");
        System.out.println("      • Homogénea: y'' - 5*y' + 6*y = 0");
        System.out.println("      • No-homogénea: y'' + 4*y = 8*cos(2*x)");
        System.out.println("      • Orden 1: y' + 2*y = 4");
        System.out.println("      • Orden 3+: y''' - y' = x^2");
        System.out.print("\n   Ingresa la ecuación: ");
        
        String ecuacion = scanner.nextLine().trim();
        
        if (ecuacion.isEmpty()) {
            System.out.println("   ⚠️ Ecuación vacía, saltando...");
            return null;
        }
        
        if (!esEcuacionDiferencial(ecuacion)) {
            System.out.println("   ⚠️ ADVERTENCIA: La ecuación podría no ser válida.");
            System.out.println("   ✓ Intentando procesar de todas formas...");
        }
        
        return ecuacion;
    }
    
    /**
     * Selecciona método de resolución
     */
    private static String seleccionarMetodo(Scanner scanner, String ecuacion) {
        // Verificar si es homogénea
        if (!ecuacion.contains("=") || ecuacion.split("=")[1].trim().equals("0")) {
            System.out.println("   📌 Ecuación homogénea detectada (método automático)");
            return "HOMOGENEA";
        }
        
        System.out.println("\n❓ Selecciona método de resolución:");
        System.out.println("   1. UC  - Coeficientes Indeterminados (más rápido)");
        System.out.println("   2. VP  - Variación de Parámetros (más general)");
        System.out.println("   3. AUTO - Automático (UC → VP si falla)");
        System.out.print("   Opción [1/2/3] (default=3): ");
        
        String input = scanner.nextLine().trim();
        
        if ("1".equals(input) || "uc".equalsIgnoreCase(input)) {
            return "UC";
        } else if ("2".equals(input) || "vp".equalsIgnoreCase(input)) {
            return "VP";
        } else {
            return "AUTO";
        }
    }
    
    /**
     * Ingresa condiciones iniciales
     */
    private static List<String> ingresarCondicionesIniciales(Scanner scanner) {
        List<String> condiciones = new ArrayList<>();
        
        System.out.print("\n❓ ¿Deseas agregar condiciones iniciales? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        
        if (!"s".equals(respuesta) && !"si".equals(respuesta)) {
            return condiciones;
        }
        
        System.out.println("\n📋 INGRESO DE CONDICIONES INICIALES:");
        System.out.println("   Formato: y(0)=1, y'(0)=2, y''(0)=3, etc.");
        System.out.println("   (Ingresa línea vacía para terminar)");
        
        int contador = 1;
        while (true) {
            System.out.print("   CI " + contador + ": ");
            String ci = scanner.nextLine().trim();
            
            if (ci.isEmpty()) {
                break;
            }
            
            if (validarCondicionInicial(ci)) {
                condiciones.add(ci);
                contador++;
            } else {
                System.out.println("      ⚠️ Formato inválido. Usa: y(a)=b o y'(a)=b");
            }
        }
        
        if (!condiciones.isEmpty()) {
            System.out.println("\n   ✅ " + condiciones.size() + " condición(es) ingresada(s):");
            for (String ci : condiciones) {
                System.out.println("      • " + ci);
            }
        }
        
        return condiciones;
    }
    
    /**
     * Valida formato de condición inicial
     */
    private static boolean validarCondicionInicial(String ci) {
        // Formato: y(x)=valor o y'(x)=valor
        return ci.matches("y'*\\(-?\\d+(?:\\.\\d+)?\\)=-?\\d+(?:\\.\\d+)?");
    }
    
    /**
     * Muestra resumen de la resolución
     */
    private static void mostrarResumenExitoso() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              ✨ RESOLUCIÓN EXITOSA ✨                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("   ✅ Ecuación procesada correctamente");
        System.out.println("   📌 Consulta la salida anterior para los detalles");
    }
    
    /**
     * Detecta resonancia pura trigonométrica y extrae coeficientes analíticamente
     */
    
    // ════════════════════════════════════════════════════════════════════════════════
    // MÉTODO PARA FRONTEND: Evaluar ecuación y retornar JSON
    // ════════════════════════════════════════════════════════════════════════════════
    
    /**
     * Evalúa una ecuación diferencial y retorna un Map con la solución
     * USO: Map<String, Object> resultado = Main.evaluate("y'' - 5*y' + 6*y = 0");
     */
    public static Map<String, Object> evaluate(String ecuacion) {
        return evaluate(ecuacion, "AUTO", new ArrayList<>());
    }
    
    /**
     * Evalúa con método especificado
     * USO: Main.evaluate("y'' + 4*y = sin(2*x)", "UC");
     */
    public static Map<String, Object> evaluate(String ecuacion, String metodo) {
        return evaluate(ecuacion, metodo, new ArrayList<>());
    }
    
    /**
     * Evalúa con todas las opciones
     * USO: Main.evaluate("y'' - 5*y' + 6*y = 0", "AUTO", Arrays.asList("y(0)=1"));
     */
    public static Map<String, Object> evaluate(String ecuacion, String metodo, List<String> condicionesIniciales) {
        Map<String, Object> resultado = new HashMap<>();
        long startTime = System.currentTimeMillis();
        EcuationParser parser = new EcuationParser();
        
        try {
            // Validar
            if (ecuacion == null || ecuacion.trim().isEmpty()) {
                resultado.put("status", "ERROR");
                resultado.put("message", "Ecuación vacía");
                resultado.put("code", 400);
                return resultado;
            }
            
            // Validar que sea EDO
            if (!esEcuacionDiferencial(ecuacion)) {
                resultado.put("status", "ERROR");
                resultado.put("message", "No es una ecuación diferencial válida");
                resultado.put("code", 400);
                return resultado;
            }
            
            // Parsear
            ExpressionData data = parser.parse(ecuacion);
            
            if (data == null) {
                resultado.put("status", "ERROR");
                resultado.put("message", "No se pudo parsear la ecuación");
                resultado.put("code", 400);
                return resultado;
            }
            
            Double[] coeffsArray = data.getCoefficients();
            int order = data.getOrder();
            
            if (coeffsArray == null || coeffsArray.length == 0 || order <= 0) {
                resultado.put("status", "ERROR");
                resultado.put("message", "No se pudo extraer el polinomio característico");
                resultado.put("code", 400);
                return resultado;
            }
            
            // Información básica
            resultado.put("expression", ecuacion);
            resultado.put("order", order);
            resultado.put("isHomogeneous", data.getIsHomogeneous());
            
            if (!data.getIsHomogeneous()) {
                resultado.put("forcingTerm", data.getIndependentTerm().get("g(x)"));
            }
            
            // Resolver raíces
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> finalRoots = PolynomialSolver.solve(coeffs);
            
            // Raíces formateadas
            List<Map<String, Object>> rootsList = new ArrayList<>();
            for (int i = 0; i < finalRoots.size(); i++) {
                Root r = finalRoots.get(i);
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("index", i + 1);
                rootMap.put("real", r.getReal());
                rootMap.put("imaginary", r.getImaginary());
                rootMap.put("display", r.toString());
                rootsList.add(rootMap);
            }
            resultado.put("roots", rootsList);
            
            // Solución homogénea
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String solution_h = hSolver.generateHomogeneousSolution(finalRoots);
            resultado.put("homogeneousSolution", solution_h);
            
            // Solución particular (si no es homogénea)
            String solution_p = null;
            String methodUsed = metodo;
            
            if (!data.getIsHomogeneous()) {
                String g_x = data.getIndependentTerm().get("g(x)");
                
                String metodoActual = metodo;
                if ("AUTO".equals(metodo)) {
                    metodoActual = "UC";
                }
                
                boolean metodoPrincipalFallo = false;
                
                // Intentar UC
                if ("UC".equals(metodoActual)) {
                    try {
                        UndeterminedCoeff ucSolver = new UndeterminedCoeff(finalRoots);
                        String ypForm = ucSolver.getParticularSolutionForm(g_x);
                        
                        UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
                        Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
                        
                        solution_p = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
                        methodUsed = "UC";
                        
                    } catch (ArithmeticException e) {
                        // Resonancia
                        metodoPrincipalFallo = false;
                        
                    } catch (Exception e) {
                        metodoPrincipalFallo = true;
                        
                        if ("AUTO".equals(metodo)) {
                            // Fallback a VP
                        }
                    }
                }
                
                // Fallback a VP
                if (metodoPrincipalFallo || "VP".equals(metodoActual)) {
                    if (order >= 2) {
                        try {
                            WronskianCalculator wc = new WronskianCalculator(finalRoots);
                            List<String> yFunctions = wc.generateFundamentalSet();
                            double leadingCoeff = coeffsArray[0];
                            
                            VariationOfParametersSolverV2 vpSolver = 
                                new VariationOfParametersSolverV2(yFunctions, g_x, leadingCoeff, order, wc);
                            
                            solution_p = vpSolver.getYpFormula();
                            methodUsed = "VP";
                            
                        } catch (Exception e) {
                            solution_p = null;
                        }
                    }
                }
                
                resultado.put("particularMethod", methodUsed);
                
                if (solution_p != null && !solution_p.startsWith("ERROR")) {
                    String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                    resultado.put("particulatSolution", cleanedYp);
                }
            }
            
            // Solución final
            String finalSolution;
            if (!data.getIsHomogeneous() && solution_p != null && !solution_p.startsWith("ERROR")) {
                String cleanedYp = solution_p.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
                // No agregar paréntesis extra si no son necesarios
                finalSolution = "y(x) = " + solution_h + " + " + cleanedYp;
            } else {
                finalSolution = "y(x) = " + solution_h;
            }
            
            resultado.put("finalSolution", finalSolution);
            resultado.put("solutionLatex", toLatex(finalSolution));
            
            // APLICAR CONDICIONES INICIALES SI LAS HAY
            if (!condicionesIniciales.isEmpty()) {
                try {
                    // Preparar la solución completa (y_h + y_p) para pasar a InitialConditionsSolver
                    String generalSolutionString = finalSolution.replace("y(x) = ", "").trim();
                    
                    InitialConditionsSolver ciSolver = new InitialConditionsSolver(generalSolutionString, order);
                    List<InitialConditionsSolver.InitialCondition> parsedConditions = 
                        InitialConditionsSolver.parseConditions(condicionesIniciales);
                    
                    if (!parsedConditions.isEmpty()) {
                        Map<String, Double> solvedConstants = ciSolver.solveInitialConditions(parsedConditions);
                        
                        // Agregar constantes al resultado
                        Map<String, Object> constantsMap = new HashMap<>();
                        for (Map.Entry<String, Double> entry : solvedConstants.entrySet()) {
                            constantsMap.put(entry.getKey(), entry.getValue());
                        }
                        resultado.put("constants", constantsMap);
                        
                        // Aplicar constantes a la solución completa
                        String particularSolution = ciSolver.applyConstants(solvedConstants);
                        
                        // Actualizar solución final con las constantes aplicadas
                        finalSolution = "y(x) = " + particularSolution;
                        
                        resultado.put("finalSolution", finalSolution);
                        resultado.put("solutionLatex", toLatex(finalSolution));
                        resultado.put("initialConditions", condicionesIniciales);
                        resultado.put("withInitialConditions", true);
                    }
                } catch (Exception e) {
                    // Si falla, mantener la solución general
                    resultado.put("initialConditionsError", e.getMessage());
                }
            }
            
            resultado.put("status", "SUCCESS");
            resultado.put("code", 200);
            resultado.put("executionTimeMs", System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            resultado.put("status", "ERROR");
            resultado.put("message", "Error: " + e.getMessage());
            resultado.put("code", 500);
            resultado.put("executionTimeMs", System.currentTimeMillis() - startTime);
        }
        
        return resultado;
    }
    
    /**
     * NUEVO: Método para evaluar con pasos detallados tipo Photomath
     * Retorna StepResponse con toda la resolución paso a paso
     */
    public static StepResponse evaluateWithSteps(String ecuacion) {
        return evaluateWithSteps(ecuacion, "AUTO");
    }
    
    /**
     * NUEVO: Evalúa con método especificado y retorna pasos
     */
    public static StepResponse evaluateWithSteps(String ecuacion, String metodo) {
        StepByStepSolver solver = new StepByStepSolver();
        return solver.solve(ecuacion, metodo);
    }
    
    /**
     * NUEVO: Convertir StepResponse a JSON string
     */
    public static String evaluateWithStepsAsJson(String ecuacion) {
        return evaluateWithStepsAsJson(ecuacion, "AUTO");
    }
    
    /**
     * NUEVO: Convertir StepResponse a JSON string
     */
    public static String evaluateWithStepsAsJson(String ecuacion, String metodo) {
        try {
            StepResponse response = evaluateWithSteps(ecuacion, metodo);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (Exception e) {
            return "{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Convertir a LaTeX
     */
    private static String toLatex(String expr) {
        if (expr == null) return "";
        String latex = expr;
        latex = latex.replace("e^", "e^{");
        latex = latex.replace("sin(", "\\sin(");
        latex = latex.replace("cos(", "\\cos(");
        return latex;
    }
    
    /**
     * Formatea un valor de constante para visualización
     */
    private static String formatConstantValue(double value) {
        double tolerance = 1e-10;
        
        // Si es muy cercano a 0
        if (Math.abs(value) < tolerance) {
            return "0";
        }
        
        // Si es un número entero
        if (Math.abs(value - Math.round(value)) < tolerance) {
            return String.valueOf((long) Math.round(value));
        }
        
        // Sino, 4 decimales
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.####", 
            new java.text.DecimalFormatSymbols(java.util.Locale.US));
        return df.format(value);
    }
}

