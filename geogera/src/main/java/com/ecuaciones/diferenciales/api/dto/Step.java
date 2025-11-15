package com.ecuaciones.diferenciales.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * 📋 Representa un PASO en la solución de una ecuación diferencial
 * 
 * Cada paso contiene:
 * - Tipo de paso (CLASSIFY, CHARACTERISTIC, SOLVE_ROOTS, etc.)
 * - Descripción legible
 * - Expresiones matemáticas
 * - Explicación
 */
public class Step {
    
    public enum StepType {
        CLASSIFY("Clasificación", "Identificar tipo de EDO"),
        CHARACTERISTIC("Ecuación Característica", "Obtener ecuación auxiliar"),
        FIND_ROOTS("Encontrar Raíces", "Resolver ecuación característica"),
        HOMOGENEOUS_SOLUTION("Solución Homogénea", "Construcción de y_h"),
        PARTICULAR_SOLUTION("Solución Particular", "Construcción de y_p"),
        GENERAL_SOLUTION("Solución General", "Combinación y_h + y_p"),
        APPLY_CONDITIONS("Aplicar Condiciones", "Usar y(x₀)=v₀, y'(x₀)=v₀, ..."),
        FINAL_SOLUTION("Solución Final", "Respuesta particular con CI aplicadas"),
        VERIFICATION("Verificación", "Comprobar solución");
        
        private final String label;
        private final String description;
        
        StepType(String label, String description) {
            this.label = label;
            this.description = description;
        }
        
        public String getLabel() { return label; }
        public String getDescription() { return description; }
    }
    
    private StepType type;
    private String title;
    private String description;
    private List<String> expressions;        // Expresiones matemáticas (LaTex o texto)
    private String explanation;              // Explicación en lenguaje natural
    private Map<String, String> details;     // Detalles adicionales
    private int order;                       // Orden del paso (1, 2, 3, ...)
    
    // --- CONSTRUCTORES ---
    
    public Step() {
        this.expressions = new ArrayList<>();
        this.details = new LinkedHashMap<>();
    }
    
    public Step(StepType type, String title, String explanation) {
        this();
        this.type = type;
        this.title = title;
        this.explanation = explanation;
    }
    
    public Step(StepType type, String title, String explanation, List<String> expressions) {
        this(type, title, explanation);
        this.expressions = expressions != null ? expressions : new ArrayList<>();
    }
    
    // --- BUILDER PATTERN ---
    
    public Step withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public Step withExpression(String expr) {
        this.expressions.add(expr);
        return this;
    }
    
    public Step withExpressions(List<String> exprs) {
        this.expressions.addAll(exprs != null ? exprs : Collections.emptyList());
        return this;
    }
    
    public Step withDetail(String key, String value) {
        this.details.put(key, value);
        return this;
    }
    
    public Step withOrder(int order) {
        this.order = order;
        return this;
    }
    
    // --- GETTERS / SETTERS ---
    
    @JsonProperty("type")
    public String getTypeString() {
        return type != null ? type.name() : null;
    }
    
    public StepType getType() {
        return type;
    }
    
    public void setType(StepType type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getExpressions() {
        return expressions;
    }
    
    public void setExpressions(List<String> expressions) {
        this.expressions = expressions != null ? expressions : new ArrayList<>();
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
        this.details = details != null ? details : new LinkedHashMap<>();
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    // --- HELPERS ---
    
    @Override
    public String toString() {
        return String.format("[Paso %d] %s: %s", order, title, explanation);
    }
}
