package com.ecuaciones.diferenciales.dto;

import java.util.*;

/**
 * Respuesta estructurada con pasos detallados (formato Photomath)
 * Cada paso muestra: título, expresiones, explicación y detalles
 */
public class StepResponse {
    private String status;
    private String message;
    private String equation;
    private String variable;
    private List<Step> steps;
    private String finalSolution;
    private String solutionLatex;
    private Map<String, String> metadata;
    private long executionTimeMs;
    private int stepCount;
    private boolean success;
    
    // Constructores
    public StepResponse() {
        this.steps = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.success = false;
    }
    
    public StepResponse(String equation) {
        this();
        this.equation = equation;
        this.variable = "x";
    }
    
    // Métodos para agregar pasos
    public void addStep(Step step) {
        steps.add(step);
    }
    
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }
    
    // Getters y Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
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
        this.steps = steps;
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
        this.metadata = metadata;
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
    
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    // ═══════════════════════════════════════════════════════════
    // Clase interna: Step (cada paso de la resolución)
    // ═══════════════════════════════════════════════════════════
    
    public static class Step {
        private String type;
        private String title;
        private int order;
        private List<String> expressions;
        private String explanation;
        private Map<String, String> details;
        
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
        
        // Métodos helper
        public void addExpression(String expr) {
            expressions.add(expr);
        }
        
        public void addDetail(String key, String value) {
            details.put(key, value);
        }
        
        // Getters y Setters
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public int getOrder() {
            return order;
        }
        
        public void setOrder(int order) {
            this.order = order;
        }
        
        public List<String> getExpressions() {
            return expressions;
        }
        
        public void setExpressions(List<String> expressions) {
            this.expressions = expressions;
        }
        
        public String getExplanation() {
            return explanation;
        }
        
        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
        
        public Map<String, String> getDetails() {
            return details;
        }
        
        public void setDetails(Map<String, String> details) {
            this.details = details;
        }
        
        @Override
        public String toString() {
            return "Step{" +
                    "type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", order=" + order +
                    ", expressions=" + expressions +
                    '}';
        }
    }
}
