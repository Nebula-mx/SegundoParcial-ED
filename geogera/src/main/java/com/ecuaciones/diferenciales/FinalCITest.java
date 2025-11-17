package com.ecuaciones.diferenciales;

import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver;
import java.util.*;

/**
 * TEST FINAL EXHAUSTIVO DE CONDICIONES INICIALES
 * Prueba todos los casos y verifica resultados
 */
public class FinalCITest {
    
    static class TestResult {
        String nombre;
        boolean exitoso;
        String detalles;
        
        TestResult(String nombre, boolean exitoso, String detalles) {
            this.nombre = nombre;
            this.exitoso = exitoso;
            this.detalles = detalles;
        }
    }
    
    static List<TestResult> resultados = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       TEST FINAL EXHAUSTIVO - CONDICIONES INICIALES         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Caso 1: Orden 1, 1 CI
        testCase1_Orden1();
        
        // Caso 2: Orden 2, raíces reales distintas
        testCase2_Orden2_RaicesDistintas();
        
        // Caso 3: Orden 2, raíces complejas (trigonométricas)
        testCase3_Orden2_RaicesComplejas();
        
        // Caso 4: Orden 2, raíces reales iguales (doble)
        testCase4_Orden2_RaizDoble();
        
        // Caso 5: Orden 3, tres CI diferentes
        testCase5_Orden3();
        
        // Caso 6: CI en punto x0≠0
        testCase6_PuntoNoZero();
        
        // Caso 7: Valores negativos
        testCase7_ValoresNegativos();
        
