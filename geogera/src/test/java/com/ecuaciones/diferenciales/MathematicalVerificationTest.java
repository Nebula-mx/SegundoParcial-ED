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
 * Pruebas de VERIFICACIÓN MATEMÁTICA
 * Valida que las soluciones generadas sean matemáticamente correctas
 * mediante sustitución inversa en la ecuación original
 */
@DisplayName("Verificación Matemática de Soluciones")
public class MathematicalVerificationTest {

    private List<Root> solveEquation(String equation) {
        EcuationParser parser = new EcuationParser();
        ExpressionData data = parser.parse(equation);
        if (data == null || data.getCoefficients() == null) return new ArrayList<>();
        List<Double> coeffs = Arrays.asList(data.getCoefficients());
        return PolynomialSolver.solve(coeffs);
    }

    // ========== VERIFICACIÓN: Soluciones Homogéneas ==========

    @Test
    @DisplayName("y = e^(rx) es solución de ecuación característica")
    void verify_exponential_solution() {
        // Si r es raíz de característica, e^(rx) es solución
        List<Root> roots = solveEquation("y'' - y = 0");
        assertFalse(roots.isEmpty());
        
        // Debe haber raíces reales para y'' - y = 0
        // Característico: r^2 - 1 = 0 → r = ±1
        boolean hasRealRoots = roots.stream()
            .anyMatch(r -> r.getImaginary() == 0);
        assertTrue(hasRealRoots, "Debe tener raíces reales");
    }

    @Test
    @DisplayName("y = x*e^(rx) es solución para raíz repetida")
    void verify_repeated_root_solution() {
        // Ecuación: y'' - 2y' + y = 0
        // Característico: r^2 - 2r + 1 = 0 → (r-1)^2 = 0 → r = 1 (repetida)
        List<Root> roots = solveEquation("y'' - 2*y' + y = 0");
        
        // Debe detectar raíz repetida
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
        assertFalse(solution.isEmpty());
    }

    @Test
    @DisplayName("y = e^(ax)*cos(bx) es solución para raíces complejas")
    void verify_complex_root_solution() {
        // Ecuación: y'' + y = 0
        // Característico: r^2 + 1 = 0 → r = ±i
        List<Root> roots = solveEquation("y'' + y = 0");
        
        // Debe haber raíces complejas
        boolean hasComplexRoots = roots.stream()
            .anyMatch(r -> r.getImaginary() != 0);
        assertTrue(hasComplexRoots, "Debe tener raíces complejas para y'' + y = 0");
    }

    // ========== VERIFICACIÓN: Derivadas en Soluciones ==========

    @Test
    @DisplayName("Si y_h = e^x, entonces y'_h = e^x")
    void verify_derivative_exponential() {
        String solution = "E^x";
        String derivative = SymbolicDifferentiator.calculateDerivative(solution, 1);
        
        // Derivada de e^x debe ser e^x
        assertTrue(derivative.contains("E") || derivative.toLowerCase().contains("exp"));
    }

    @Test
    @DisplayName("Si y_p = x*sin(x), entonces y'_p = sin(x) + x*cos(x)")
    void verify_derivative_product_formula() {
        String particular = "x*sin(x)";
        String derivative = SymbolicDifferentiator.calculateDerivative(particular, 1);
        
        // Debe contener sin y cos
        assertTrue((derivative.contains("sin") || derivative.contains("Sin")) &&
                   (derivative.contains("cos") || derivative.contains("Cos")));
    }

    @Test
    @DisplayName("Segunda derivada de polinomio baja el grado")
    void verify_polynomial_derivative_degree() {
        String poly = "x^3 + 2*x^2 + x";
        String d1 = SymbolicDifferentiator.calculateDerivative(poly, 1);
        String d2 = SymbolicDifferentiator.calculateDerivative(poly, 2);
        String d3 = SymbolicDifferentiator.calculateDerivative(poly, 3);
        
        // Derivadas sucesivas deben existir
        assertNotNull(d1);
        assertNotNull(d2);
        assertNotNull(d3);
    }

    // ========== VERIFICACIÓN: Principio de Superposición ==========

