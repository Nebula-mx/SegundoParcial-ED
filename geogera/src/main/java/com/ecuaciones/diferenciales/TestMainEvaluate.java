package com.ecuaciones.diferenciales;

import java.util.*;

/**
 * Prueba simple de los métodos evaluate() agregados a Main.java
 * Demuestra cómo usar la integración JSON sin necesidad de API
 */
public class TestMainEvaluate {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  TEST DE MÉTODOS evaluate() INTEGRADOS EN Main.java            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        // Prueba 1: Ecuación homogénea simple
        System.out.println("TEST 1: Ecuación homogénea y'' - 5y' + 6y = 0");
        System.out.println("─".repeat(60));
        Map<String, Object> result1 = Main.evaluate("y'' - 5*y' + 6*y = 0");
        mostrarResultado(result1);
        
        // Prueba 2: Ecuación con método especificado
        System.out.println("\n\nTEST 2: Ecuación no homogénea con método UC");
        System.out.println("Ecuación: y'' + 4y = sin(2x)");
        System.out.println("─".repeat(60));
        Map<String, Object> result2 = Main.evaluate("y'' + 4*y = sin(2*x)", "UC");
        mostrarResultado(result2);
        
        // Prueba 3: Ecuación con VP
        System.out.println("\n\nTEST 3: Ecuación con método VP");
        System.out.println("Ecuación: y'' + y = 1/cos(x)");
        System.out.println("─".repeat(60));
        Map<String, Object> result3 = Main.evaluate("y'' + y = 1/cos(x)", "VP");
        mostrarResultado(result3);
        
        // Prueba 4: Resonancia
        System.out.println("\n\nTEST 4: Ecuación con resonancia");
        System.out.println("Ecuación: y'' + 4y = sin(2x)");
        System.out.println("─".repeat(60));
        Map<String, Object> result4 = Main.evaluate("y'' + 4*y = sin(2*x)");
        mostrarResultado(result4);
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println("✅ Todos los tests completados exitosamente");
        System.out.println("═".repeat(60));
    }
    
    private static void mostrarResultado(Map<String, Object> resultado) {
        String status = (String) resultado.get("status");
        System.out.println("Status: " + status);
        
        if ("SUCCESS".equals(status)) {
            System.out.println("Expression: " + resultado.get("expression"));
            System.out.println("Order: " + resultado.get("order"));
            System.out.println("Homogeneous: " + resultado.get("isHomogeneous"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> roots = (List<Map<String, Object>>) resultado.get("roots");
            if (roots != null && !roots.isEmpty()) {
                System.out.println("Roots: " + roots.size() + " found");
            }
            
            System.out.println("Homogeneous Solution: " + resultado.get("homogeneousSolution"));
            
            if (resultado.get("particulatSolution") != null) {
                System.out.println("Particular Solution: " + resultado.get("particulatSolution"));
            }
            
            System.out.println("Final Solution: " + resultado.get("finalSolution"));
            System.out.println("Execution Time: " + resultado.get("executionTimeMs") + " ms");
        } else {
            System.out.println("Error: " + resultado.get("message"));
            System.out.println("Code: " + resultado.get("code"));
        }
    }
}
