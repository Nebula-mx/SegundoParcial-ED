package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.dto.StepResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

/**
 * Ejemplo que muestra cómo usar el nuevo formato JSON con pasos detallados
 * (Estilo Photomath con resolución paso a paso)
 */
public class EjemploNuevoFormatoJSON {
    
    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  NUEVO FORMATO JSON CON PASOS DETALLADOS (Estilo Photomath)   ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Prueba 1: Ecuación simple de orden 1
        System.out.println("TEST 1: Ecuación y' + y = 0");
        System.out.println("─".repeat(60));
        StepResponse resp1 = Main.evaluateWithSteps("y' + y = 0");
        mostrarRespuesta(resp1, mapper);
        
        // Prueba 2: Ecuación de orden 2 homogénea
        System.out.println("\n\nTEST 2: Ecuación y'' - 5y' + 6y = 0");
        System.out.println("─".repeat(60));
        StepResponse resp2 = Main.evaluateWithSteps("y'' - 5*y' + 6*y = 0");
        mostrarRespuesta(resp2, mapper);
        
        // Prueba 3: Ecuación con resonancia
        System.out.println("\n\nTEST 3: Ecuación y'' + 4y = sin(2x) (Resonancia)");
        System.out.println("─".repeat(60));
        StepResponse resp3 = Main.evaluateWithSteps("y'' + 4*y = sin(2*x)");
        mostrarRespuesta(resp3, mapper);
        
        // Prueba 4: Ecuación no homogénea simple
        System.out.println("\n\nTEST 4: Ecuación y'' - y = e^x");
        System.out.println("─".repeat(60));
        StepResponse resp4 = Main.evaluateWithSteps("y'' - y = e^x");
        mostrarRespuesta(resp4, mapper);
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println("✅ Ejemplos completados");
        System.out.println("═".repeat(60));
    }
    
    private static void mostrarRespuesta(StepResponse resp, ObjectMapper mapper) {
        try {
            System.out.println("\n📋 RESPUESTA JSON FORMATEADA:\n");
            String jsonFormatted = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);
            System.out.println(jsonFormatted);
            
            System.out.println("\n📝 RESUMEN:");
            System.out.println("✅ Status: " + resp.getStatus());
            System.out.println("📐 Ecuación: " + resp.getEquation());
            System.out.println("🎯 Solución Final: " + resp.getFinalSolution());
            System.out.println("📊 Total de pasos: " + resp.getStepCount());
            System.out.println("⏱️  Tiempo: " + resp.getExecutionTimeMs() + "ms");
            
            if (resp.getSteps() != null && !resp.getSteps().isEmpty()) {
                System.out.println("\n📚 PASOS DETALLADOS:");
                for (int i = 0; i < resp.getSteps().size(); i++) {
                    StepResponse.Step step = resp.getSteps().get(i);
                    System.out.println("\n  Paso " + (i + 1) + ": " + step.getTitle());
                    System.out.println("  Tipo: " + step.getType());
                    if (step.getExpressions() != null && !step.getExpressions().isEmpty()) {
                        System.out.println("  Expresiones: " + step.getExpressions());
                    }
                    if (step.getExplanation() != null && !step.getExplanation().isEmpty()) {
                        System.out.println("  Explicación: " + step.getExplanation());
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al formatear: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
