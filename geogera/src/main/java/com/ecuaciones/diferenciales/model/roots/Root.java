package com.ecuaciones.diferenciales.model.roots;

import java.util.Locale;

/**
 * Representa una raíz de la ecuación característica (real o compleja) 
 * con su multiplicidad. Almacena la raíz principal (alpha + |beta|*i).
 */
public class Root {

    private double real;
    private double imaginary; // Almacenada como |beta| >= 0
    private int multiplicity;
    
    // Se recomienda usar el mismo nombre de constante en todo el proyecto.
    private static final double TOLERANCE = 1e-9; 

    private double cleanValue(double value) {
        // Usa una tolerancia para tratar números muy pequeños como cero.
        return Math.abs(value) < TOLERANCE ? 0.0 : value;
    }

    public Root(double real, double imaginary, int multiplicity) {
        this.real = cleanValue(real);
        // Almacenar la parte imaginaria con signo (permitir conjugados distintos)
        this.imaginary = cleanValue(imaginary);
        this.multiplicity = multiplicity;
    }

    // Getters
    public double getReal() { return real; }
    public double getImaginary() { return imaginary; }
    public int getMultiplicity() { return multiplicity; }
    public boolean isReal() { return Math.abs(imaginary) < TOLERANCE; }

    // Setter (necesario para la consolidación de multiplicidad)
    public void setMultiplicity(int multiplicity) { 
        this.multiplicity = multiplicity; 
    }

    private String formatValue(double value) {
        if (Math.abs(value) < TOLERANCE) {
            return "0";
        }
        // Formateo limpio para enteros y decimales (incluyendo el signo si es negativo)
        if (Math.abs(value - Math.round(value)) < TOLERANCE) {
            return String.valueOf(Math.round(value)); 
        }
        return String.format(Locale.US, "%.4f", value); 
    }
    
    // ----------------------- Lógica de Igualdad para Consolidación y Resonancia --------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Root root = (Root) o;
        
        // Las raíces son iguales si las partes real e imaginaria (magnitud) son iguales dentro de la tolerancia.
        // La multiplicidad NO se considera para la igualdad de la raíz.
        return Math.abs(real - root.real) < TOLERANCE &&
               Math.abs(imaginary - root.imaginary) < TOLERANCE;
    }

    @Override
    public int hashCode() {
        // Usa los bits de los doubles como base para el hash, esencial para usar Root en Mapas/Sets.
        long realBits = Double.doubleToLongBits(real);
        long imagBits = Double.doubleToLongBits(imaginary);
        // Combina los hashes.
        return (int) (realBits ^ (realBits >>> 32) ^ imagBits ^ (imagBits >>> 32));
    }

    // ----------------------- Representación en Cadena (Output) --------------------------
    
    @Override
    public String toString() {
        String realStr = formatValue(real);
        String rootStr;
        
        if (isReal()) {
            // Caso Real: r
            rootStr = realStr;
        } else {
            // Caso Complejo: alpha +/- beta*i
            String imagPart;
            
            // Determinar la parte i (i, Bi)
            if (Math.abs(imaginary - 1.0) < TOLERANCE) {
                 imagPart = "i";
            } else {
                 imagPart = formatValue(imaginary) + "i";
            }
            
            // 1. Manejar caso Raíz Pura (0 +/- Bi)
            if (Math.abs(real) < TOLERANCE) {
                // Genera +Bi y -Bi usando la magnitud de la parte imaginaria
                String mag = (Math.abs(imaginary - 1.0) < TOLERANCE) ? "i" : formatValue(Math.abs(imaginary)) + "i";
                String rootStr1 = mag;
                String rootStr2 = "-" + mag;
                rootStr = rootStr1 + ", " + rootStr2;
            } else {
                // 2. Manejar caso Raíz Compleja General (Alpha +/- Bi)
                // Genera (Alpha + Bi) y (Alpha - Bi) usando magnitud para la parte imaginaria
                String mag = (Math.abs(imaginary - 1.0) < TOLERANCE || Math.abs(imaginary + 1.0) < TOLERANCE) ? "i" : formatValue(Math.abs(imaginary)) + "i";
                String rootStr1 = realStr + " + " + mag;
                String rootStr2 = realStr + " - " + mag;
                rootStr = rootStr1 + ", " + rootStr2;
            }
        }
        
        // Añadir la multiplicidad si es mayor a 1
        return rootStr + (multiplicity > 1 ? " (mult: " + multiplicity + ")" : "");
    }
}