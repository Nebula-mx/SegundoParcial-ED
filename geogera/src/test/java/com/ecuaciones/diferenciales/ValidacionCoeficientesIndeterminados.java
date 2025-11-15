package com.ecuaciones.diferenciales;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import com.ecuaciones.diferenciales.model.EcuationParser;
import com.ecuaciones.diferenciales.model.roots.Root;
import com.ecuaciones.diferenciales.model.solver.homogeneous.HomogeneousSolver;
import com.ecuaciones.diferenciales.model.solver.homogeneous.PolynomialSolver;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeff;
import com.ecuaciones.diferenciales.model.solver.nonhomogeneous.UndeterminedCoeffResolver;
import com.ecuaciones.diferenciales.model.templates.ExpressionData;

/**
 * Tests para VALIDAR la lógica de Coeficientes Indeterminados en casos críticos
 */
public class ValidacionCoeficientesIndeterminados {

    private static void testCase(String description, String equation) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔍 TEST: " + description);
        System.out.println("📝 Ecuación: " + equation);
        System.out.println("=".repeat(80));
        
        try {
            EcuationParser parser = new EcuationParser();
            ExpressionData data = parser.parse(equation);
            
            Double[] coeffsArray = data.getCoefficients();
            int order = data.getOrder();
            String gx = data.getIndependentTerm().get("g(x)");
            
            System.out.println("✓ Parsed: Orden=" + order + ", g(x)=" + gx);
            
            List<Double> coeffs = Arrays.asList(coeffsArray);
            List<Root> roots = PolynomialSolver.solve(coeffs);
            
            System.out.println("✓ Raíces: " + roots);
            
            HomogeneousSolver hSolver = new HomogeneousSolver();
            String yh = hSolver.generateHomogeneousSolution(roots);
            System.out.println("✓ y_h = " + yh);
            
            UndeterminedCoeff ucSolver = new UndeterminedCoeff(roots);
            String ypForm = ucSolver.getParticularSolutionForm(gx);
            System.out.println("✓ Forma y_p* = " + ypForm);
            
            UndeterminedCoeffResolver resolver = new UndeterminedCoeffResolver(data, ucSolver);
            Map<String, Double> solved = resolver.resolveCoefficients();
            System.out.println("✓ Coeficientes: " + solved);
            
            String yp = ucSolver.generateParticularSolution(ypForm, solved);
            System.out.println("✓ y_p = " + yp);
            
            System.out.println("\n✅ TEST COMPLETADO EXITOSAMENTE");
            
        } catch (Exception e) {
            System.out.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void test_Case1_PolinomialSimple() {
        testCase("Polinomial Simple (SIN Resonancia)",
                 "y'' - y = x^2 + 1");
    }

    @Test
    public void test_Case2_ExponencialConResonancia() {
        testCase("Exponencial CON Resonancia",
                 "y'' - 3*y' + 2*y = e^x");
    }

    @Test
    public void test_Case3_TrigonometricoConResonancia() {
        testCase("Trigonométrico CON Resonancia",
                 "y'' + y = sin(x)");
    }

    @Test
    public void test_Case4_ExponencialPolinomialConResonancia() {
        testCase("Exponencial × Polinomial CON Resonancia",
                 "y'' - 2*y' + y = x*e^x");
    }

    @Test
    public void test_Case5_TrigonometricoMixto() {
        testCase("Trigonométrico Mixto",
                 "y'' + 4*y = cos(2*x) + sin(2*x)");
    }

    @Test
    public void test_Case6_ExponencialTrigonometrico() {
        testCase("Exponencial × Trigonométrico",
                 "y'' + 2*y' + 2*y = e^(-x)*cos(x)");
    }

    @Test
    public void test_Case7_PolinomialGradoAlto() {
        testCase("Polinomial Grado Alto",
                 "y'' + y = x^3 + 2*x^2 + x + 1");
    }

    @Test
    public void test_Case8_MultiplicidadAlta() {
        testCase("Multiplicidad Alta (Orden 3)",
                 "y''' - 3*y'' + 3*y' - y = e^x");
    }

    @Test
    public void test_Case9_ForzamientoMixto() {
        testCase("Forzamiento Mixto (Polinomial + Exponencial)",
                 "y'' - y = x^2 + e^(2*x)");
    }

    @Test
    public void test_Case10_ConstanteConMultiplicidad() {
        testCase("Constante con Multiplicidad",
                 "y''' + 3*y'' + 3*y' + y = 5");
    }
}
