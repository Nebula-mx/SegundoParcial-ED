package com.ecuaciones.diferenciales;

import java.util.Arrays;
import java.util.Map;

import com.ecuaciones.diferenciales.service.EquationSolverService;

/**
 * EJEMPLO DE USO: Cómo usar EquationSolverService desde el frontend
 * 
 * OPCIÓN 1: Obtener JSON string directamente
 * OPCIÓN 2: Obtener un Map<String, Object> y procesarlo como quieras
 */
public class QuickStartExample {
    
    public static void main(String[] args) {
        EquationSolverService solver = new EquationSolverService();
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         EJEMPLOS DE USO: EquationSolverService             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        // ============ OPCIÓN 1: JSON String ============
        System.out.println("\n📌 OPCIÓN 1: Obtener JSON String");
        System.out.println("────────────────────────────────────────────────────────────\n");
        
        String jsonResult1 = solver.solve("y'' - 5*y' + 6*y = 0");
        System.out.println("Ejemplo 1: Ecuación homogénea");
        System.out.println(jsonResult1);
        
        // ============ OPCIÓN 2: Map Object ============
        System.out.println("\n\n📌 OPCIÓN 2: Obtener Map Object");
        System.out.println("────────────────────────────────────────────────────────────\n");
        
        Map<String, Object> result = solver.solveAsMap("y'' + 4*y = sin(2*x)", "UC");
        System.out.println("Ejemplo 2: Ecuación con resonancia");
        System.out.println("Status: " + result.get("status"));
        System.out.println("Solución: " + result.get("finalSolution"));
        
        // ============ OPCIÓN 3: Con Condiciones Iniciales ============
        System.out.println("\n\n📌 OPCIÓN 3: Con Condiciones Iniciales");
        System.out.println("────────────────────────────────────────────────────────────\n");
        
        String jsonResult3 = solver.solve(
            "y'' - 5*y' + 6*y = 0",
            "AUTO",
            Arrays.asList("y(0)=1", "y'(0)=2")
        );
        System.out.println(jsonResult3);
        
        // ============ OPCIÓN 4: Casos de Error ============
        System.out.println("\n\n📌 OPCIÓN 4: Manejo de Errores");
        System.out.println("────────────────────────────────────────────────────────────\n");
        
        String errorResult = solver.solve("2*x + 3 = 5");  // No es ecuación diferencial
        System.out.println("Ecuación inválida:");
        System.out.println(errorResult);
        
        // ============ OPCIÓN 5: Variación de Parámetros ============
        System.out.println("\n\n📌 OPCIÓN 5: Con Variación de Parámetros");
        System.out.println("────────────────────────────────────────────────────────────\n");
        
        String jsonResult5 = solver.solve(
            "y'' + y = 1/(1 + x^2)",
            "VP"
        );
        System.out.println(jsonResult5);
        
        System.out.println("\n\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ✅ EJEMPLOS COMPLETADOS                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
