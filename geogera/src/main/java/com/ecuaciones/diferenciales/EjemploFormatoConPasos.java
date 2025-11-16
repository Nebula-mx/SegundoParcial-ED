package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.dto.StepResponse;
import com.ecuaciones.diferenciales.dto.StepResponse.Step;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

/**
 * Ejemplo de uso del nuevo formato con pasos detallados (tipo Photomath)
 * Demuestra cómo obtener la solución con todos los pasos intermedios
 */
public class EjemploFormatoConPasos {
    
    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  EJEMPLO: Formato Photomath con Pasos Detallados              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        // Ejemplo 1: Ecuación homogénea simple
        System.out.println("📋 EJEMPLO 1: Ecuación homogénea (y'' - 5y' + 6y = 0)");
        System.out.println("─".repeat(70));
        String ecuacion1 = "y'' - 5*y' + 6*y = 0";
        StepResponse response1 = Main.evaluateWithSteps(ecuacion1);
        mostrarRespuestaDetallada(response1);
        
        // Mostrar como JSON
        System.out.println("\n📄 Versión JSON:");
        String json1 = Main.evaluateWithStepsAsJson(ecuacion1);
        System.out.println(json1);
        
        // Ejemplo 2: Ecuación no homogénea
        System.out.println("\n\n📋 EJEMPLO 2: Ecuación no homogénea (y'' + 4y = sin(2x))");
        System.out.println("─".repeat(70));
        String ecuacion2 = "y'' + 4*y = sin(2*x)";
        StepResponse response2 = Main.evaluateWithSteps(ecuacion2, "UC");
        mostrarRespuestaDetallada(response2);
        
        // Mostrar como JSON
        System.out.println("\n📄 Versión JSON:");
        String json2 = Main.evaluateWithStepsAsJson(ecuacion2, "UC");
        System.out.println(json2);
    }
    
    /**
     * Muestra la respuesta de forma legible
     */
    private static void mostrarRespuestaDetallada(StepResponse response) {
        if (!response.isSuccess()) {
            System.out.println("❌ Error: " + response.getMessage());
            return;
        }
        
        System.out.println("✅ Status: " + response.getStatus());
        System.out.println("📝 Ecuación: " + response.getEquation());
        System.out.println("📊 Variable: " + response.getVariable());
        
        System.out.println("\n🎯 PASOS DE RESOLUCIÓN:");
        List<Step> steps = response.getSteps();
        for (Step step : steps) {
            System.out.println("\n  " + step.getOrder() + ". " + step.getTitle());
            System.out.println("     Tipo: " + step.getType());
            
            // Mostrar expresiones
            for (String expr : step.getExpressions()) {
                System.out.println("     → " + expr);
            }
            
            // Mostrar detalles
            if (!step.getDetails().isEmpty()) {
                System.out.println("     Detalles:");
                for (Map.Entry<String, String> detail : step.getDetails().entrySet()) {
                    System.out.println("       • " + detail.getKey() + ": " + detail.getValue());
                }
            }
            
            // Mostrar explicación
            if (step.getExplanation() != null && !step.getExplanation().isEmpty()) {
                System.out.println("     📌 " + step.getExplanation());
            }
        }
        
        System.out.println("\n" + "═".repeat(70));
        System.out.println("✨ SOLUCIÓN FINAL: " + response.getFinalSolution());
        System.out.println("📐 LaTeX: " + response.getSolutionLatex());
        
        System.out.println("\n📋 METADATOS:");
        for (Map.Entry<String, String> meta : response.getMetadata().entrySet()) {
            System.out.println("  • " + meta.getKey() + ": " + meta.getValue());
        }
        
        System.out.println("\n⏱️  Tiempo de ejecución: " + response.getExecutionTimeMs() + " ms");
        System.out.println("📊 Total de pasos: " + response.getStepCount());
    }
}