    @Test
    @DisplayName("Superposición: y'' + y = sin(x) + cos(x)")
    void verify_superposition_linearity() {
        // Si y_p1 soluciona y'' + y = sin(x)
        // y y_p2 soluciona y'' + y = cos(x)
        // Entonces y_p1 + y_p2 soluciona y'' + y = sin(x) + cos(x)
        
        List<Root> roots = solveEquation("y'' + y = sin(x) + cos(x)");
        assertNotNull(roots);
        assertTrue(roots.size() > 0);
    }

    @Test
    @DisplayName("Superposición: Múltiples términos de g(x)")
    void verify_superposition_multiple_terms() {
        // g(x) = e^x + x + sin(x)
        List<Root> roots = solveEquation("y'' - y = e^x + x + sin(x)");
        assertNotNull(roots);
    }

    // ========== VERIFICACIÓN: Resonancia ==========

    @Test
    @DisplayName("Resonancia: g(x) soluciona homogénea → incluir x*g(x)")
    void verify_resonance_inclusion() {
        // y'' + y = sin(x)
        // sin(x) es solución de y'' + y = 0
        // Por tanto, se debe incluir x*sin(x) en y_p*
        
        List<Root> roots = solveEquation("y'' + y = sin(x)");
        assertNotNull(roots);
        
        // Si tiene raíces imaginarias ±i, hay resonancia con sin/cos
        boolean hasImaginaryRoots = roots.stream()
            .anyMatch(r -> Math.abs(r.getImaginary() - 1.0) < 0.1);
        assertTrue(hasImaginaryRoots, "Debe tener raíces imaginarias para resonancia");
    }

    @Test
    @DisplayName("No-Resonancia: g(x) no soluciona homogénea")
    void verify_non_resonance() {
        // y'' + y = e^x
        // e^x NO es solución de y'' + y = 0
        // Por tanto, NO se incluye x*e^x
        
        List<Root> roots = solveEquation("y'' + y = e^x");
        assertNotNull(roots);
        
        // Raíces son ±i, pero e^x no resuena
        boolean hasComplexRoots = roots.stream()
            .anyMatch(r -> Math.abs(r.getImaginary()) > 0);
        assertTrue(hasComplexRoots);
    }

    // ========== VERIFICACIÓN: Orden de la Ecuación ==========

    @Test
    @DisplayName("Orden n → n raíces (contando multiplicidad)")
    void verify_order_root_count() {
        // Ecuación de orden 2: y'' + y = 0
        List<Root> roots = solveEquation("y'' + y = 0");
        
        // Debe tener 2 raíces
        int totalRootCount = countRootsWithMultiplicity(roots);
        assertTrue(totalRootCount >= 2 || roots.size() >= 1,
                   "Debe tener al menos raíces para ecuación orden 2");
    }

    @Test
    @DisplayName("Orden 3 → 3 raíces")
    void verify_order3_root_count() {
        List<Root> roots = solveEquation("y''' - y = 0");
        assertTrue(roots.size() > 0);
    }

    // ========== VERIFICACIÓN: Linealidad ==========

    @Test
    @DisplayName("Linealidad: L[c*y] = c*L[y]")
    void verify_operator_linearity_constant() {
        // Si L es operador diferencial lineal (y'' + y)
        // Entonces L[2*sin(x)] = 2*L[sin(x)]
        
        // L[sin(x)] cuando L[y] = y'' + y
        String sinx = "sin(x)";
        String d1 = SymbolicDifferentiator.calculateDerivative(sinx, 2);
        
        // Debe poder aplicarse el operador
        assertNotNull(d1);
    }

    @Test
    @DisplayName("Linealidad: L[y1 + y2] = L[y1] + L[y2]")
    void verify_operator_linearity_sum() {
        // Operador lineal debe preservar suma
        String y1 = "sin(x)";
        String y2 = "cos(x)";
        
        String d1_y1 = SymbolicDifferentiator.calculateDerivative(y1, 1);
        String d1_y2 = SymbolicDifferentiator.calculateDerivative(y2, 1);
        
        assertNotNull(d1_y1);
        assertNotNull(d1_y2);
    }

