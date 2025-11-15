package com.ecuaciones.diferenciales.api.dto;

import java.util.*;

/**
 * 📥 DTO para recibir la solicitud del frontend
 * 
 * Ejemplo:
 * {
 *   "equation": "y'' + 3y' + 2y = e^x",
 *   "initialConditions": ["y(0)=1", "y'(0)=0"],
 *   "variable": "x"
 * }
 */
public class ExpressionData {
    
    private String equation;                    // La EDO a resolver
    private List<String> initialConditions;     // Condiciones iniciales
    private String variable;                    // Variable independiente (x, t, etc.)
    private String description;                 // Descripción opcional
    private String method;                      // Método: "UC" (Coeficientes Indeterminados) o "VP" (Variación de Parámetros)
    
    // --- CONSTRUCTORES ---
    
    public ExpressionData() {
        this.initialConditions = new ArrayList<>();
        this.variable = "x";
    }
    
    public ExpressionData(String equation, List<String> initialConditions, String variable) {
        this.equation = equation;
        this.initialConditions = initialConditions != null ? initialConditions : new ArrayList<>();
        this.variable = variable != null ? variable : "x";
    }
    
    // --- GETTERS / SETTERS ---
    
    public String getEquation() {
        return equation;
    }
    
    public void setEquation(String equation) {
        this.equation = equation;
    }
    
    public List<String> getInitialConditions() {
        return initialConditions;
    }
    
    public void setInitialConditions(List<String> initialConditions) {
        this.initialConditions = initialConditions != null ? initialConditions : new ArrayList<>();
    }
    
    public String getVariable() {
        return variable;
    }
    
    public void setVariable(String variable) {
        this.variable = variable != null ? variable : "x";
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMethod() {
        return method != null ? method : "UC";  // Default a UC
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    // --- VALIDACIÓN ---
    
    /**
     * Validar si los datos de entrada son válidos
     */
    public boolean isValid() {
        return equation != null && !equation.trim().isEmpty() && 
               variable != null && !variable.trim().isEmpty();
    }
    
    /**
     * Validación detallada con mensaje de error específico
     * @return Null si es válido, o mensaje de error descriptivo
     */
    public String getValidationError() {
        // Verificar que la ecuación no esté vacía
        if (equation == null || equation.trim().isEmpty()) {
            return "La ecuación no puede estar vacía";
        }
        
        // Verificar que la variable no esté vacía
        if (variable == null || variable.trim().isEmpty()) {
            return "La variable independiente no puede estar vacía";
        }
        
        // Verificar que la variable sea un solo carácter
        if (variable.trim().length() > 1) {
            return "La variable independiente debe ser un solo carácter (ej: x, t, u)";
        }
        
        // Verificar que contiene operadores básicos de EDO (y, y', y'', etc)
        String eq = equation.toLowerCase();
        if (!eq.contains("y") && !eq.contains("d")) {
            return "La ecuación debe contener una función desconocida (y) o derivadas (d)";
        }
        
        // Verificar que la ecuación no sea demasiado larga
        if (equation.length() > 1000) {
            return "La ecuación es demasiado larga (máximo 1000 caracteres)";
        }
        
        // Si todo está bien, devolver null
        return null;
    }
    
    /**
     * Normalizar la entrada
     */
    public void normalize() {
        if (equation != null) {
            equation = equation.trim();
        }
        if (variable != null) {
            variable = variable.trim();
        }
    }
    
    @Override
    public String toString() {
        return "ExpressionData{" +
                "equation='" + equation + '\'' +
                ", variable='" + variable + '\'' +
                ", initialConditions=" + initialConditions +
                ", description='" + description + '\'' +
                '}';
    }
}
