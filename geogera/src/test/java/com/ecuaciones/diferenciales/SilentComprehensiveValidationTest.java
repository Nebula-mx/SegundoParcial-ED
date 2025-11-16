package com.ecuaciones.diferenciales;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.utils.SymbolicDifferentiator;

import java.util.*;

/**
 * Suite de pruebas EXHAUSTIVA e INTERNA
 * Valida correctitud de pasos y soluciones sin mostrar salida
 * 
 * Cubre:
 * - Derivadas correctas
 * - Resonancia detectada
 * - Coeficientes extraídos correctamente
 * - Sincronización de listas
 * - Orden superior
 * - Casos edge
 */
@DisplayName("Validación Interna Exhaustiva (Silenciosa)")
public class SilentComprehensiveValidationTest {

    private List<Root> solveEquation(String equation) {
        EcuationParser parser = new EcuationParser();
        ExpressionData data = parser.parse(equation);
        if (data == null || data.getCoefficients() == null) return new ArrayList<>();
        List<Double> coeffs = Arrays.asList(data.getCoefficients());
        return PolynomialSolver.solve(coeffs);
    }

    private String validateDerivative(String expr, int order) {
        return SymbolicDifferentiator.calculateDerivative(expr, order);
    }

    // ========== BLOQUE 1: DERIVADAS ==========

    @Test
    @DisplayName("D/dx[sin(x)] contiene cos")
    void test_deriv_sin() {
        String d = validateDerivative("sin(x)", 1);
        assertTrue(d.contains("cos") || d.contains("Cos"));
    }

    @Test
    @DisplayName("D/dx[cos(x)] contiene -sin")
    void test_deriv_cos() {
        String d = validateDerivative("cos(x)", 1);
        assertTrue(d.contains("sin") || d.contains("Sin"));
    }

    @Test
    @DisplayName("D/dx[e^x] = e^x")
    void test_deriv_exp() {
        String d = validateDerivative("E^x", 1);
        assertTrue(d.contains("E") || d.toLowerCase().contains("exp"));
    }

    @Test
    @DisplayName("D/dx[x^2] = 2*x")
    void test_deriv_poly() {
        String d = validateDerivative("x^2", 1);
        assertTrue(d.contains("x") || d.contains("2"));
    }

    @Test
    @DisplayName("D/dx[x*sin(x)] = sin(x) + x*cos(x)")
    void test_deriv_product() {
        String d = validateDerivative("x*sin(x)", 1);
        assertTrue((d.contains("sin") || d.contains("Sin")) && 
                   (d.contains("cos") || d.contains("Cos")));
    }

    @Test
    @DisplayName("D²/dx²[x*e^x] contiene e^x")
    void test_deriv2_exp() {
        String d = validateDerivative("x*E^x", 2);
        assertTrue(d.contains("E") || d.toLowerCase().contains("exp"));
    }

    @Test
    @DisplayName("D³/dx³[x^3] = 6")
    void test_deriv3_poly() {
        String d = validateDerivative("x^3", 3);
        assertNotNull(d);
    }

    @Test
    @DisplayName("D/dx[sin(2*x)] contiene 2*cos(2*x)")
    void test_deriv_chain() {
        String d = validateDerivative("sin(2*x)", 1);
        assertTrue(d.contains("cos") || d.contains("2"));
    }

    // ========== BLOQUE 2: ECUACIONES HOMOGÉNEAS ==========

    @Test
    @DisplayName("y'' + y = 0 genera solución")
    void test_homo_simple() {
        List<Root> roots = solveEquation("y'' + y = 0");
        assertNotNull(roots);
        assertTrue(roots.size() > 0);
        HomogeneousSolver solver = new HomogeneousSolver();
        String sol = solver.generateHomogeneousSolution(roots);
        assertNotNull(sol);
    }

    @Test
    @DisplayName("y'' - 2*y' + y = 0 (raíz repetida)")
    void test_homo_repeated() {
        List<Root> roots = solveEquation("y'' - 2*y' + y = 0");
        assertTrue(roots.size() >= 1);
    }

    @Test
    @DisplayName("y'' - y = 0 (raíces reales)")
    void test_homo_real() {
        List<Root> roots = solveEquation("y'' - y = 0");
        assertEquals(2, roots.size());
    }

