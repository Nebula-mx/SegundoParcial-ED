package com.ecuaciones.diferenciales.solver.homogeneous;

import com.ecuaciones.diferenciales.model.roots.Root;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Genera la cadena de texto final para la Solución Homogénea (y_h)
 * a partir de una lista de raíces consolidadas.
 */
public class HomogeneousSolver {
    
    private static final double TOLERANCE = 1e-9; 
    // Usar #.#### para la precisión, pero mantener un formato simple.
    private static final DecimalFormat DF = new DecimalFormat("#.####", new java.text.DecimalFormatSymbols(Locale.US));

    /**
     * Formatea un valor para ser usado como coeficiente o argumento de x/trig.
     * CRÍTICO: No incluye el signo si el valor es negativo, ese manejo se hace en el ensamblaje.
     */
    private String formatArgumentValue(double value) {
        double absValue = Math.abs(value);
        if (absValue < TOLERANCE) return "0";
        
        // Si el valor es ~1 o ~-1, retorna ""
        if (Math.abs(absValue - 1.0) < TOLERANCE) return ""; 
        
        // Si es un entero, formatea como entero
        if (Math.abs(absValue - Math.round(absValue)) < TOLERANCE) return String.valueOf(Math.round(absValue));
        
        // Formato decimal estándar
        return DF.format(absValue);
    }
    
    /**
     * Genera la solución y_h sumando todos los términos.
     */
    public String generateHomogeneousSolution(List<Root> finalRoots) {
        if (finalRoots == null || finalRoots.isEmpty()) return "0";
        
        List<String> terms = new ArrayList<>();
        int constantIndex = 1;

        for (Root root : finalRoots) {
            double alpha = root.getReal();
            double beta = root.getImaginary(); // Siempre >= 0
            int multiplicity = root.getMultiplicity();

            // ----------------------------------------------------
            // CRÍTICO: Factor Exponencial Común (e^(alpha*x))
            // ----------------------------------------------------
            String expFactor = "";
            if (Math.abs(alpha) >= TOLERANCE) {
                String absAlphaStr = formatArgumentValue(alpha);
                String sign = alpha < 0 ? "-" : "";
                String expArg = absAlphaStr.isEmpty() ? "x" : absAlphaStr + "x"; 
                expFactor = "e^(" + sign + expArg + ")";
            }

            // ----------------------------------------------------
            // Caso 1: Raíces Reales (r = alpha)
            // ----------------------------------------------------
            if (root.isReal() || Math.abs(beta) < TOLERANCE) {
                
                for (int m = 0; m < multiplicity; m++) {
                    String Ci = "C" + constantIndex++;
                    
                    // a) Factor x^m (para multiplicidad)
                    String xFactor = m == 0 ? "" : (m == 1 ? "x" : "x^" + m);
                    
                    // b) Ensamblaje del Término
                    String term = Ci;
                    if (!xFactor.isEmpty()) {
                        term += " * " + xFactor;
                    }
                    if (!expFactor.isEmpty()) {
                        term += " * " + expFactor;
                    }
                    
                    terms.add(term);
                }
            } 
            // ----------------------------------------------------
            // Caso 2: Raíces Complejas (alpha +/- beta*i, beta > 0)
            // ----------------------------------------------------
            else {
                // Término Común Exterior (solo e^(alpha*x))
                String outerFactor = expFactor.isEmpty() ? "" : expFactor + " * ";

                // Contenedor para la suma de todos los términos repetidos
                List<String> innerTerms = new ArrayList<>();
                
                // Argumento trigonométrico (beta*x)
                String absBetaStr = formatArgumentValue(beta); 
                String trigArg = absBetaStr.isEmpty() ? "x" : absBetaStr + "x";
                
                // Iteración por multiplicidad (k = 1...n)
                for (int m = 0; m < multiplicity; m++) {
                    // Ck y Ck+1 (dos constantes por par de raíces repetidas)
                    String Ci1 = "C" + constantIndex++;
                    String Ci2 = "C" + constantIndex++;
                    
                    // Factor x^m
                    String xFactor = m == 0 ? "" : (m == 1 ? "x" : "x^" + m);
                    
                    // Suma interna: x^m * (Ci cos(...) + Cj sin(...))
                    String cosTerm = Ci1 + " * cos(" + trigArg + ")";
                    String sinTerm = Ci2 + " * sin(" + trigArg + ")";
                    
                    String termPair = "(" + cosTerm + " + " + sinTerm + ")";
                    
                    if (!xFactor.isEmpty()) {
                         termPair = xFactor + " * " + termPair;
                    }

                    innerTerms.add(termPair);
                }
                
                // Ensamblaje final del término complejo
                String term = outerFactor + "(" + String.join(" + ", innerTerms) + ")";
                terms.add(term);
            }
        }
        
        // Unir todos los términos con " + "
        return String.join(" + ", terms); 
    }
}