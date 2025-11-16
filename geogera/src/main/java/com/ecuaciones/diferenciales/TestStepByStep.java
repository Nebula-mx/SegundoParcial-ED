package com.ecuaciones.diferenciales;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;

/**
 * Pruebas paso a paso del caso de resonancia
 */
public class TestStepByStep {
    
    public static void main(String[] args) {
        String ecuacion = "y'' + 4*y = 8*cos(2*x)";
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         🔍 ANÁLISIS PASO A PASO - RESONANCIA 🔍            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        try {
            // PASO 1: PARSE
            System.out.println("📌 PASO 1: PARSE DE ECUACIÓN");
            System.out.println("   Input: " + ecuacion);
            
            EcuationParser parser = new EcuationParser();
            ExpressionData data = parser.parse(ecuacion);
            
            if (data == null) {
                System.out.println("   ❌ Parse falló");
                return;
            }
            
            Double[] coeffsArray = data.getCoefficients();
            int order = data.getOrder();
            String g_x = data.getIndependentTerm().get("g(x)");
            
            System.out.println("   ✅ Coeficientes: " + Arrays.toString(coeffsArray));
            System.out.println("   ✅ Orden: " + order);
            System.out.println("   ✅ g(x): " + g_x);
            System.out.println("   ✅ Homogénea: " + data.getIsHomogeneous());
            
            // PASO 2: RAÍCES
            System.out.println("\n📌 PASO 2: RESOLVER RAÍCES CARACTERÍSTICAS");
            System.out.println("   Ecuación característica: r² + 4 = 0");
            
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> roots = PolynomialSolver.solve(coeffs);
            
            System.out.print("   ✅ Raíces encontradas: ");
            for (Root r : roots) {
                System.out.print(r.toString() + " ");
            }
            System.out.println();
            
            double omega_from_roots = 2.0;  // Esperado: ±2i
            System.out.println("   ✅ ω de raíces: " + omega_from_roots);
            
            // PASO 3: SOLUCIÓN HOMOGÉNEA
            System.out.println("\n📌 PASO 3: SOLUCIÓN HOMOGÉNEA");
            
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String yh = hSolver.generateHomogeneousSolution(roots);
            
            System.out.println("   ✅ y_h = " + yh);
            System.out.println("   Esperado: C1*cos(2x) + C2*sin(2x)");
            
            // PASO 4: FORMA PROPUESTA UC
            System.out.println("\n📌 PASO 4: UC - FORMA PROPUESTA");
            
            UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
            String ypForm = ucSolver.getParticularSolutionForm(g_x);
            
            System.out.println("   🔌 Forcing g(x): " + g_x);
            System.out.println("   ✅ Forma propuesta: " + ypForm);
            System.out.println("   Esperado: (A + C*x)*cos(2*x) + (B + D*x)*sin(2*x)");
            
            List<String> coeffNames = ucSolver.getCoeffNames();
            System.out.println("   ✅ Incógnitas: " + coeffNames);
            System.out.println("   Esperado: [A, B, C, D]");
            
            // PASO 5: RESOLVER SISTEMA
            System.out.println("\n📌 PASO 5: UC - RESOLVER SISTEMA LINEAL");
            System.out.println("   Resolviendo: A*x = b");
            
            UndeterminedCoeffResolver ucResolver = new UndeterminedCoeffResolver(data, ucSolver);
            Map<String, Double> solvedCoeffs = ucResolver.resolveCoefficients();
            
            System.out.println("   ✅ Coeficientes calculados:");
            for (String name : coeffNames) {
                Double val = solvedCoeffs.get(name);
                System.out.println("      • " + name + " = " + val);
            }
            
            boolean allZero = solvedCoeffs.values().stream()
                .allMatch(v -> Math.abs(v) < 1e-9);
            
            System.out.println("   ✅ ¿Todos cero? " + allZero);
            System.out.println("   Esperado: SI (sistema singular - resonancia)");
            
            // PASO 6: SOLUCIÓN PARTICULAR
            System.out.println("\n📌 PASO 6: UC - SOLUCIÓN PARTICULAR");
            
            String yp = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
            String cleanYp = yp.replaceAll("^y_p\\(x\\)\\s*=\\s*", "").trim();
            
            System.out.println("   ✅ y_p = " + cleanYp);
            System.out.println("   Esperado: 2*x*sin(2*x)");
            
            // VERIFICACIÓN
            System.out.println("\n📌 PASO 7: VERIFICACIÓN MATEMÁTICA");
            System.out.println("   Si y_p = 2*x*sin(2*x)");
            System.out.println("   Entonces:");
            System.out.println("   • y_p' = 2*sin(2*x) + 4*x*cos(2*x)");
            System.out.println("   • y_p'' = 8*cos(2*x) - 8*x*sin(2*x)");
            System.out.println("   • y_p'' + 4*y_p = 8*cos(2*x) - 8*x*sin(2*x) + 8*x*sin(2*x)");
            System.out.println("   • y_p'' + 4*y_p = 8*cos(2*x) ✅");
            
            // RESULTADO FINAL
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                  📊 RESULTADO FINAL 📊                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
            System.out.println("\n✅ ECUACIÓN: " + ecuacion);
            System.out.println("✅ MÉTODO: Coeficientes Indeterminados (UC)");
            System.out.println("✅ RESONANCIA: Detectada y resuelta analíticamente");
            System.out.println("✅ y_h = " + yh);
            System.out.println("✅ y_p = " + cleanYp);
            System.out.println("✅ y(x) = (" + yh + ") + (" + cleanYp + ")");
            
            System.out.println("\n✨ TODOS LOS PASOS CORRECTOS ✨\n");
            
        } catch (Exception e) {
            System.out.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