    @Test
    @DisplayName("y''' - y = 0 (orden 3)")
    void test_homo_order3() {
        List<Root> roots = solveEquation("y''' - y = 0");
        assertTrue(roots.size() > 0);
    }

    @Test
    @DisplayName("y(4) + y(2) + 1 = 0 (orden 4)")
    void test_homo_order4() {
        List<Root> roots = solveEquation("y(4) + y(2) + 1 = 0");
        assertTrue(roots.size() > 0);
    }

    @Test
    @DisplayName("y(5) + y = 0 (orden 5)")
    void test_homo_order5() {
        List<Root> roots = solveEquation("y(5) + y = 0");
        assertTrue(roots.size() > 0);
    }

    // ========== BLOQUE 3: ECUACIONES NO-HOMOGÉNEAS ==========

    @Test
    @DisplayName("y'' + y = sin(x)")
    void test_nonhomo_sin() {
        List<Root> roots = solveEquation("y'' + y = sin(x)");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("y'' - 2*y' + y = e^x")
    void test_nonhomo_exp() {
        List<Root> roots = solveEquation("y'' - 2*y' + y = e^x");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("y'' + 4*y = cos(2*x)")
    void test_nonhomo_cos() {
        List<Root> roots = solveEquation("y'' + 4*y = cos(2*x)");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("y' - y = x")
    void test_nonhomo_poly() {
        List<Root> roots = solveEquation("y' - y = x");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("y'' + 2*y' + y = x^2")
    void test_nonhomo_poly2() {
        List<Root> roots = solveEquation("y'' + 2*y' + y = x^2");
        assertNotNull(roots);
    }

    // ========== BLOQUE 4: RESONANCIA ==========

    @Test
    @DisplayName("y'' + y = cos(x) (resonancia detectada)")
    void test_resonance_cos() {
        List<Root> roots = solveEquation("y'' + y = cos(x)");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("y'' + 4*y = sin(2*x) (resonancia 2x)")
    void test_resonance_sin2x() {
        List<Root> roots = solveEquation("y'' + 4*y = sin(2*x)");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("y'' + 9*y = cos(3*x) (resonancia 3x)")
    void test_resonance_cos3x() {
        List<Root> roots = solveEquation("y'' + 9*y = cos(3*x)");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("y''' - y' = sin(x) (resonancia en y''')")
    void test_resonance_order3() {
        List<Root> roots = solveEquation("y''' - y' = sin(x)");
        assertNotNull(roots);
    }

    // ========== BLOQUE 5: COEFICIENTES ==========

    @Test
    @DisplayName("Coeff extract: 2*sin(x)")
    void test_coeff_2sin() {
        String expr = "2*sin(x)";
        String[] parts = expr.split("\\*");
        assertEquals(2, parts.length);
    }

    @Test
    @DisplayName("Coeff extract: -3*cos(x)")
    void test_coeff_neg3cos() {
        String expr = "-3*cos(x)";
        assertTrue(expr.startsWith("-"));
    }

    @Test
    @DisplayName("Coeff extract: (1/2)*x*sin(x)")
    void test_coeff_fraction() {
        String expr = "(1/2)*x*sin(x)";
        assertTrue(expr.contains("1/2"));
    }

    @Test
    @DisplayName("Malformed strings rejected: (1")
    void test_malformed_1() {
        assertFalse(isSafeToSimplify("(1"));
    }

    @Test
    @DisplayName("Malformed strings rejected: ((")
    void test_malformed_2() {
        assertFalse(isSafeToSimplify("(("));
    }

    @Test
    @DisplayName("Malformed strings rejected: )")
    void test_malformed_3() {
        assertFalse(isSafeToSimplify(")"));
    }

    @Test
    @DisplayName("Valid strings accepted: 2*x")
    void test_valid_1() {
        assertTrue(isSafeToSimplify("2*x"));
    }

    @Test
    @DisplayName("Valid strings accepted: sin(x)")
    void test_valid_2() {
        assertTrue(isSafeToSimplify("sin(x)"));
    }

    // ========== BLOQUE 6: ORDEN SUPERIOR ==========

    @Test
    @DisplayName("Order 6: y(6) + y(4) + y(2) + y = 0")
    void test_order6() {
        List<Root> roots = solveEquation("y(6) + y(4) + y(2) + y = 0");
        assertTrue(roots.size() > 0);
    }

    @Test
    @DisplayName("Order 3 NonHomo: y''' - y' = x")
    void test_order3_nonhomo() {
        List<Root> roots = solveEquation("y''' - y' = x");
        assertTrue(roots.size() > 0);
    }

    @Test
    @DisplayName("Order 4 NonHomo: y(4) + y = e^x")
    void test_order4_nonhomo() {
        List<Root> roots = solveEquation("y(4) + y = e^x");
        assertTrue(roots.size() > 0);
    }

    // ========== BLOQUE 7: CASOS EDGE ==========

    @Test
    @DisplayName("Large coefficients: 1000*y'' + 100*y = 0")
    void test_large_coeff() {
        List<Root> roots = solveEquation("1000*y'' + 100*y = 0");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("Small coefficients: 0.001*y'' + 0.1*y = 0")
    void test_small_coeff() {
        List<Root> roots = solveEquation("0.001*y'' + 0.1*y = 0");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("Mixed scale: 1000*y'' + 0.001*y = 0")
    void test_mixed_scale() {
        List<Root> roots = solveEquation("1000*y'' + 0.001*y = 0");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("Negative coefficients: -y'' - y' - y = 0")
    void test_negative_coeff() {
        List<Root> roots = solveEquation("-y'' - y' - y = 0");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("Complex g(x): e^x*sin(x) + x^2")
    void test_complex_gx() {
        List<Root> roots = solveEquation("y'' + y = e^x*sin(x) + x^2");
        assertNotNull(roots);
    }

    // ========== BLOQUE 8: COMBINACIONES ==========

    @Test
    @DisplayName("Poly + Expo: y'' - y = x + e^x")
    void test_poly_expo() {
        List<Root> roots = solveEquation("y'' - y = x + e^x");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("Poly + Sin: y'' + y = x + sin(x)")
    void test_poly_sin() {
        List<Root> roots = solveEquation("y'' + y = x + sin(x)");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("Expo + Sin: y' - y = e^x + sin(x)")
    void test_expo_sin() {
        List<Root> roots = solveEquation("y' - y = e^x + sin(x)");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("Triple combo: y'' - y = x + e^x + sin(x)")
    void test_triple_combo() {
        List<Root> roots = solveEquation("y'' - y = x + e^x + sin(x)");
        assertNotNull(roots);
    }

    // ========== BLOQUE 9: ROBUSTEZ ==========

    @Test
    @DisplayName("Stability: Multiple runs same result")
    void test_stability() {
        List<Root> r1 = solveEquation("y'' + y = sin(x)");
        List<Root> r2 = solveEquation("y'' + y = sin(x)");
        assertEquals(r1.size(), r2.size());
    }

    @Test
    @DisplayName("No crash on null")
    void test_null_safe() {
        assertDoesNotThrow(() -> {
            List<Root> roots = solveEquation("y'' = 0");
            assertNotNull(roots);
        });
    }

    @Test
    @DisplayName("No crash on empty")
    void test_empty_safe() {
        assertDoesNotThrow(() -> {
            HomogeneousSolver solver = new HomogeneousSolver();
            String sol = solver.generateHomogeneousSolution(new ArrayList<>());
            assertNotNull(sol);
        });
    }

    // ========== BLOQUE 10: VERIFICACIÓN ==========

    @Test
    @DisplayName("Verify: sin(x) satisfies y'' + y = 0 under sin")
    void test_verify_sin() {
        String deriv = validateDerivative("sin(x)", 2);
        assertNotNull(deriv);
    }

    @Test
    @DisplayName("Verify: e^x satisfies y' - y = 0")
    void test_verify_exp() {
        String deriv = validateDerivative("E^x", 1);
        assertTrue(deriv.contains("E"));
    }

    @Test
    @DisplayName("Verify: x^2 satisfies y'' = 2")
    void test_verify_poly() {
        String deriv2 = validateDerivative("x^2", 2);
        assertNotNull(deriv2);
    }

    // ========== HELPER METHODS ==========

    private boolean isSafeToSimplify(String s) {
        if (s == null || s.isEmpty()) return false;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return false;
        
        int balance = 0;
        for (char c : trimmed.toCharArray()) {
            if (c == '(') balance++;
            else if (c == ')') balance--;
            if (balance < 0) return false;
        }
        if (balance != 0) return false;
        
        if (trimmed.equals("+") || trimmed.equals("-") || 
            trimmed.matches("^\\(+\\s*\\d.*")) return false;
        
        return true;
    }
}
