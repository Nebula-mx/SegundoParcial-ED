package com.ecuaciones.diferenciales.model.templates;

/**
 * Define los tipos de funciones que componen el término no homogéneo g(x) 
 * para el método de Coeficientes Indeterminados (UC). Estos tipos generan
 * la forma de la solución particular y_p.
 */
public enum FunctionType {
    // Componentes fundamentales (grado 0)
    CONSTANT,      // Caso especial: constante pura (e.g., 5). Genera A.
    
    // Formas básicas
    POLYNOMIAL,    // Polinomio simple P(x) (e.g., x^2 + 2x). Genera un polinomio de grado m.
    EXPONENTIAL,   // Exponencial simple e^(ax) (e.g., e^3x). Genera A*e^ax.
    SINUSOIDAL,    // Trigonométrica simple sin(bx) o cos(bx) (e.g., cos(4x)). Genera A*cos(bx) + B*sin(bx).
    
    // Formas complejas / Compuestas
    PRODUCT,       // Producto de las anteriores, incluye la forma completa P(x)e^(ax)sin(bx)
    SUM,           // Indica que la solución y_p es la suma de formas anteriores (Principio de Superposición)
    
    // Fallback
    UNKNOWN        // Función no soportada por el método (e.g., tan(x), ln(x), 1/x).
}