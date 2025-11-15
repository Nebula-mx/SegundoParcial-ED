package com.ecuaciones.diferenciales.api.service;

import com.ecuaciones.diferenciales.api.dto.Step;
import com.ecuaciones.diferenciales.api.dto.SolutionResponse;
import java.util.*;

/**
 * 🔨 Generador de pasos para la solución paso-a-paso
 * 
 * Se encarga de:
 * - Registrar cada operación realizada
 * - Convertir operaciones a pasos legibles
 * - Generar explicaciones en lenguaje natural
 */
public class StepBuilder {
    
    private List<Step> steps;
    private int stepCounter;
    
    public StepBuilder() {
        this.steps = new ArrayList<>();
        this.stepCounter = 0;
    }
    
    // --- PASOS DE CLASIFICACIÓN ---
    
    public void addClassificationStep(String odeType, String order, String description) {
        Step step = new Step(
            Step.StepType.CLASSIFY,
            "Clasificación de la EDO",
            description
        );
        
        step.withDetail("Tipo", odeType)
            .withDetail("Orden", order)
            .withExpression(String.format("EDO de orden %s, %s", order, odeType));
        
        addStep(step);
    }
    
    // --- PASOS DE ECUACIÓN CARACTERÍSTICA ---
    
    public void addCharacteristicStep(String differential, String characteristic) {
        Step step = new Step(
            Step.StepType.CHARACTERISTIC,
            "Formar la ecuación característica",
            "Reemplazar y^(n) con r^n para obtener la ecuación auxiliar"
        );
        
        step.withExpression("Diferencial: " + differential)
            .withExpression("Característica: " + characteristic)
            .withDetail("Método", "Sustitución exponencial");
        
        addStep(step);
    }
    
    // --- PASOS DE RAÍCES ---
    
    public void addRootsStep(String characteristic, List<String> roots, String explanation) {
        Step step = new Step(
            Step.StepType.FIND_ROOTS,
            "Encontrar las raíces",
            explanation
        );
        
        step.withExpression("Ecuación: " + characteristic);
        
        for (int i = 0; i < roots.size(); i++) {
            step.withExpression("r" + (i + 1) + " = " + roots.get(i));
        }
        
        addStep(step);
    }
    
    // --- PASOS DE SOLUCIÓN HOMOGÉNEA ---
    
    public void addHomogeneousSolutionStep(String fundamentalSet, String composition, String explanation) {
        Step step = new Step(
            Step.StepType.HOMOGENEOUS_SOLUTION,
            "Construir la solución homogénea",
            explanation
        );
        
        step.withExpression("Conjunto fundamental: {" + fundamentalSet + "}")
            .withExpression("y_h(x) = " + composition)
            .withDetail("Forma", "Combinación lineal de soluciones fundamentales");
        
        addStep(step);
    }
    
    // --- PASOS DE SOLUCIÓN PARTICULAR ---
    
    public void addParticularSolutionStep(String method, String guess, String result, String explanation) {
        Step step = new Step(
            Step.StepType.PARTICULAR_SOLUTION,
            "Encontrar la solución particular",
            explanation
        );
        
        step.withDetail("Método", method)
            .withExpression("Forma supuesta: " + guess)
            .withExpression("Solución: y_p(x) = " + result);
        
        addStep(step);
    }
    
    // --- PASOS DE SOLUCIÓN GENERAL ---
    
    public void addGeneralSolutionStep(String homogeneous, String particular, String general) {
        Step step = new Step(
            Step.StepType.GENERAL_SOLUTION,
            "Escribir la solución general",
            "La solución general es la suma de la solución homogénea y particular"
        );
        
        step.withExpression("y_h(x) = " + homogeneous)
            .withExpression("y_p(x) = " + particular)
            .withExpression("y(x) = y_h(x) + y_p(x)")
            .withExpression("y(x) = " + general);
        
        addStep(step);
    }
    
    // --- PASOS DE CONDICIONES INICIALES ---
    
    public void addApplyConditionsStep(List<String> conditions, List<String> equations, String explanation) {
        Step step = new Step(
            Step.StepType.APPLY_CONDITIONS,
            "Aplicar las condiciones iniciales",
            explanation
        );
        
        for (String condition : conditions) {
            step.withExpression(condition);
        }
        
        step.withDetail("Sistema de ecuaciones", equations.size() + " ecuación(es)");
        
        for (int i = 0; i < equations.size(); i++) {
            step.withExpression("(" + (i + 1) + ") " + equations.get(i));
        }
        
        addStep(step);
    }
    
    // --- PASOS DE SOLUCIÓN FINAL ---
    
    public void addFinalSolutionStep(Map<String, Double> constants, String finalSolution, String explanation) {
        Step step = new Step(
            Step.StepType.FINAL_SOLUTION,
            "Solución particular con condiciones aplicadas",
            explanation
        );
        
        for (Map.Entry<String, Double> entry : constants.entrySet()) {
            step.withExpression(entry.getKey() + " = " + entry.getValue());
        }
        
        step.withExpression("y(x) = " + finalSolution);
        
        addStep(step);
    }
    
    // --- PASOS PERSONALIZADOS ---
    
    public void addCustomStep(Step.StepType type, String title, String explanation, List<String> expressions) {
        Step step = new Step(type, title, explanation);
        if (expressions != null) {
            step.withExpressions(expressions);
        }
        addStep(step);
    }
    
    // --- HELPERS ---
    
    private void addStep(Step step) {
        stepCounter++;
        step.withOrder(stepCounter);
        this.steps.add(step);
    }
    
    public List<Step> getSteps() {
        return new ArrayList<>(steps);
    }
    
    public int getStepCount() {
        return steps.size();
    }
    
    public void clear() {
        steps.clear();
        stepCounter = 0;
    }
    
    /**
     * Integra los pasos en una SolutionResponse
     */
    public void applyToResponse(SolutionResponse response) {
        response.addSteps(this.getSteps());
    }
    
    /**
     * Imprime los pasos de forma legible
     */
    public void printSteps() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              📋 PASOS DE LA SOLUCIÓN                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        for (Step step : steps) {
            System.out.println("\n┌" + "─".repeat(60) + "┐");
            System.out.println("│ [Paso " + step.getOrder() + "] " + step.getTitle());
            System.out.println("└" + "─".repeat(60) + "┘");
            System.out.println("📝 " + step.getExplanation());
            
            if (!step.getExpressions().isEmpty()) {
                System.out.println("\n   Expresiones:");
                for (String expr : step.getExpressions()) {
                    System.out.println("   ➜ " + expr);
                }
            }
            
            if (!step.getDetails().isEmpty()) {
                System.out.println("\n   Detalles:");
                for (Map.Entry<String, String> detail : step.getDetails().entrySet()) {
                    System.out.println("   • " + detail.getKey() + ": " + detail.getValue());
                }
            }
        }
        
        System.out.println("\n╚════════════════════════════════════════════════════════════╝");
    }
}
