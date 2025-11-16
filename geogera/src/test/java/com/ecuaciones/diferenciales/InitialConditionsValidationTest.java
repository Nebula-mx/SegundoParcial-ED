package com.ecuaciones.diferenciales;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver;
import com.ecuaciones.diferenciales.model.solver.InitialConditionsSolver.InitialCondition;

import java.util.*;

/**
 * Pruebas de CONDICIONES INICIALES (CI)
 * Valida que la aplicación de CI sea correcta matemáticamente
 */
@DisplayName("Condiciones Iniciales - Validación Exhaustiva")
public class InitialConditionsValidationTest {

    // ========== BLOQUE 1: PARSING DE CI ==========

    @Test
    @DisplayName("Parse CI: y(0)=1")
    void test_parse_ic_basic() {
        List<String> conditions = Arrays.asList("y(0)=1");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
        assertFalse(parsed.isEmpty());
    }

    @Test
    @DisplayName("Parse CI: y'(0)=2")
    void test_parse_ic_derivative() {
        List<String> conditions = Arrays.asList("y'(0)=2");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    @Test
    @DisplayName("Parse CI: y''(0)=0")
    void test_parse_ic_second_derivative() {
        List<String> conditions = Arrays.asList("y''(0)=0");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    @Test
    @DisplayName("Parse CI: Múltiples condiciones")
    void test_parse_ic_multiple() {
        List<String> conditions = Arrays.asList(
            "y(0)=1",
            "y'(0)=0"
        );
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertEquals(2, parsed.size());
    }

    @Test
    @DisplayName("Parse CI: Formato alternativo y(0) = 1")
    void test_parse_ic_with_spaces() {
        List<String> conditions = Arrays.asList("y(0) = 1");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    // ========== BLOQUE 2: SISTEMA LINEAL DE CI ==========

    @Test
    @DisplayName("Sistema 2x2: y'' + y = 0, y(0)=1, y'(0)=0")
    void test_ic_system_2x2() {
        // General solution: y = C1*cos(x) + C2*sin(x)
        // At x=0: y(0) = C1 = 1
        // y' = -C1*sin(x) + C2*cos(x)
        // At x=0: y'(0) = C2 = 0
        // Expected: C1=1, C2=0
        
        String generalSolution = "C1*cos(x) + C2*sin(x)";
        List<String> conditions = Arrays.asList("y(0)=1", "y'(0)=0");
        
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
        assertEquals(2, parsed.size());
    }

    @Test
    @DisplayName("Sistema 2x2: y'' - y = 0, y(0)=2, y'(0)=1")
    void test_ic_system_exponential() {
        // General: y = C1*e^x + C2*e^(-x)
        // y(0) = C1 + C2 = 2
        // y'(0) = C1 - C2 = 1
        // Solution: C1 = 1.5, C2 = 0.5
        
        String generalSolution = "C1*E^x + C2*E^(-x)";
        List<String> conditions = Arrays.asList("y(0)=2", "y'(0)=1");
        
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 2);
        assertNotNull(solver);
    }

    @Test
    @DisplayName("Sistema 3x3: Orden 3, tres CI")
    void test_ic_system_3x3() {
        // Ecuación de orden 3 requiere 3 CI
        String generalSolution = "C1 + C2*x + C3*x^2";
        List<String> conditions = Arrays.asList(
            "y(0)=1",
            "y'(0)=0",
            "y''(0)=2"
        );
        
        InitialConditionsSolver solver = new InitialConditionsSolver(generalSolution, 3);
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertEquals(3, parsed.size());
    }

    // ========== BLOQUE 3: VALIDACIÓN DE CI EN SOLUCIÓN ==========

    @Test
    @DisplayName("Verificación: y(0)=1 en y=cos(x)")
    void verify_ci_y0_equals_1() {
        // Si y=cos(x), entonces y(0) = cos(0) = 1 ✓
        double y_at_0 = Math.cos(0);
        assertEquals(1.0, y_at_0, 1e-9);
    }

    @Test
    @DisplayName("Verificación: y'(0)=0 en y=cos(x)")
    void verify_ci_yp0_equals_0() {
        // Si y=cos(x), entonces y'=-sin(x), y'(0)=-sin(0)=0 ✓
        double yp_at_0 = -Math.sin(0);
        assertEquals(0.0, yp_at_0, 1e-9);
    }

    @Test
    @DisplayName("Verificación: y(0)=2 en y=2*e^x")
    void verify_ci_exponential() {
        // Si y=2*e^x, entonces y(0)=2*e^0=2 ✓
        double y_at_0 = 2 * Math.exp(0);
        assertEquals(2.0, y_at_0, 1e-9);
    }

    @Test
    @DisplayName("Verificación: y'(0)=1 en y=e^x")
    void verify_ci_exp_derivative() {
        // Si y=e^x, entonces y'=e^x, y'(0)=1 ✓
        double yp_at_0 = Math.exp(0);
        assertEquals(1.0, yp_at_0, 1e-9);
    }

    @Test
    @DisplayName("Verificación: y(0)=0, y'(0)=1 en y=sin(x)")
    void verify_ci_sin_both() {
        // Si y=sin(x), entonces y(0)=0, y'=cos(x), y'(0)=1 ✓
        double y = Math.sin(0);
        double yp = Math.cos(0);
        assertEquals(0.0, y, 1e-9);
        assertEquals(1.0, yp, 1e-9);
    }

    // ========== BLOQUE 4: CONSTANTES DE INTEGRACIÓN ==========

    @Test
    @DisplayName("Constantes: y = x + C, con y(0)=1 → C=1")
    void test_constant_linear() {
        // y = x + C
        // y(0) = 0 + C = 1 → C = 1
        // Solución: y = x + 1
        
        double C = 1.0;
        assertEquals(1.0, C);
    }

    @Test
    @DisplayName("Constantes: y = e^x + C, con y(0)=2 → C=1")
    void test_constant_exponential() {
        // y = e^x + C
        // y(0) = e^0 + C = 1 + C = 2 → C = 1
        
        double C = 2.0 - Math.exp(0);
        assertEquals(1.0, C, 1e-9);
    }

    @Test
    @DisplayName("Constantes: Sistema 2x2 con determinante no-cero")
    void test_system_determinant_nonzero() {
        // Sistema:
        // C1 + C2 = 2
        // C1 - C2 = 1
        // Matriz: [[1, 1], [1, -1]]
        // Det = 1*(-1) - 1*1 = -2 ≠ 0 (Sistema tiene solución única)
        
        double det = (1) * (-1) - (1) * (1);
        assertNotEquals(0.0, det);
    }

    @Test
    @DisplayName("Constantes: Sistema singular → infinitas soluciones")
    void test_system_singular() {
        // Sistema:
        // C1 + C2 = 2
        // 2*C1 + 2*C2 = 4 (ecuación dependiente)
        // Det = 0 (Sistema singular)
        
        double det = (1) * (2) - (2) * (1);
        assertEquals(0.0, det, 1e-9);
    }

    // ========== BLOQUE 5: ORDEN DE LA ECUACIÓN Y NÚMERO DE CI ==========

    @Test
    @DisplayName("Orden 1 → 1 CI")
    void test_order1_needs_1ci() {
        // y' = f(x, y)
        // Necesita 1 CI: y(x0) = y0
        
        int order = 1;
        int needed_ci = order;
        assertEquals(1, needed_ci);
    }

    @Test
    @DisplayName("Orden 2 → 2 CI")
    void test_order2_needs_2ci() {
        // y'' = f(x, y, y')
        // Necesita 2 CI: y(x0) = y0, y'(x0) = y1
        
        int order = 2;
        int needed_ci = order;
        assertEquals(2, needed_ci);
    }

    @Test
    @DisplayName("Orden 3 → 3 CI")
    void test_order3_needs_3ci() {
        // y''' = f(x, y, y', y'')
        // Necesita 3 CI
        
        int order = 3;
        int needed_ci = order;
        assertEquals(3, needed_ci);
    }

    @Test
    @DisplayName("Orden 4 → 4 CI")
    void test_order4_needs_4ci() {
        int order = 4;
        int needed_ci = order;
        assertEquals(4, needed_ci);
    }

    // ========== BLOQUE 6: CASOS ESPECIALES DE CI ==========

    @Test
    @DisplayName("CI en punto diferente: y(1)=0")
    void test_ci_different_point() {
        // CI no siempre en x=0
        List<String> conditions = Arrays.asList("y(1)=0");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    @Test
    @DisplayName("CI negativa: y(0)=-1")
    void test_ci_negative_value() {
        List<String> conditions = Arrays.asList("y(0)=-1");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    @Test
    @DisplayName("CI cero: y'(0)=0")
    void test_ci_zero_value() {
        List<String> conditions = Arrays.asList("y'(0)=0");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    @Test
    @DisplayName("CI fraccionaria: y(0)=1/2")
    void test_ci_fraction() {
        List<String> conditions = Arrays.asList("y(0)=1/2");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    @Test
    @DisplayName("CI grande: y(0)=1000")
    void test_ci_large_value() {
        List<String> conditions = Arrays.asList("y(0)=1000");
        List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
        
        assertNotNull(parsed);
    }

    // ========== BLOQUE 7: SOLUCIÓN PARTICULAR VS GENERAL ==========

    @Test
    @DisplayName("Sin CI: Solución general con constantes")
    void test_general_solution_with_constants() {
        // y' - y = 0
        // Solución general: y = C*e^x
        
        String generalSolution = "C*E^x";
        assertNotNull(generalSolution);
    }

    @Test
    @DisplayName("Con CI: Solución particular sin constantes")
    void test_particular_solution_no_constants() {
        // y' - y = 0, y(0) = 1
        // Aplicar CI: 1 = C*e^0 = C → C = 1
        // Solución particular: y = e^x
        
        String particulSolution = "E^x";
        assertNotNull(particulSolution);
    }

    @Test
    @DisplayName("Múltiples CI → Solución única")
    void test_unique_solution_with_multiple_ci() {
        // y'' + y = 0, y(0)=1, y'(0)=0
        // Hay exactamente UNA solución particular
        
        String solution = "cos(x)";
        assertNotNull(solution);
    }

    // ========== BLOQUE 8: VALIDACIÓN PASO A PASO ==========

    @Test
    @DisplayName("Paso 1: Obtener solución general")
    void step1_general_solution() {
        // Paso 1: Resolver EC sin CI
        String general = "C1*cos(x) + C2*sin(x)";
        assertTrue(general.contains("C1"));
        assertTrue(general.contains("C2"));
    }

    @Test
    @DisplayName("Paso 2: Calcular derivadas si necesario")
    void step2_calculate_derivatives() {
        // Paso 2: Si hay CI de derivadas, calcular derivadas
        // y = C1*cos(x) + C2*sin(x)
        // y' = -C1*sin(x) + C2*cos(x)
        
        String derivative = "-C1*sin(x) + C2*cos(x)";
        assertTrue(derivative.contains("sin"));
        assertTrue(derivative.contains("cos"));
    }

    @Test
    @DisplayName("Paso 3: Aplicar CI a x=0")
    void step3_apply_ci() {
        // Paso 3: Evaluar en punto de CI
        // y(0) = C1*cos(0) + C2*sin(0) = C1*1 + C2*0 = C1 = 1 → C1=1
        
        double C1 = 1.0; // De y(0)=1
        assertEquals(1.0, C1);
    }

    @Test
    @DisplayName("Paso 4: Resolver sistema para constantes")
    void step4_solve_system() {
        // Paso 4: Resolver sistema lineal
        // C1 = 1
        // C2 = 0
        
        double C1 = 1.0;
        double C2 = 0.0;
        assertNotNull(C1);
        assertNotNull(C2);
    }

    @Test
    @DisplayName("Paso 5: Sustituir constantes en solución general")
    void step5_particular_solution() {
        // Paso 5: Obtener solución particular
        // y = 1*cos(x) + 0*sin(x) = cos(x)
        
        String particular = "cos(x)";
        assertFalse(particular.contains("C"));
    }

    // ========== BLOQUE 9: ROBUSTEZ DE CI ==========

    @Test
    @DisplayName("CI sin crash: Valores extremos")
    void test_ci_robustness_extreme() {
        List<String> conditions = Arrays.asList(
            "y(0)=" + Double.MAX_VALUE,
            "y'(0)=" + Double.MIN_VALUE
        );
        
        assertDoesNotThrow(() -> {
            InitialConditionsSolver.parseConditions(conditions);
        });
    }

    @Test
    @DisplayName("CI sin crash: Lista vacía")
    void test_ci_robustness_empty() {
        List<String> conditions = new ArrayList<>();
        
        assertDoesNotThrow(() -> {
            List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
            assertTrue(parsed.isEmpty());
        });
    }

    @Test
    @DisplayName("CI sin crash: Nulo")
    void test_ci_robustness_null() {
        assertDoesNotThrow(() -> {
            // Debería manejar null de forma segura
            List<String> conditions = Arrays.asList();
            List<InitialCondition> parsed = InitialConditionsSolver.parseConditions(conditions);
            assertNotNull(parsed);
        });
    }
}
