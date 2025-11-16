package com.ecuaciones.diferenciales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.*;

/**
 * Respuesta en formato Photomath con pasos detallados
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhotomathResponse {
    private String status;
    private String message;
    private String equation;
    private String variable;
    private List<Step> steps;
    private String finalSolution;
    private String solutionLatex;
    private Map<String, Object> metadata;
    private long executionTimeMs;
    private int stepCount;
    private boolean success;

    public PhotomathResponse() {
        this.steps = new ArrayList<>();
        this.metadata = new LinkedHashMap<>();
        this.success = false;
    }

    // ============ GETTERS Y SETTERS ============

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

    public void addStep(Step step) {
        step.setOrder(this.steps.size() + 1);
        this.steps.add(step);
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

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // ============ CLASE INTERNA: Step ============

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Step {
        private String type;           // CLASSIFY, CHARACTERISTIC, ROOTS, HOMOGENEOUS_SOLUTION, PARTICULAR_SOLUTION, FINAL_SOLUTION
        private String title;          // Título descriptivo con emoji
        private int order;             // Orden del paso
        private List<String> expressions;  // Expresiones matemáticas
        private String explanation;    // Explicación en texto
        private Map<String, Object> details;  // Detalles adicionales

        public Step() {
            this.expressions = new ArrayList<>();
            this.details = new LinkedHashMap<>();
        }

        public Step(String type, String title, String explanation) {
            this();
            this.type = type;
            this.title = title;
            this.explanation = explanation;
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

        public void addExpression(String expr) {
            this.expressions.add(expr);
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public Map<String, Object> getDetails() {
            return details;
        }

        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }

        public void addDetail(String key, Object value) {
            this.details.put(key, value);
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

    @Override
    public String toString() {
        return "PhotomathResponse{" +
                "status='" + status + '\'' +
                ", equation='" + equation + '\'' +
                ", finalSolution='" + finalSolution + '\'' +
                ", stepCount=" + stepCount +
                ", success=" + success +
                '}';
    }
}
