package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver;
import com.ecuaciones.diferenciales.utils.LinearSystemSolver;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRUEBAS EXHAUSTIVAS DE CONDICIONES INICIALES
 * Verifica funcionamiento correcto en todos los casos
 */
public class ExhaustiveCITest {

    /**
     * Caso 1: Ecuación de Orden 1 con 1 CI en x0=0
     * EDO: y' - y = 0
     * Solución general: y(x) = C1*e^x
     * CI: y(0) = 5
     * Solución particular: y(x) = 5*e^x
     */
    @Test
    void testOrder1_OneCI_x0Zero() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 1: Orden 1, 1 CI en x0=0         ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1*e^(1*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 1);
        
        // CI: y(0) = 5
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 5.0));
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y' - y = 0");
        System.out.println("✓ Solución general: C1*e^x");
        System.out.println("✓ CI: y(0) = 5");
        System.out.println("→ Resultado: C1 = " + constants.get("C1"));
        assertEquals(5.0, constants.get("C1"), 1e-5, "C1 debe ser 5.0");
        System.out.println("✅ PASSOU");
    }

    /**
     * Caso 2: Ecuación de Orden 2 con 2 CI en x0=0
     * EDO: y'' - 5y' + 6y = 0
     * Solución general: y(x) = C1*e^(3x) + C2*e^(2x)
     * CI: y(0)=1, y'(0)=2
     * Esperado: C1 ≈ 1.036, C2 ≈ -0.036
     */
    @Test
    void testOrder2_TwoCI_x0Zero() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 2: Orden 2, 2 CI en x0=0         ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1*e^(3*x) + C2*e^(2*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
        
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));    // y(0) = 1
        conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 2.0));    // y'(0) = 2
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y'' - 5y' + 6y = 0");
        System.out.println("✓ Solución general: C1*e^(3x) + C2*e^(2x)");
        System.out.println("✓ CI: y(0) = 1, y'(0) = 2");
        System.out.println("→ Resultado: C1 = " + String.format("%.4f", constants.get("C1")) + 
                          ", C2 = " + String.format("%.4f", constants.get("C2")));
        
        // Verificar que C1 + C2 = 1 (evaluación en x=0 de la solución general)
        assertEquals(1.0, constants.get("C1") + constants.get("C2"), 1e-4, 
                     "C1 + C2 debe ser 1.0");
        // Verificar que 3*C1 + 2*C2 = 2 (derivada en x=0)
        assertEquals(2.0, 3*constants.get("C1") + 2*constants.get("C2"), 1e-4, 
                     "3*C1 + 2*C2 debe ser 2.0");
        System.out.println("✅ PASSOU");
    }

    /**
     * Caso 3: Ecuación de Orden 3 con 3 CI en x0=0
     * EDO: y''' - 6y'' + 11y' - 6y = 0
     * Raíces: 1, 2, 3
     * Solución general: y(x) = C1*e^x + C2*e^(2x) + C3*e^(3x)
     * CI: y(0)=1, y'(0)=0, y''(0)=1
     */
    @Test
    void testOrder3_ThreeCI_x0Zero() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 3: Orden 3, 3 CI en x0=0         ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1*e^(1*x) + C2*e^(2*x) + C3*e^(3*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 3);
        
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));    // y(0) = 1
        conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 0.0));    // y'(0) = 0
        conditions.add(new InitialConditionsSolver.InitialCondition(2, 0.0, 1.0));    // y''(0) = 1
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y''' - 6y'' + 11y' - 6y = 0");
        System.out.println("✓ Solución general: C1*e^x + C2*e^(2x) + C3*e^(3x)");
        System.out.println("✓ CI: y(0)=1, y'(0)=0, y''(0)=1");
        System.out.println("→ Resultado: C1 = " + String.format("%.4f", constants.get("C1")) + 
                          ", C2 = " + String.format("%.4f", constants.get("C2")) +
                          ", C3 = " + String.format("%.4f", constants.get("C3")));
        
        // Verificar que C1 + C2 + C3 = 1 (y(0) = 1)
        assertEquals(1.0, constants.get("C1") + constants.get("C2") + constants.get("C3"), 1e-4, 
                     "C1 + C2 + C3 debe ser 1.0");
        // Verificar que 1*C1 + 2*C2 + 3*C3 = 0 (y'(0) = 0)
        assertEquals(0.0, 1*constants.get("C1") + 2*constants.get("C2") + 3*constants.get("C3"), 1e-4, 
                     "1*C1 + 2*C2 + 3*C3 debe ser 0");
        // Verificar que 1*C1 + 4*C2 + 9*C3 = 1 (y''(0) = 1)
        assertEquals(1.0, 1*constants.get("C1") + 4*constants.get("C2") + 9*constants.get("C3"), 1e-4, 
                     "1*C1 + 4*C2 + 9*C3 debe ser 1");
        System.out.println("✅ PASSOU");
    }

    /**
     * Caso 4: Ecuación de Orden 2 con CI en punto diferente (x0=1)
     * EDO: y'' - 4y = 0
     * Solución general: y(x) = C1*e^(2x) + C2*e^(-2x)
     * CI: y(1)=1, y'(1)=0
     */
    @Test
    void testOrder2_TwoCI_x0NonZero() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 4: Orden 2, 2 CI en x0=1         ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1*e^(2*x) + C2*e^(-2*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
        
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 1.0, 1.0));    // y(1) = 1
        conditions.add(new InitialConditionsSolver.InitialCondition(1, 1.0, 0.0));    // y'(1) = 0
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y'' - 4y = 0");
        System.out.println("✓ Solución general: C1*e^(2x) + C2*e^(-2x)");
        System.out.println("✓ CI: y(1) = 1, y'(1) = 0");
        System.out.println("→ Resultado: C1 = " + String.format("%.4f", constants.get("C1")) + 
                          ", C2 = " + String.format("%.4f", constants.get("C2")));
        
        double e2 = Math.exp(2);
        double eNeg2 = Math.exp(-2);
        
        // Verificar que C1*e^2 + C2*e^(-2) = 1 (y(1) = 1)
        assertEquals(1.0, constants.get("C1")*e2 + constants.get("C2")*eNeg2, 1e-4, 
                     "C1*e^2 + C2*e^(-2) debe ser 1.0");
        // Verificar que 2*C1*e^2 - 2*C2*e^(-2) = 0 (y'(1) = 0)
        assertEquals(0.0, 2*constants.get("C1")*e2 - 2*constants.get("C2")*eNeg2, 1e-4, 
                     "2*C1*e^2 - 2*C2*e^(-2) debe ser 0");
        System.out.println("✅ PASSOU");
    }

    /**
     * Caso 5: Ecuación de Orden 2 con soluciones complejas (trigonométricas)
     * EDO: y'' + 4y = 0
     * Solución general: y(x) = C1*cos(2x) + C2*sin(2x)
     * CI: y(0)=1, y'(0)=1
     */
    @Test
    void testOrder2_ComplexRoots_Trigonometric() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 5: Orden 2, raíces complejas       ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1*cos(2*x) + C2*sin(2*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
        
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));    // y(0) = 1
        conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 1.0));    // y'(0) = 1
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y'' + 4y = 0");
        System.out.println("✓ Solución general: C1*cos(2x) + C2*sin(2x)");
        System.out.println("✓ CI: y(0)=1, y'(0)=1");
        System.out.println("→ Resultado: C1 = " + String.format("%.4f", constants.get("C1")) + 
                          ", C2 = " + String.format("%.4f", constants.get("C2")));
        
        // Verificar que C1 = 1 (y(0) = 1, cos(0)=1, sin(0)=0)
        assertEquals(1.0, constants.get("C1"), 1e-4, "C1 debe ser 1.0");
        // Verificar que -2*C1*sin(0) + 2*C2*cos(0) = 1 => 2*C2 = 1 => C2 = 0.5 (y'(0) = 1)
        assertEquals(0.5, constants.get("C2"), 1e-4, "C2 debe ser 0.5");
        System.out.println("✅ PASSOU");
    }

    /**
     * Caso 6: Orden 4 con 4 CI
     * EDO: y'''' - y = 0
     * Raíces: 1, -1, i, -i
     * Solución general: y(x) = C1*e^x + C2*e^(-x) + C3*cos(x) + C4*sin(x)
     * CI: y(0)=0, y'(0)=1, y''(0)=0, y'''(0)=-1
     */
    @Test
    void testOrder4_FourCI() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 6: Orden 4, 4 CI                  ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1*e^(1*x) + C2*e^(-1*x) + C3*cos(1*x) + C4*sin(1*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 4);
        
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 0.0));    // y(0) = 0
        conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 1.0));    // y'(0) = 1
        conditions.add(new InitialConditionsSolver.InitialCondition(2, 0.0, 0.0));    // y''(0) = 0
        conditions.add(new InitialConditionsSolver.InitialCondition(3, 0.0, -1.0));   // y'''(0) = -1
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y'''' - y = 0");
        System.out.println("✓ Solución general: C1*e^x + C2*e^(-x) + C3*cos(x) + C4*sin(x)");
        System.out.println("✓ CI: y(0)=0, y'(0)=1, y''(0)=0, y'''(0)=-1");
        System.out.println("→ Resultado:");
        System.out.println("   C1 = " + String.format("%.4f", constants.get("C1")));
        System.out.println("   C2 = " + String.format("%.4f", constants.get("C2")));
        System.out.println("   C3 = " + String.format("%.4f", constants.get("C3")));
        System.out.println("   C4 = " + String.format("%.4f", constants.get("C4")));
        
        // Verificar que C1 + C2 + C3 = 0 (y(0) = 0)
        assertEquals(0.0, constants.get("C1") + constants.get("C2") + constants.get("C3"), 1e-4, 
                     "C1 + C2 + C3 debe ser 0");
        System.out.println("✅ PASSOU");
    }

    /**
     * Caso 7: Valores negativos en CI
     * EDO: y'' - y = 0
     * Solución general: y(x) = C1*e^x + C2*e^(-x)
     * CI: y(0)=-1, y'(0)=-2
     */
    @Test
    void testNegativeValues() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 7: Valores negativos               ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1*e^(1*x) + C2*e^(-1*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
        
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, -1.0));    // y(0) = -1
        conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, -2.0));    // y'(0) = -2
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y'' - y = 0");
        System.out.println("✓ Solución general: C1*e^x + C2*e^(-x)");
        System.out.println("✓ CI: y(0)=-1, y'(0)=-2");
        System.out.println("→ Resultado: C1 = " + String.format("%.4f", constants.get("C1")) + 
                          ", C2 = " + String.format("%.4f", constants.get("C2")));
        
        // Verificar que C1 + C2 = -1 (y(0) = -1)
        assertEquals(-1.0, constants.get("C1") + constants.get("C2"), 1e-4, 
                     "C1 + C2 debe ser -1.0");
        // Verificar que C1 - C2 = -2 (y'(0) = -2)
        assertEquals(-2.0, constants.get("C1") - constants.get("C2"), 1e-4, 
                     "C1 - C2 debe ser -2.0");
        System.out.println("✅ PASSOU");
    }

    /**
     * Caso 8: CI con puntos múltiples diferentes
     * EDO: y'' - y' = 0
     * Solución general: y(x) = C1 + C2*e^x
     * CI: y(0)=1, y(1)=3
     */
    @Test
    void testMultiplePoints() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ CASO 8: CI en puntos múltiples          ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        String generalSolution = "C1 + C2*e^(1*x)";
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
        
        List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));    // y(0) = 1
        conditions.add(new InitialConditionsSolver.InitialCondition(0, 1.0, 3.0));    // y(1) = 3
        
        Map<String, Double> constants = solver.solveInitialConditions(conditions);
        
        System.out.println("✓ Ecuación: y'' - y' = 0");
        System.out.println("✓ Solución general: C1 + C2*e^x");
        System.out.println("✓ CI: y(0)=1, y(1)=3");
        System.out.println("→ Resultado: C1 = " + String.format("%.4f", constants.get("C1")) + 
                          ", C2 = " + String.format("%.4f", constants.get("C2")));
        
        double e1 = Math.exp(1);
        
        // Verificar que C1 + C2 = 1 (y(0) = 1)
        assertEquals(1.0, constants.get("C1") + constants.get("C2"), 1e-4, 
                     "C1 + C2 debe ser 1.0");
        // Verificar que C1 + C2*e = 3 (y(1) = 3)
        assertEquals(3.0, constants.get("C1") + constants.get("C2")*e1, 1e-4, 
                     "C1 + C2*e debe ser 3.0");
        System.out.println("✅ PASSOU");
    }

    /**
     * RESUMEN EJECUTIVO
     */
    @Test
    void runAllTests() {
        System.out.println("\n\n");
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         PRUEBAS EXHAUSTIVAS DE CONDICIONES INICIALES      ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        testOrder1_OneCI_x0Zero();
        testOrder2_TwoCI_x0Zero();
        testOrder3_ThreeCI_x0Zero();
        testOrder2_TwoCI_x0NonZero();
        testOrder2_ComplexRoots_Trigonometric();
        testOrder4_FourCI();
        testNegativeValues();
        testMultiplePoints();
        
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         ✅ TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE     ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
    }
}
