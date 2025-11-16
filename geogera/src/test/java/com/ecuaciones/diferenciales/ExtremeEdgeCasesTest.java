package com.ecuaciones.diferenciales;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.utils.SymjaEngine;

import java.util.*;

@DisplayName("Extreme Edge Cases")
public class ExtremeEdgeCasesTest {

    private List<Root> solveEquation(String equation) {
        EcuationParser parser = new EcuationParser();
        ExpressionData data = parser.parse(equation);
        if (data == null || data.getCoefficients() == null) return new ArrayList<>();
        List<Double> coeffs = Arrays.asList(data.getCoefficients());
        return PolynomialSolver.solve(coeffs);
    }

    // Order 3
    @Test
    @DisplayName("Order 3: y''' - 6*y'' + 11*y' - 6*y = 0")
    void testOrder3() {
        List<Root> roots = solveEquation("y''' - 6*y'' + 11*y' - 6*y = 0");
        assertEquals(3, roots.size());
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
        assertFalse(solution.isEmpty());
    }

    // Order 4
    @Test
    @DisplayName("Order 4: y'''' + 2*y'' + y = 0")
    void testOrder4() {
        List<Root> roots = solveEquation("y'''' + 2*y'' + y = 0");
        assertTrue(roots.size() > 0, "Debe tener al menos una raíz");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Order 5
    @Test
    @DisplayName("Order 5: y''''' + y''' = 0")
    void testOrder5() {
        List<Root> roots = solveEquation("y''''' + y''' = 0");
        assertTrue(roots.size() > 0);
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Large coefficients
    @Test
    @DisplayName("Large Coefficients: 1000*y'' + 500*y' + 100*y = 0")
    void testLargeCoefficients() {
        List<Root> roots = solveEquation("1000*y'' + 500*y' + 100*y = 0");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Small coefficients
    @Test
    @DisplayName("Small Coefficients: 0.001*y'' + 0.0001*y' + 0.00001*y = 0")
    void testSmallCoefficients() {
        List<Root> roots = solveEquation("0.001*y'' + 0.0001*y' + 0.00001*y = 0");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Mixed scale
    @Test
    @DisplayName("Mixed Scale: 1000*y'' + 0.001*y' + 100*y = 0")
    void testMixedScale() {
        List<Root> roots = solveEquation("1000*y'' + 0.001*y' + 100*y = 0");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Complex forcing
    @Test
    @DisplayName("Complex Forcing: y'' - y = x*e^x*sin(x)")
    void testComplexForcing() {
        List<Root> roots = solveEquation("y'' - y = x*e^x*sin(x)");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // High degree polynomial
    @Test
    @DisplayName("High Degree Polynomial: y'' - y = x^5 + x^3")
    void testHighDegreePolynomial() {
        List<Root> roots = solveEquation("y'' - y = x^5 + x^3");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Multiple trig
    @Test
    @DisplayName("Multiple Trig: y'' + y = sin(x) + cos(2*x) + sin(3*x)")
    void testMultipleTrig() {
        List<Root> roots = solveEquation("y'' + y = sin(x) + cos(2*x) + sin(3*x)");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Integrate reciprocal
    @Test
    @DisplayName("Integrate: 1/x")
    void testIntegrateReciprocal() {
        String result = SymjaEngine.symbolicIntegral("1/x");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Integrate radical
    @Test
    @DisplayName("Integrate: sqrt(x)")
    void testIntegrateRadical() {
        String result = SymjaEngine.symbolicIntegral("sqrt(x)");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Integrate logarithm
    @Test
    @DisplayName("Integrate: ln(x)")
    void testIntegrateLogarithm() {
        String result = SymjaEngine.symbolicIntegral("ln(x)");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Integrate non-elementary
    @Test
    @DisplayName("Integrate: e^(x^2)")
    void testIntegrateNonElementary() {
        String result = SymjaEngine.symbolicIntegral("e^(x^2)");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Integrate product
    @Test
    @DisplayName("Integrate: e^x*sin(x)")
    void testIntegrateProduct() {
        String result = SymjaEngine.symbolicIntegral("e^x*sin(x)");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Integrate high degree
    @Test
    @DisplayName("Integrate: x^5")
    void testIntegrateHighDegree() {
        String result = SymjaEngine.symbolicIntegral("x^5");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Resonance order 3
    @Test
    @DisplayName("Resonance Order 3: y''' - y'' = e^x")
    void testResonanceOrder3() {
        List<Root> roots = solveEquation("y''' - y'' = e^x");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Double resonance
    @Test
    @DisplayName("Double Resonance: y'' + 2*y' + y = e^(-x)")
    void testDoubleResonance() {
        List<Root> roots = solveEquation("y'' + 2*y' + y = e^(-x)");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Order 1
    @Test
    @DisplayName("Order 1: y' - 2*y = 0")
    void testOrder1() {
        List<Root> roots = solveEquation("y' - 2*y = 0");
        assertEquals(1, roots.size());
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
        assertTrue(solution.contains("e^") || solution.contains("E^"));
    }

    // Order 2 distinct
    @Test
    @DisplayName("Order 2 Distinct: y'' - 5*y' + 6*y = 0")
    void testOrder2Distinct() {
        List<Root> roots = solveEquation("y'' - 5*y' + 6*y = 0");
        assertEquals(2, roots.size());
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Order 2 repeated
    @Test
    @DisplayName("Order 2 Repeated: y'' - 4*y' + 4*y = 0")
    void testOrder2Repeated() {
        List<Root> roots = solveEquation("y'' - 4*y' + 4*y = 0");
        assertTrue(roots.size() > 0, "Debe tener al menos una raíz");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Order 2 complex
    @Test
    @DisplayName("Order 2 Complex: y'' + 2*y' + 5*y = 0")
    void testOrder2Complex() {
        List<Root> roots = solveEquation("y'' + 2*y' + 5*y = 0");
        assertTrue(roots.size() > 0, "Debe tener al menos una raíz");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
        assertTrue(solution.contains("sin") || solution.contains("cos") ||
                  solution.contains("Sin") || solution.contains("Cos"));
    }

    // Hyperbolic
    @Test
    @DisplayName("Hyperbolic: y'' - y = sinh(x)")
    void testHyperbolic() {
        List<Root> roots = solveEquation("y'' - y = sinh(x)");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Inverse trig
    @Test
    @DisplayName("Inverse Trig: y'' + y = arctan(x)")
    void testInverseTrig() {
        List<Root> roots = solveEquation("y'' + y = arctan(x)");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Stiff
    @Test
    @DisplayName("Stiff: 1000*y'' + 1*y' + 1000*y = 0")
    void testStiff() {
        List<Root> roots = solveEquation("1000*y'' + 1*y' + 1000*y = 0");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }

    // Nearly singular
    @Test
    @DisplayName("Nearly Singular: y'' + 0.0001*y' + y = 0")
    void testNearlySingular() {
        List<Root> roots = solveEquation("y'' + 0.0001*y' + y = 0");
        HomogeneousSolver solver = new HomogeneousSolver();
        String solution = solver.generateHomogeneousSolution(roots);
        assertNotNull(solution);
    }
}
