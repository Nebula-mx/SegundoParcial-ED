package com.ecuaciones.diferenciales;

import java.util.List;
import java.util.ArrayList;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.variation.WronskianCalculator;

/**
 * Test para verificar que el Wronskiano se calcula correctamente
 * después de arreglar SymbolicDifferentiator
 */
public class TestWronskianoVP {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        TEST: WRONSKIANO CON DERIVADAS CORREGIDAS           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Caso: y'' + 4y = 8cos(2x)
        // Raíces: 2i, -2i
        // Conjunto fundamental: {cos(2x), sin(2x)}
        // W(x) = cos(2x) * (sin(2x))' - sin(2x) * (cos(2x))'
        //      = cos(2x) * 2cos(2x) - sin(2x) * (-2sin(2x))
        //      = 2cos²(2x) + 2sin²(2x)
        //      = 2(cos²(2x) + sin²(2x))
        //      = 2
        
        List<Root> roots = new ArrayList<>();
        roots.add(new Root(0, 2, 1));   // 0 + 2i
        roots.add(new Root(0, -2, 1));  // 0 - 2i
        
        WronskianCalculator wc = new WronskianCalculator(roots);
        
        System.out.println("📋 Ecuación: y'' + 4y = 8cos(2x)");
        System.out.println("📋 Raíces del Polinomio: 2i, -2i\n");
        
        // Obtener conjunto fundamental
        List<String> fundamentalSet = wc.generateFundamentalSet();
        System.out.println("🔍 Conjunto Fundamental:");
        for (int i = 0; i < fundamentalSet.size(); i++) {
            System.out.println("   y_" + (i+1) + "(x) = " + fundamentalSet.get(i));
        }
        System.out.println();
        
        // Generar matriz de Wronskiano
        List<List<String>> wMatrix = wc.generateWronskianMatrix(fundamentalSet, 2);
        System.out.println("📊 Matriz de Wronskiano W:");
        System.out.println("   ┌                 ┐");
        for (int i = 0; i < wMatrix.size(); i++) {
            System.out.print("   │ ");
            for (int j = 0; j < wMatrix.get(i).size(); j++) {
                System.out.print(String.format("%-20s", wMatrix.get(i).get(j)));
            }
            System.out.println(" │");
        }
        System.out.println("   └                 ┘");
        System.out.println();
        
        // Calcular Wronskiano
        String wronskianFormula = wc.calculateWronskianFormula(wMatrix);
        System.out.println("✨ Fórmula del Wronskiano:");
        System.out.println("   W(x) = " + wronskianFormula);
        System.out.println();
        
        // Validación
        System.out.println("✅ VALIDACIÓN:");
        if (wronskianFormula.contains("0")) {
            System.out.println("   ❌ ERROR: Wronskiano contiene '0' (derivadas mal calculadas)");
        } else {
            System.out.println("   ✅ Wronskiano NO contiene '0' (derivadas correctas)");
        }
        
        if (wronskianFormula.contains("Sin") && wronskianFormula.contains("Cos")) {
            System.out.println("   ✅ Wronskiano contiene Sin y Cos (formato correcto)");
        } else {
            System.out.println("   ⚠️ Verificar formato de Wronskiano");
        }
    }
}
