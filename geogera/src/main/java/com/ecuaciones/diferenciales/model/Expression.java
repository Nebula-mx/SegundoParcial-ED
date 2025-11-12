package com.ecuaciones.diferenciales.model;

/**
 * Clase auxiliar que representa un único término en la EDO.
 * Contiene el orden de la derivada y el valor del coeficiente.
 */
public class Expression {
    
    private int order;      
    private double coefficient; 
    private static final double TOLERANCE = 1e-9; // Tolerancia para la comparación de doubles

    public Expression(int order, double coefficient) { 
        this.order = order;
        this.coefficient = coefficient;
    }

    // Getters y Setters (correctos)
    public int getOrder() { return order; }
    public double getCoefficient() { return coefficient; }
    public void setOrder(int order) { this.order = order; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }

    /**
     * Retorna la representación matemática limpia del término (ej: 4y'', -y', y).
     * CRÍTICO: Elimina el '1' y el '*' para una notación estándar.
     */
    @Override
    public String toString() {
        
        // 1. Notación de la Derivada (y, y', y'', y''', etc.)
        String derivativeNotation;
        switch (order) {
            case 0: derivativeNotation = "y"; break;
            case 1: derivativeNotation = "y'"; break;
            case 2: derivativeNotation = "y''"; break;
            case 3: derivativeNotation = "y'''"; break;
            default: derivativeNotation = "y^(" + order + ")";
        }
        
        // 2. Formato del Coeficiente
        String coeffStr;
        
        boolean isOne = Math.abs(coefficient - 1.0) < TOLERANCE;
        boolean isMinusOne = Math.abs(coefficient - (-1.0)) < TOLERANCE;
        boolean isInteger = Math.abs(coefficient - Math.round(coefficient)) < TOLERANCE;

        if (isOne) {
            coeffStr = ""; // Omitir el coeficiente 1 (ej: y'')
        } else if (isMinusOne) {
            coeffStr = "-"; // Mostrar solo "-" para el coeficiente -1 (ej: -y')
        } else if (isInteger) {
             // Formatear como entero (ej: 4.0 -> 4)
             coeffStr = String.valueOf((long) Math.round(coefficient)); 
        } else {
             // Formato decimal estándar
             coeffStr = String.valueOf(coefficient); 
        }
        
        // 3. Concatenación: Coeficiente + Derivada (sin el separador '*')
        return coeffStr + derivativeNotation;
    }
}