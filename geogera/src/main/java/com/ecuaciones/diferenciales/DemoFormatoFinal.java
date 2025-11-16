package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.dto.StepResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Demostración simple del nuevo formato JSON con pasos
 * Ejecuta: java -cp target/classes com.ecuaciones.diferenciales.DemoFormatoFinal
 */
public class DemoFormatoFinal {
    
    public static void main(String[] args) throws Exception {
        System.out.println("\n" + "╔".repeat(70) + "╗");
        System.out.println("║" + " ".repeat(15) + "DEMO: NUEVO FORMATO JSON CON PASOS" + " ".repeat(21) + "║");
        System.out.println("╚" + "═".repeat(70) + "╝\n");
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();
        
        // Prueba 1: Ecuación simple
        System.out.println("📋 TEST 1: Ecuación y' + y = 0");
        System.out.println("─".repeat(70));
        try {
            StepResponse resp1 = Main.evaluateWithSteps("y' + y = 0");
            imprimirRespuesta(resp1, mapper);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        // Prueba 2: Ecuación de orden 2
        System.out.println("\n\n📋 TEST 2: Ecuación y'' - 5y' + 6y = 0");
        System.out.println("─".repeat(70));
        try {
            StepResponse resp2 = Main.evaluateWithSteps("y'' - 5*y' + 6*y = 0");
            imprimirRespuesta(resp2, mapper);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        // Prueba 3: Ecuación con resonancia
        System.out.println("\n\n📋 TEST 3: Ecuación y'' + 4y = sin(2x)");
        System.out.println("─".repeat(70));
        try {
            StepResponse resp3 = Main.evaluateWithSteps("y'' + 4*y = sin(2*x)");
            imprimirRespuesta(resp3, mapper);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        System.out.println("\n" + "═".repeat(70));
        System.out.println("✅ Demostración completada");
        System.out.println("═".repeat(70) + "\n");
    }
    
    private static void imprimirRespuesta(StepResponse resp, ObjectMapper mapper) throws Exception {
        if (resp.getStatus().equals("SUCCESS")) {
            System.out.println("✅ Status: SUCCESS");
            System.out.println("📐 Ecuación: " + resp.getEquation());
            System.out.println("🎯 Solución: " + resp.getFinalSolution());
            System.out.println("📊 Total de pasos: " + resp.getStepCount());
            System.out.println("⏱️  Tiempo: " + resp.getExecutionTimeMs() + "ms");
            
            if (resp.getSteps() != null && !resp.getSteps().isEmpty()) {
                System.out.println("\n📚 PASOS:");
                for (int i = 0; i < resp.getSteps().size(); i++) {
                    StepResponse.Step step = resp.getSteps().get(i);
                    System.out.println("\n  Paso " + (i + 1) + ": " + step.getTitle());
                    System.out.println("  ├─ Tipo: " + step.getType());
                    if (step.getExpressions() != null && !step.getExpressions().isEmpty()) {
                        System.out.println("  ├─ Expresiones: " + String.join(", ", step.getExpressions()));
                    }
                    if (step.getExplanation() != null && !step.getExplanation().isEmpty()) {
                        System.out.println("  └─ Explicación: " + step.getExplanation());
                    }
                }
            }
            
            // Mostrar JSON formateado
            System.out.println("\n\n📄 JSON FORMATEADO:");
            System.out.println("```json");
            String jsonFormatted = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);
            System.out.println(jsonFormatted);
            System.out.println("```");
            
        } else {
            System.out.println("❌ Status: " + resp.getStatus());
            System.out.println("   Mensaje: " + resp.getMessage());
        }
    }
}
