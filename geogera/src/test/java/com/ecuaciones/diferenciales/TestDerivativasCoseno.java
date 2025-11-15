package com.ecuaciones.diferenciales;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;
import com.ecuaciones.diferenciales.utils.SymjaEngine;

/**
 * Test para verificar que las derivadas de cos(2x) y sin(2x) funcionan correctamente.
 */
public class TestDerivativasCoseno {

    @Test
    public void testDerivativaConversionSymja() {
        System.out.println("\n🔧 TEST: Conversión a Sintaxis Symja");
        System.out.println("═".repeat(60));
        
        String[] testCases = {
            "cos(2x)",
            "sin(2x)",
            "cos(x)",
            "sin(x)",
            "e^x",
            "e^(2x)"
        };
        
        for (String expr : testCases) {
            String symjaExpr = SymjaEngine.convertToSymjaSyntax(expr);
            System.out.println("  " + expr + " → " + symjaExpr);
        }
    }
    
    @Test
    public void testDerivativaSimple() {
        System.out.println("\n🔍 TEST: Primera Derivada - Casos Simples");
        System.out.println("═".repeat(60));
        
        String[] testCases = {
            "cos(x)",
            "sin(x)",
            "e^x"
        };
        
        for (String expr : testCases) {
            String deriv = SymbolicDifferentiator.differentiate(expr, 1);
            System.out.println("  d/dx[" + expr + "] = " + deriv);
            assertNotNull(deriv, "Derivada no debería ser null para: " + expr);
            assertNotEquals("0", deriv.trim(), "Derivada de " + expr + " no debería ser 0");
        }
    }
    
    @Test
    public void testDerivativaCos2x() {
        System.out.println("\n🎯 TEST: Derivada de cos(2x)");
        System.out.println("═".repeat(60));
        
        String expr = "cos(2x)";
        String deriv = SymbolicDifferentiator.differentiate(expr, 1);
        
        System.out.println("  Expresión original: " + expr);
        System.out.println("  Derivada obtenida: " + deriv);
        System.out.println("  Tipo: " + (deriv != null ? deriv.getClass().getSimpleName() : "NULL"));
        
        assertNotNull(deriv, "Derivada de cos(2x) no debería ser null");
        assertNotEquals("0", deriv.trim(), "Derivada de cos(2x) es -2*sin(2x), no 0");
        assertTrue(deriv.contains("sin") || deriv.contains("Sin"), 
                  "Derivada debería contener sin/Sin");
        assertFalse(deriv.trim().equals("0"), "Derivada no debería ser exactamente 0");
    }
    
    @Test
    public void testDerivativaSin2x() {
        System.out.println("\n🎯 TEST: Derivada de sin(2x)");
        System.out.println("═".repeat(60));
        
        String expr = "sin(2x)";
        String deriv = SymbolicDifferentiator.differentiate(expr, 1);
        
        System.out.println("  Expresión original: " + expr);
        System.out.println("  Derivada obtenida: " + deriv);
        
        assertNotNull(deriv, "Derivada de sin(2x) no debería ser null");
        assertNotEquals("0", deriv.trim(), "Derivada de sin(2x) es 2*cos(2x), no 0");
        assertTrue(deriv.contains("cos") || deriv.contains("Cos"), 
                  "Derivada debería contener cos/Cos");
    }
}
