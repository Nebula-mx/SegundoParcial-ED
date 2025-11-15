package com.ecuaciones.diferenciales.api.dto;

import java.util.*;
import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 📤 DTO para enviar la respuesta con la solución completa
 * 
 * Contiene:
 * - Pasos detallados de la solución
 * - Solución final
 * - Metadatos
 */
public class SolutionResponse {
    
    public enum Status {
        @SerializedName("success")
        SUCCESS("success"),
        
        @SerializedName("error")
        ERROR("error"),
        
        @SerializedName("partial")
        PARTIAL("partial"),
        
        @SerializedName("unsupported")
        UNSUPPORTED("unsupported");
        
        private final String value;
        Status(String value) { this.value = value; }
        
        @JsonValue
        public String getValue() { return value; }
    }
    
    private Status status;
    private String message;
    private String expression;        // ⭐ La ecuación ingresada
    private String equation;
    private String variable;
    private List<Step> steps;
    private String finalSolution;
    private String solutionLatex;          // Para renderizar en frontend
    private Map<String, String> metadata;  // Información adicional
    private String error;                  // Si status = ERROR
    private long executionTimeMs;          // Tiempo de cálculo
    
    // --- CONSTRUCTORES ---
    
    public SolutionResponse() {
        this.status = Status.SUCCESS;
        this.steps = new ArrayList<>();
        this.metadata = new LinkedHashMap<>();
    }
    
    public SolutionResponse(Status status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
    
    public SolutionResponse(Status status, String message, String error) {
        this(status, message);
        this.error = error;
    }
    
    // --- BUILDER PATTERN ---
    
    public SolutionResponse withEquation(String equation) {
        this.equation = equation;
        this.expression = equation;  // ⭐ También guardar en expression
        return this;
    }
    
    public SolutionResponse withVariable(String variable) {
        this.variable = variable;
        return this;
    }
    
    public SolutionResponse addStep(Step step) {
        if (step != null) {
            step.withOrder(this.steps.size() + 1);
            this.steps.add(step);
        }
        return this;
    }
    
    public SolutionResponse addSteps(List<Step> stepsToAdd) {
        if (stepsToAdd != null) {
            for (Step step : stepsToAdd) {
                addStep(step);
            }
        }
        return this;
    }
    
    public SolutionResponse withFinalSolution(String solution) {
        this.finalSolution = solution;
        return this;
    }
    
    public SolutionResponse withSolutionLatex(String latex) {
        this.solutionLatex = latex;
        return this;
    }
    
    public SolutionResponse withMetadata(String key, String value) {
        this.metadata.put(key, value);
        return this;
    }
    
    public SolutionResponse withExecutionTime(long millis) {
        this.executionTimeMs = millis;
        return this;
    }
    
    // --- GETTERS / SETTERS ---
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    public String getEquation() {
        return equation;
    }
    
    public void setEquation(String equation) {
        this.equation = equation;
    }
    
    public String getVariable() {
        return variable;
    }
    
    public void setVariable(String variable) {
        this.variable = variable;
    }
    
    public List<Step> getSteps() {
        return steps;
    }
    
    public void setSteps(List<Step> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }
    
    public String getFinalSolution() {
        return finalSolution;
    }
    
    public void setFinalSolution(String finalSolution) {
        this.finalSolution = finalSolution;
    }
    
    public String getSolutionLatex() {
        return solutionLatex;
    }
    
    public void setSolutionLatex(String solutionLatex) {
        this.solutionLatex = solutionLatex;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata != null ? metadata : new LinkedHashMap<>();
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    // --- HELPERS ---
    
    public int getStepCount() {
        return steps.size();
    }
    
    @JsonIgnore
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
    
    public void printSummary() {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("📊 SOLUCIÓN: " + equation);
        System.out.println("═══════════════════════════════════════════════════════════");
        
        for (Step step : steps) {
            System.out.println("\n" + step);
            if (step.getExpressions() != null) {
                for (String expr : step.getExpressions()) {
                    System.out.println("  ➜ " + expr);
                }
            }
        }
        
        System.out.println("\n───────────────────────────────────────────────────────────");
        System.out.println("✅ SOLUCIÓN FINAL:");
        System.out.println("  " + finalSolution);
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("⏱️  Tiempo: " + executionTimeMs + " ms");
    }
}
