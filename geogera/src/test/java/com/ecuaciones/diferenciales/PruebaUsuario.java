package com.ecuaciones.diferenciales;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;

public class PruebaUsuario {
    
    @Test
    public void pruebaCubica() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                   PRUEBA: y''' + 2y'' + y = 20x² + 40                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════╝\n");
        
        try {
            // Ecuación: y''' + 2y'' + y = 20x² + 40
            String ecuacion = "y''' + 2*y'' + y = 20*x^2 + 40";
            
            System.out.println("📝 ECUACIÓN INGRESADA: " + ecuacion);
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // 1. Parsear la ecuación
            EcuationParser parser = new EcuationParser();
            ExpressionData data = parser.parse(ecuacion);
            
            Double[] coeffsArray = data.getCoefficients();
            int order = data.getOrder();
            String gx = data.getIndependentTerm().get("g(x)");
            
            System.out.println("📊 INFORMACIÓN EXTRAÍDA:");
            System.out.println("   ├─ Orden de la EDO: " + order);
            System.out.println("   ├─ Coeficientes [a₂, a₁, a₀]: " + Arrays.toString(coeffsArray));
            System.out.println("   ├─ Forzamiento g(x): " + gx);
            System.out.println("   └─ Es homogénea: " + data.getIsHomogeneous());
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // 2. Resolver raíces características
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> roots = PolynomialSolver.solve(coeffs);
            
            System.out.println("✅ PASO 1: ECUACIÓN CARACTERÍSTICA");
            System.out.println("   Ecuación: r³ + 2r² + r = 0");
            System.out.println("   Factorizada: r(r² + 2r + 1) = r(r + 1)² = 0");
            System.out.println("   Raíces encontradas:");
            for (Root root : roots) {
                System.out.println("   ├─ " + root);
            }
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // 3. Generar solución homogénea
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String yh = hSolver.generateHomogeneousSolution(roots);
            
            System.out.println("✅ PASO 2: SOLUCIÓN HOMOGÉNEA (y_h)");
            System.out.println("   Raíz r = 0 con multiplicidad 1");
            System.out.println("   Raíz r = -1 con multiplicidad 2");
            System.out.println("   y_h(x) = " + yh);
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // 4. Solución particular por Coeficientes Indeterminados
            System.out.println("✅ PASO 3: SOLUCIÓN PARTICULAR (y_p) - Coeficientes Indeterminados");
            System.out.println("   Forzamiento: g(x) = 20x² + 40 (polinomio grado 2)");
            
            UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
            String ypForm = ucSolver.getParticularSolutionForm(gx);
            
            System.out.println("   ├─ Tipo detectado: POLINOMIAL");
            System.out.println("   ├─ Grado: 2");
            System.out.println("   ├─ ¿Hay resonancia? NO (el polinomio no es raíz)");
            System.out.println("   ├─ Forma propuesta y_p: " + ypForm);
            System.out.println("   └─ Coeficientes a resolver: " + ucSolver.getCoeffNames());
            System.out.println();
            
            // 5. Resolver el sistema lineal
            UndeterminedCoeffResolver resolver = new UndeterminedCoeffResolver(data, ucSolver);
            Map<String, Double> solvedCoeffs = resolver.resolveCoefficients();
            
            System.out.println("   📐 SISTEMA LINEAL RESUELTO:");
            System.out.println("      Coeficientes encontrados:");
            for (Map.Entry<String, Double> entry : solvedCoeffs.entrySet()) {
                System.out.println("      ├─ " + entry.getKey() + " = " + entry.getValue());
            }
            System.out.println();
            
            // 6. Generar solución particular
            String yp = ucSolver.generateParticularSolution(ypForm, solvedCoeffs);
            
            System.out.println("   ✓ Solución particular calculada:");
            System.out.println("   y_p(x) = " + yp);
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            // 7. Solución general
            System.out.println("✅ PASO 4: SOLUCIÓN GENERAL");
            String ygeneral = yh + " + " + yp;
            System.out.println("   y(x) = y_h(x) + y_p(x)");
            System.out.println("   y(x) = " + ygeneral);
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                          ✅ PRUEBA COMPLETADA ✅                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════╝\n");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