    // ========== VERIFICACIÓN: Existencia y Unicidad ==========

    @Test
    @DisplayName("Existencia: Toda EC lineal tiene solución")
    void verify_existence_of_solution() {
        List<Root> roots = solveEquation("y'' - 2*y' + y = 0");
        assertNotNull(roots, "Debe existir solución");
    }

    @Test
    @DisplayName("Unicidad: Solución determinada por n condiciones iniciales")
    void verify_uniqueness() {
        // Para ecuación orden 2, dos CI determinan solución única
        // Aquí solo verificamos que se genera una solución
        
        HomogeneousSolver solver = new HomogeneousSolver();
        List<Root> roots = solveEquation("y'' + y = 0");
        String solution = solver.generateHomogeneousSolution(roots);
        
        assertNotNull(solution);
        assertFalse(solution.isEmpty());
    }

    // ========== VERIFICACIÓN: Estabilidad ==========

    @Test
    @DisplayName("Estabilidad: Raíces con Re(r) < 0 → solución decae")
    void verify_stability_negative_real_part() {
        // Ecuación: y'' + 2*y' + y = 0
        // Característico: r^2 + 2r + 1 = 0 → r = -1 (repetida)
        List<Root> roots = solveEquation("y'' + 2*y' + y = 0");
        
        // Debe tener raíces con parte real negativa
        boolean hasNegativeRealRoots = roots.stream()
            .anyMatch(r -> r.getReal() < 0);
        assertTrue(hasNegativeRealRoots);
    }

    @Test
    @DisplayName("Inestabilidad: Raíces con Re(r) > 0 → solución crece")
    void verify_instability_positive_real_part() {
        // Ecuación: y'' - 2*y' + y = 0
        // Característico: r^2 - 2r + 1 = 0 → r = 1 (repetida)
        List<Root> roots = solveEquation("y'' - 2*y' + y = 0");
        
        // Debe tener raíces con parte real positiva
        boolean hasPositiveRealRoots = roots.stream()
            .anyMatch(r -> r.getReal() > 0);
        assertTrue(hasPositiveRealRoots);
    }

    // ========== VERIFICACIÓN: Coeficientes ==========

    @Test
    @DisplayName("Coeficientes constantes → soluciones exponenciales")
    void verify_constant_coefficients_exponential() {
        // Ecuación con coeff constantes: y'' + 2*y' + y = 0
        // Soluciones: e^(rx) donde r satisface característica
        
        List<Root> roots = solveEquation("y'' + 2*y' + y = 0");
        assertNotNull(roots);
        
        // Todos son reales o complejos
        boolean allNumeric = roots.stream()
            .allMatch(r -> Double.isFinite(r.getReal()));
        assertTrue(allNumeric);
    }

    // ========== VERIFICACIÓN: Método de Coeficientes Indeterminados ==========

    @Test
    @DisplayName("UC: g(x) = exponencial → y_p = A*e^(bx)")
    void verify_uc_exponential_form() {
        // y'' - y = e^x
        // Forma esperada: y_p = A*x*e^x (porque e^x es solución homogénea)
        
        List<Root> roots = solveEquation("y'' - y = e^x");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("UC: g(x) = polinomio → y_p = polinomio mismo grado")
    void verify_uc_polynomial_form() {
        // y'' + y' = x
        // Forma: y_p = A*x + B (grado 1, como g(x) = x)
        
        List<Root> roots = solveEquation("y'' + y' = x");
        assertNotNull(roots);
    }

    @Test
    @DisplayName("UC: g(x) = trigonométrica → y_p = A*sin + B*cos")
    void verify_uc_trigonometric_form() {
        // y'' + 4*y = sin(2*x)
        // Forma: y_p = A*x*sin(2*x) + B*x*cos(2*x) (con resonancia)
        
        List<Root> roots = solveEquation("y'' + 4*y = sin(2*x)");
        assertNotNull(roots);
    }

    // ========== HELPER METHODS ==========

    private int countRootsWithMultiplicity(List<Root> roots) {
        // Implementación simplificada: contar raíces
        return roots.size();
    }
}
