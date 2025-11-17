package com.ecuaciones.diferenciales.evaluator;

import java.util.*;

/**
 * Clase que representa el resultado completo de la evaluación de una ecuación diferencial
 * Estructura inspirada en Photomath
 */
public class EvaluationResult {
    
    private String status;              // "SUCCESS" o "ERROR"
    private String message;             // Mensaje descriptivo
    private String expression;          // Ecuación original
    private String variable;            // Variable (default: x)
    private List<Step> steps;           // Array de pasos de resolución
    private String finalSolution;       // Solución final en texto
    private String solutionLatex;       // Solución en LaTeX
    private Map<String, Object> metadata;  // Información adicional
    private long executionTimeMs;       // Tiempo de ejecución
    private int stepCount;              // Total de pasos
    private boolean success;            // true si status = SUCCESS
    private String errorDetails;        // Detalles del error si hay
    
    /**
     * Clase interna para representar cada paso
     */
    public static class Step {
        public String type;             // CLASSIFY, CHARACTERISTIC, ROOTS, HOMOGENEOUS_SOLUTION, etc
        public String title;            // Título descriptivo
        public int order;               // Número secuencial del paso
        public String description;      // Descripción adicional
        public List<String> expressions;// Expresiones matemáticas
        public String explanation;      // Explicación en texto
        public Map<String, Object> details; // Información adicional
        
        public Step() {
            this.expressions = new ArrayList<>();
            this.details = new HashMap<>();
        }
        
        public Step(String type, String title, int order) {
            this();
            this.type = type;
            this.title = title;
            this.order = order;
        }
    }
    
    // Constructor
    public EvaluationResult() {
        this.steps = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.executionTimeMs = 0;
        this.stepCount = 0;
        this.success = false;
    }
    
    // ============ Getters y Setters ============
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.success = "SUCCESS".equals(status);
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
        this.steps = steps;
        this.stepCount = steps.size();
    }
    
    public void addStep(Step step) {
        this.steps.add(step);
        this.stepCount = this.steps.size();
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
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public int getStepCount() {
        return stepCount;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorDetails() {
        return errorDetails;
    }
    
    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
    
    // ============ Métodos útiles ============
    
    /**
     * Crear un resultado de error rápidamente
     */
    public static EvaluationResult error(String message, String errorDetails) {
        EvaluationResult result = new EvaluationResult();
        result.setStatus("ERROR");
        result.setMessage(message);
        result.setErrorDetails(errorDetails);
        result.success = false;
        return result;
    }
    
    /**
     * Crear un resultado exitoso rápidamente
     */
    public static EvaluationResult success(String message) {
        EvaluationResult result = new EvaluationResult();
        result.setStatus("SUCCESS");
        result.setMessage(message);
        result.success = true;
        return result;
    }
    
    /**
     * Convertir a JSON string (usando toString simple o JSON library)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"status\": \"").append(status).append("\",\n");
        sb.append("  \"message\": \"").append(message).append("\",\n");
        sb.append("  \"expression\": \"").append(expression).append("\",\n");
        sb.append("  \"variable\": \"").append(variable).append("\",\n");
        sb.append("  \"finalSolution\": \"").append(finalSolution).append("\",\n");
        sb.append("  \"solutionLatex\": \"").append(solutionLatex).append("\",\n");
        sb.append("  \"stepCount\": ").append(stepCount).append(",\n");
        sb.append("  \"executionTimeMs\": ").append(executionTimeMs).append(",\n");
        sb.append("  \"success\": ").append(success).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
