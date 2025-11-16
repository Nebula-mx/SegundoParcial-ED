package com.ecuaciones.diferenciales.model.solver.nonhomogeneous;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolvedor especial para casos de RESONANCIA PURA en UC.
 * Para ecuaciones del tipo: a*y'' + b*y' + c*y = A*cos(bx) o A*sin(bx)
 * donde ±bi son raíces del polinomio característico.
 */
public class ResonanceUCResolver {
    
    private static final double TOLERANCE = 1e-9;
    
    /**
     * Resuelve resonancia simple para ecuación: y'' + w²y = A*cos(wx) + B*sin(wx)
     * Solución: y_p = x*(C*cos(wx) + D*sin(wx))
     * 
     * @param omega Frecuencia de resonancia (w en cos(wx))
     * @param ampCos Amplitud del término cos(wx) en g(x)
     * @param ampSin Amplitud del término sin(wx) en g(x)
     * @return Mapa {C, D} de coeficientes
     */
    public static Map<String, Double> solveSimpleResonance(double omega, double ampCos, double ampSin) {
        Map<String, Double> result = new HashMap<>();
        
        // Para y'' + ω²y = A*cos(ωx) + B*sin(ωx) con resonancia:
        // y_p = x(C*cos(ωx) + D*sin(ωx))
        //  y_p' = C*cos(ωx) - ωx*C*sin(ωx) + D*sin(ωx) + ωx*D*cos(ωx)
        // y_p'' = -ωC*sin(ωx) - ω(C*sin(ωx) + ωx*C*cos(ωx)) + ωD*cos(ωx) + ω(D*cos(ωx) - ωx*D*sin(ωx))
        //      = -2ωC*sin(ωx) - ω²x*C*cos(ωx) + 2ωD*cos(ωx) - ω²x*D*sin(ωx)
        
        // Sustituyendo en y'' + ω²y:
        // (-2ωC*sin(ωx) - ω²xC*cos(ωx) + 2ωD*cos(ωx) - ω²xD*sin(ωx)) + ω²(xC*cos(ωx) + xD*sin(ωx))
        // = -2ωC*sin(ωx) + 2ωD*cos(ωx)
        
        // Igualando a A*cos(ωx) + B*sin(ωx):
        // 2ωD = A  →  D = A/(2ω)
        //-2ωC = B  →  C = -B/(2ω)
        
        double w = Math.abs(omega);
        if (w < TOLERANCE) {
            // Caso degenerad o (ω ≈ 0)
            result.put("C", 0.0);
            result.put("D", 0.0);
            return result;
        }
        
        double C = -ampSin / (2 * w);
        double D = ampCos / (2 * w);
        
        result.put("C", Math.abs(C) < TOLERANCE ? 0.0 : C);
        result.put("D", Math.abs(D) < TOLERANCE ? 0.0 : D);
        
        return result;
    }
    
    /**
     * Versión general para ecuación: a*y'' + b*y' + c*y = g(x)
     * Detecta resonancia y aplica corrección
     */
    public static Map<String, Double> solveResonanceGeneral(
            double a, double b, double c,
            double omega,  // Frecuencia de la solución homogénea (raíz = ±ωi)
            double ampCos, // Amplitud de cos(ωx) en g(x)
            double ampSin  // Amplitud de sin(ωx) en g(x)
    ) {
        // Para ecuación general: a*y'' + b*y' + c*y = A*cos(ωx) + B*sin(ωx)
        // Con b = 0 (caso sin amortiguamiento), c = a*ω²
        // La resonancia es simple: y_p = x*(C*cos(ωx) + D*sin(ωx))
        
        // Caso simple: b = 0 (sin término de primer orden)
        if (Math.abs(b) < TOLERANCE) {
            // y_p' = C*cos(ωx) - ωx*C*sin(ωx) + D*sin(ωx) + ωx*D*cos(ωx)
            // y_p'' = -2ωC*sin(ωx) + 2ωD*cos(ωx) - ω²x*(C*cos(ωx) + D*sin(ωx))
            
            // Sustituyendo:
            // a*[-2ωC*sin(ωx) + 2ωD*cos(ωx) - ω²x*(C*cos(ωx) + D*sin(ωx))]
            // + c*[x*(C*cos(ωx) + D*sin(ωx))]
            // = A*cos(ωx) + B*sin(ωx)
            
            // Si c = a*ω², los términos con x se anulan:
            // -2aωC*sin(ωx) + 2aωD*cos(ωx) = A*cos(ωx) + B*sin(ωx)
            // 2aωD = A  →  D = A/(2aω)
            //-2aωC = B  →  C = -B/(2aω)
            
            double w = Math.abs(omega);
            if (w < TOLERANCE || Math.abs(a) < TOLERANCE) {
                return new HashMap<>();
            }
            
            double denom = 2 * a * w;
            double C = -ampSin / denom;
            double D = ampCos / denom;
            
            Map<String, Double> result = new HashMap<>();
            result.put("C", Math.abs(C) < TOLERANCE ? 0.0 : C);
            result.put("D", Math.abs(D) < TOLERANCE ? 0.0 : D);
            return result;
        }
        
        // Para casos con amortiguamiento (b ≠ 0), es más complejo y requiere solver numérico
        return new HashMap<>();
    }
}