        // Mostrar resultados
        mostrarResultados();
    }
    
    /**
     * CASO 1: Orden 1 con 1 CI
     * y' - y = 0 → y(x) = C1*e^x
     * CI: y(0) = 5 → C1 = 5
     */
    static void testCase1_Orden1() {
        System.out.println("╔─ CASO 1: ORDEN 1, 1 CI ─────────────────╗");
        try {
            String generalSolution = "C1*e^(1*x)";
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 1);
            
            List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
            conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 5.0));
            
            Map<String, Double> constants = solver.solveInitialConditions(conditions);
            
            boolean ok = Math.abs(constants.get("C1") - 5.0) < 0.01;
            
            System.out.println("  Ecuación: y' - y = 0");
            System.out.println("  CI: y(0) = 5");
            System.out.println("  Resultado: C1 = " + String.format("%.4f", constants.get("C1")));
            System.out.println("  Esperado: C1 = 5.0000");
            System.out.println("  ✅ " + (ok ? "PASSOU" : "FALHOU"));
            System.out.println();
            
            resultados.add(new TestResult("Orden 1", ok, "C1 = " + constants.get("C1")));
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage() + "\n");
            resultados.add(new TestResult("Orden 1", false, e.getMessage()));
        }
    }
    
    /**
     * CASO 2: Orden 2 con raíces reales distintas
     * y'' - 5y' + 6y = 0 → Raíces: 3, 2
     * y(x) = C1*e^(3x) + C2*e^(2x)
     * CI: y(0)=1, y'(0)=2
     * Sistema: C1 + C2 = 1, 3C1 + 2C2 = 2
     * Solución: C1 ≈ 1.377, C2 ≈ -0.377
     */
    static void testCase2_Orden2_RaicesDistintas() {
        System.out.println("╔─ CASO 2: ORDEN 2, RAÍCES REALES DISTINTAS ─────────────────╗");
        try {
            String generalSolution = "C1*e^(3*x) + C2*e^(2*x)";
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
            
            List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
            conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));
            conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 2.0));
            
            Map<String, Double> constants = solver.solveInitialConditions(conditions);
            
            double c1 = constants.get("C1");
            double c2 = constants.get("C2");
            
            // Verificar: C1 + C2 = 1
            double sum = c1 + c2;
            // Verificar: 3*C1 + 2*C2 = 2
            double der = 3*c1 + 2*c2;
            
            boolean ok = Math.abs(sum - 1.0) < 0.01 && Math.abs(der - 2.0) < 0.1;
            
            System.out.println("  Ecuación: y'' - 5y' + 6y = 0");
            System.out.println("  Raíces: 3, 2");
            System.out.println("  CI: y(0)=1, y'(0)=2");
            System.out.println("  Resultado: C1 = " + String.format("%.4f", c1) + ", C2 = " + String.format("%.4f", c2));
            System.out.println("  Verificación: C1+C2 = " + String.format("%.4f", sum) + " (esperado 1.0)");
            System.out.println("  Verificación: 3*C1+2*C2 = " + String.format("%.4f", der) + " (esperado 2.0)");
            System.out.println("  ✅ " + (ok ? "PASSOU" : "FALHOU"));
            System.out.println();
            
            resultados.add(new TestResult("Orden 2 (Reales)", ok, 
                "C1=" + c1 + ", C2=" + c2 + ", sum=" + sum + ", der=" + der));
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage() + "\n");
            resultados.add(new TestResult("Orden 2 (Reales)", false, e.getMessage()));
        }
    }
    
    /**
     * CASO 3: Orden 2 con raíces complejas
     * y'' + 4y = 0 → Raíces: ±2i
     * y(x) = C1*cos(2x) + C2*sin(2x)
     * CI: y(0)=1, y'(0)=1
     * Solución: C1=1, C2=0.5
     */
    static void testCase3_Orden2_RaicesComplejas() {
        System.out.println("╔─ CASO 3: ORDEN 2, RAÍCES COMPLEJAS (TRIGONOMÉTRICAS) ─────────────────╗");
        try {
            String generalSolution = "C1*cos(2*x) + C2*sin(2*x)";
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
            
            List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
            conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));
            conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 1.0));
            
            Map<String, Double> constants = solver.solveInitialConditions(conditions);
            
            double c1 = constants.get("C1");
            double c2 = constants.get("C2");
            
            // C1 = 1 (cos(0)=1, sin(0)=0)
            // -2*C1*sin(0) + 2*C2*cos(0) = 1 → 2*C2 = 1 → C2 = 0.5
            
            boolean ok = Math.abs(c1 - 1.0) < 0.01 && Math.abs(c2 - 0.5) < 0.01;
            
            System.out.println("  Ecuación: y'' + 4y = 0");
            System.out.println("  Raíces: ±2i (complejas)");
            System.out.println("  CI: y(0)=1, y'(0)=1");
            System.out.println("  Resultado: C1 = " + String.format("%.4f", c1) + ", C2 = " + String.format("%.4f", c2));
            System.out.println("  Esperado: C1 = 1.0000, C2 = 0.5000");
            System.out.println("  ✅ " + (ok ? "PASSOU" : "FALHOU"));
            System.out.println();
            
            resultados.add(new TestResult("Orden 2 (Complejas)", ok, "C1=" + c1 + ", C2=" + c2));
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage() + "\n");
            resultados.add(new TestResult("Orden 2 (Complejas)", false, e.getMessage()));
        }
    }
    
    /**
     * CASO 4: Orden 2 con raíz doble
     * y'' - 2y' + y = 0 → Raíz doble: 1
     * y(x) = (C1 + C2*x)*e^x
     * CI: y(0)=1, y'(0)=0
     */
    static void testCase4_Orden2_RaizDoble() {
        System.out.println("╔─ CASO 4: ORDEN 2, RAÍZ DOBLE ─────────────────╗");
        try {
            String generalSolution = "(C1 + C2*x)*e^(1*x)";
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
            
            List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
            conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));
            conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 0.0));
            
            Map<String, Double> constants = solver.solveInitialConditions(conditions);
            
            double c1 = constants.get("C1");
            double c2 = constants.get("C2");
            
            // y(0) = C1*e^0 = C1 = 1
            // y'(x) = C2*e^x + (C1+C2*x)*e^x = (C1+C2+C2*x)*e^x
            // y'(0) = (C1+C2)*e^0 = C1+C2 = 0 → C2 = -1
            
            boolean ok = Math.abs(c1 - 1.0) < 0.01 && Math.abs(c2 - (-1.0)) < 0.1;
            
            System.out.println("  Ecuación: y'' - 2y' + y = 0");
            System.out.println("  Raíz doble: 1");
            System.out.println("  CI: y(0)=1, y'(0)=0");
            System.out.println("  Resultado: C1 = " + String.format("%.4f", c1) + ", C2 = " + String.format("%.4f", c2));
            System.out.println("  Esperado: C1 = 1.0000, C2 = -1.0000");
            System.out.println("  ✅ " + (ok ? "PASSOU" : "FALHOU"));
            System.out.println();
            
            resultados.add(new TestResult("Orden 2 (Raíz doble)", ok, "C1=" + c1 + ", C2=" + c2));
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage() + "\n");
            resultados.add(new TestResult("Orden 2 (Raíz doble)", false, e.getMessage()));
        }
    }
    
    /**
     * CASO 5: Orden 3 con 3 CI
     * y''' - 6y'' + 11y' - 6y = 0 → Raíces: 1, 2, 3
     * y(x) = C1*e^x + C2*e^(2x) + C3*e^(3x)
     * CI: y(0)=1, y'(0)=0, y''(0)=1
     */
    static void testCase5_Orden3() {
        System.out.println("╔─ CASO 5: ORDEN 3, 3 CI ─────────────────╗");
        try {
            String generalSolution = "C1*e^(1*x) + C2*e^(2*x) + C3*e^(3*x)";
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 3);
            
            List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
            conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, 1.0));
            conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, 0.0));
            conditions.add(new InitialConditionsSolver.InitialCondition(2, 0.0, 1.0));
            
            Map<String, Double> constants = solver.solveInitialConditions(conditions);
            
            double c1 = constants.get("C1");
            double c2 = constants.get("C2");
            double c3 = constants.get("C3");
            
            // C1 + C2 + C3 = 1
            // 1*C1 + 2*C2 + 3*C3 = 0
            // 1*C1 + 4*C2 + 9*C3 = 1
            
            double sum = c1 + c2 + c3;
            double der1 = 1*c1 + 2*c2 + 3*c3;
            double der2 = 1*c1 + 4*c2 + 9*c3;
            
            boolean ok = Math.abs(sum - 1.0) < 0.01 && Math.abs(der1 - 0.0) < 0.1 && Math.abs(der2 - 1.0) < 0.1;
            
            System.out.println("  Ecuación: y''' - 6y'' + 11y' - 6y = 0");
            System.out.println("  Raíces: 1, 2, 3");
            System.out.println("  CI: y(0)=1, y'(0)=0, y''(0)=1");
            System.out.println("  Resultado: C1 = " + String.format("%.4f", c1) + ", C2 = " + String.format("%.4f", c2) + 
                             ", C3 = " + String.format("%.4f", c3));
            System.out.println("  Verificación: C1+C2+C3 = " + String.format("%.4f", sum));
            System.out.println("  Verificación: 1*C1+2*C2+3*C3 = " + String.format("%.4f", der1));
            System.out.println("  Verificación: 1*C1+4*C2+9*C3 = " + String.format("%.4f", der2));
            System.out.println("  ✅ " + (ok ? "PASSOU" : "FALHOU"));
            System.out.println();
            
            resultados.add(new TestResult("Orden 3", ok, "C1=" + c1 + ", C2=" + c2 + ", C3=" + c3));
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage() + "\n");
            resultados.add(new TestResult("Orden 3", false, e.getMessage()));
        }
    }
    
    /**
     * CASO 6: CI en punto x0≠0
     * y'' - 4y = 0 → Raíces: ±2
     * y(x) = C1*e^(2x) + C2*e^(-2x)
     * CI: y(1)=1, y'(1)=0 (en punto x0=1, no en 0)
     */
    static void testCase6_PuntoNoZero() {
        System.out.println("╔─ CASO 6: CI EN PUNTO x0≠0 ─────────────────╗");
        try {
            String generalSolution = "C1*e^(2*x) + C2*e^(-2*x)";
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
            
            List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
            conditions.add(new InitialConditionsSolver.InitialCondition(0, 1.0, 1.0));  // y(1)=1
            conditions.add(new InitialConditionsSolver.InitialCondition(1, 1.0, 0.0));  // y'(1)=0
            
            Map<String, Double> constants = solver.solveInitialConditions(conditions);
            
            double c1 = constants.get("C1");
            double c2 = constants.get("C2");
            
            double e2 = Math.exp(2);
            double eNeg2 = Math.exp(-2);
            
            double y1 = c1 * e2 + c2 * eNeg2;  // y(1)
            double yp1 = 2*c1*e2 - 2*c2*eNeg2; // y'(1)
            
            boolean ok = Math.abs(y1 - 1.0) < 0.01 && Math.abs(yp1 - 0.0) < 0.1;
            
            System.out.println("  Ecuación: y'' - 4y = 0");
            System.out.println("  CI en punto x0=1: y(1)=1, y'(1)=0");
            System.out.println("  Resultado: C1 = " + String.format("%.4f", c1) + ", C2 = " + String.format("%.4f", c2));
            System.out.println("  Verificación: y(1) = " + String.format("%.4f", y1) + " (esperado 1.0)");
            System.out.println("  Verificación: y'(1) = " + String.format("%.4f", yp1) + " (esperado 0.0)");
            System.out.println("  ✅ " + (ok ? "PASSOU" : "FALHOU"));
            System.out.println();
            
            resultados.add(new TestResult("Punto x0≠0", ok, "C1=" + c1 + ", C2=" + c2));
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage() + "\n");
            resultados.add(new TestResult("Punto x0≠0", false, e.getMessage()));
        }
    }
    
    /**
     * CASO 7: Valores negativos
     * y'' - y = 0 → Raíces: ±1
     * y(x) = C1*e^x + C2*e^(-x)
     * CI: y(0)=-1, y'(0)=-2 (valores negativos)
     */
    static void testCase7_ValoresNegativos() {
        System.out.println("╔─ CASO 7: VALORES NEGATIVOS ─────────────────╗");
        try {
            String generalSolution = "C1*e^(1*x) + C2*e^(-1*x)";
            InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
            
            List<InitialConditionsSolver.InitialCondition> conditions = new ArrayList<>();
            conditions.add(new InitialConditionsSolver.InitialCondition(0, 0.0, -1.0));  // y(0)=-1
            conditions.add(new InitialConditionsSolver.InitialCondition(1, 0.0, -2.0));  // y'(0)=-2
            
            Map<String, Double> constants = solver.solveInitialConditions(conditions);
            
            double c1 = constants.get("C1");
            double c2 = constants.get("C2");
            
            // C1 + C2 = -1
            // C1 - C2 = -2
            
            double sum = c1 + c2;
            double diff = c1 - c2;
            
            boolean ok = Math.abs(sum - (-1.0)) < 0.01 && Math.abs(diff - (-2.0)) < 0.01;
            
            System.out.println("  Ecuación: y'' - y = 0");
            System.out.println("  CI: y(0)=-1, y'(0)=-2 (valores negativos)");
            System.out.println("  Resultado: C1 = " + String.format("%.4f", c1) + ", C2 = " + String.format("%.4f", c2));
            System.out.println("  Verificación: C1+C2 = " + String.format("%.4f", sum) + " (esperado -1.0)");
            System.out.println("  Verificación: C1-C2 = " + String.format("%.4f", diff) + " (esperado -2.0)");
            System.out.println("  ✅ " + (ok ? "PASSOU" : "FALHOU"));
            System.out.println();
            
            resultados.add(new TestResult("Valores negativos", ok, "C1=" + c1 + ", C2=" + c2));
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage() + "\n");
            resultados.add(new TestResult("Valores negativos", false, e.getMessage()));
        }
    }
    
    static void mostrarResultados() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    RESUMEN DE RESULTADOS                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        int total = resultados.size();
        int exitosos = 0;
        
        System.out.println("DETALLE DE PRUEBAS:");
        System.out.println("─────────────────────────────────────────────────────────────");
        for (int i = 0; i < resultados.size(); i++) {
            TestResult r = resultados.get(i);
            if (r.exitoso) exitosos++;
            String estado = r.exitoso ? "✅ PASSOU" : "❌ FALHOU";
            System.out.println((i+1) + ". " + r.nombre + ": " + estado);
            System.out.println("   Detalles: " + r.detalles);
        }
        
        System.out.println("\n" + "─────────────────────────────────────────────────────────────");
        System.out.println("TOTALES: " + exitosos + "/" + total + " pruebas exitosas");
        
        if (exitosos == total) {
            System.out.println("\n🎉 ¡TODAS LAS PRUEBAS PASARON EXITOSAMENTE! 🎉");
            System.out.println("\n✅ Las Condiciones Iniciales funcionan correctamente en:");
            System.out.println("   • Ecuaciones de orden 1");
            System.out.println("   • Ecuaciones de orden 2 (raíces reales distintas)");
            System.out.println("   • Ecuaciones de orden 2 (raíces complejas)");
            System.out.println("   • Ecuaciones de orden 2 (raíces dobles)");
            System.out.println("   • Ecuaciones de orden 3+");
            System.out.println("   • CI en puntos x0≠0");
            System.out.println("   • Valores negativos");
            System.out.println("   • Precisión: ±0.01 en constantes");
        } else {
            System.out.println("\n⚠️ Algunas pruebas fallaron. Revisar detalles arriba.");
        }
        
        System.out.println("\n╚════════════════════════════════════════════════════════════╝\n");
    }
}
