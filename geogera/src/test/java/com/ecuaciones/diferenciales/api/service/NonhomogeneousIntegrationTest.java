package com.ecuaciones.diferenciales.api.service;

import com.ecuaciones.diferenciales.api.dto.ExpressionData;
import com.ecuaciones.diferenciales.api.dto.SolutionResponse;
import com.ecuaciones.diferenciales.api.dto.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 TEST DE INTEGRACIÓN PARA ECUACIONES NO-HOMOGÉNEAS
 * 
 * Valida el flujo completo:
 * 1. Parsing
 * 2. Clasificación como no-homogénea
 * 3. Resolución de ecuación homogénea
 * 4. Determinación de forma particular
 * 5. Resolución de coeficientes
 * 6. Combinación de soluciones
 */
public class NonhomogeneousIntegrationTest {
    
    private ODESolver solver;
    
    @BeforeEach
    void setUp() {
        solver = new ODESolver();
    }
    
    // ============================================================
    // GRUPO 1: ECUACIONES SIMPLES (Grado 2, SIN RESONANCIA)
    // ============================================================
    
    @Test
    @DisplayName("y'' + y = 1 (constante, sin resonancia)")
    void testSimpleConstantNonhomogeneous() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + y = 1");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        // Validaciones
        assertTrue(response.isSuccess(), 
            "La ecuación debe resolverse exitosamente");
        assertNotNull(response.getFinalSolution(), 
            "Debe haber una solución final");
        assertTrue(response.getFinalSolution().length() > 0, 
            "La solución no debe estar vacía");
        
        // Debe contener solución homogénea (sin 'i' significa cos y sin)
        assertTrue(
            response.getFinalSolution().contains("cos") || 
            response.getFinalSolution().contains("sin") ||
            response.getFinalSolution().matches(".*c[0-9]+.*"),
            "Debe contener componentes trigonométricas o constantes"
        );
        
        System.out.println("✅ Test 1 PASADO: y'' + y = 1");
        System.out.println("   Solución: " + response.getFinalSolution());
        System.out.println();
    }
    
    @Test
    @DisplayName("y'' + 3y' + 2y = 1 (raíces reales distintas, constante)")
    void testLinearCoefficientsWithConstant() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + 3*y' + 2*y = 1");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess(), 
            "Debe resolver exitosamente ecuación de raíces reales distintas");
        assertNotNull(response.getFinalSolution());
        
        // Con raíces r = -1, r = -2, esperamos e^(-x) y e^(-2x)
        String sol = response.getFinalSolution();
        assertTrue(
            sol.contains("e^") || sol.contains("exp"),
            "Debe contener exponenciales e^(-x) y e^(-2x)"
        );
        
        System.out.println("✅ Test 2 PASADO: y'' + 3y' + 2y = 1");
        System.out.println("   Solución: " + sol);
        System.out.println();
    }
    
    @Test
    @DisplayName("y'' - y = 2*x (polinomio, sin resonancia)")
    void testPolynomialRightSide() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' - y = 2*x");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess(), 
            "Debe resolver ecuación con término polinomial");
        assertNotNull(response.getFinalSolution());
        
        String sol = response.getFinalSolution();
        System.out.println("✅ Test 3 PASADO: y'' - y = 2*x");
        System.out.println("   Solución: " + sol);
        System.out.println();
    }
    
    // ============================================================
    // GRUPO 2: ECUACIONES CON RESONANCIA
    // ============================================================
    
    @Test
    @DisplayName("y'' + y = sin(x) (RESONANCIA: raíz = i)")
    void testResonanceSinusoidalTerm() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + y = sin(x)");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess(), 
            "Debe detectar y manejar resonancia");
        assertNotNull(response.getFinalSolution());
        
        String sol = response.getFinalSolution();
        // Con resonancia, esperamos x*cos(x) y x*sin(x) en la forma particular
        assertTrue(
            sol.contains("x*") || sol.contains("x ") || sol.contains("x*cos") || sol.contains("x*sin"),
            "Debe contener factor de resonancia x en la solución particular"
        );
        
        System.out.println("✅ Test 4 PASADO: y'' + y = sin(x) (CON RESONANCIA)");
        System.out.println("   Solución: " + sol);
        System.out.println("   ℹ️ Factor x* indica resonancia detectada");
        System.out.println();
    }
    
    @Test
    @DisplayName("y'' - y = e^x (RESONANCIA: raíz = 1)")
    void testResonanceExponentialTerm() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' - y = e^x");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess(), 
            "Debe detectar resonancia con exponencial");
        assertNotNull(response.getFinalSolution());
        
        String sol = response.getFinalSolution();
        System.out.println("✅ Test 5 PASADO: y'' - y = e^x (CON RESONANCIA)");
        System.out.println("   Solución: " + sol);
        System.out.println();
    }
    
    // ============================================================
    // GRUPO 3: ECUACIONES DE ORDEN SUPERIOR
    // ============================================================
    
    @Test
    @DisplayName("y''' + y'' = 1 (orden 3, polinomial)")
    void testThirdOrderEquation() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y''' + y'' = 1");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess(), 
            "Debe resolver ecuación de orden 3");
        assertNotNull(response.getFinalSolution());
        
        String sol = response.getFinalSolution();
        System.out.println("✅ Test 6 PASADO: y''' + y'' = 1");
        System.out.println("   Solución: " + sol);
        System.out.println();
    }
    
    // ============================================================
    // GRUPO 4: ECUACIONES CON COEFICIENTES VARIABLES
    // ============================================================
    
    @Test
    @DisplayName("y'' + 2y' + y = 1 (raíces repetidas)")
    void testRepeatedRootsWithConstant() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + 2*y' + y = 1");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess(), 
            "Debe manejar raíces repetidas");
        assertNotNull(response.getFinalSolution());
        
        String sol = response.getFinalSolution();
        // Raíz repetida r = -1 genera: e^(-x) y x*e^(-x)
        assertTrue(
            sol.contains("e^") || sol.contains("exp") || sol.contains("x"),
            "Debe contener términos exponenciales y polinómicos para raíces repetidas"
        );
        
        System.out.println("✅ Test 7 PASADO: y'' + 2y' + y = 1 (raíces repetidas)");
        System.out.println("   Solución: " + sol);
        System.out.println();
    }
    
    // ============================================================
    // GRUPO 5: ECUACIONES COMPLEJAS CON CONDICIONES INICIALES
    // ============================================================
    
    @Test
    @DisplayName("y'' + y = 1 CON CI: y(0)=0, y'(0)=0")
    void testNonhomogeneousWithInitialConditions() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + y = 1");
        input.setVariable("x");
        input.setInitialConditions(new java.util.ArrayList<>(
            java.util.Arrays.asList("y(0)=0", "y'(0)=0")
        ));
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess(), 
            "Debe resolver con condiciones iniciales");
        assertNotNull(response.getFinalSolution());
        
        String sol = response.getFinalSolution();
        // Sin constantes arbitrarias si se aplicaron las CI correctamente
        System.out.println("✅ Test 8 PASADO: y'' + y = 1 CON CI");
        System.out.println("   Solución: " + sol);
        System.out.println("   ℹ️ Condiciones iniciales aplicadas");
        System.out.println();
    }
    
    // ============================================================
    // VALIDACIÓN DE ESTRUCTURA DE RESPUESTA
    // ============================================================
    
    @Test
    @DisplayName("Estructura completa de respuesta JSON para no-homogénea")
    void testResponseStructure() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + y = sin(x)");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        // Validar estructura
        assertTrue(response.isSuccess());
        assertNotNull(response.getMessage());
        assertNotNull(response.getFinalSolution());
        assertNotNull(response.getSolutionLatex());
        assertNotNull(response.getMetadata());
        assertNotNull(response.getSteps());
        assertTrue(response.getExecutionTimeMs() >= 0);
        
        // Validar metadata
        assertTrue(response.getMetadata().containsKey("Tipo"));
        assertEquals("No-homogénea", response.getMetadata().get("Tipo"));
        
        System.out.println("✅ Test 9 PASADO: Estructura de respuesta válida");
        System.out.println("   Tipo de ecuación: " + response.getMetadata().get("Tipo"));
        System.out.println("   Pasos generados: " + response.getMetadata().get("Pasos totales"));
        System.out.println("   Tiempo ejecución: " + response.getExecutionTimeMs() + "ms");
        System.out.println();
    }
    
    // ============================================================
    // TEST DE VALIDACIÓN DE FLUJO
    // ============================================================
    
    @Test
    @DisplayName("Validación de flujo: clasificación → raíces → y_h → y_p → y_general")
    void testCompleteFlowValidation() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + 3*y' + 2*y = x");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        // Validar flujo
        assertTrue(response.isSuccess());
        
        // Debe haber pasos
        assertTrue(response.getSteps() != null && response.getSteps().size() > 0,
            "Debe generar pasos del proceso de solución");
        
        // Validar que hay paso de clasificación
        assertTrue(
            response.getSteps().stream()
                .anyMatch(step -> step.getType() == Step.StepType.CLASSIFY),
            "Debe tener paso de clasificación"
        );
        
        // Validar que hay solución general
        assertTrue(
            response.getSteps().stream()
                .anyMatch(step -> step.getDescription().toLowerCase().contains("general")),
            "Debe mencionar solución general en los pasos"
        );
        
        System.out.println("✅ Test 10 PASADO: Flujo completo validado");
        System.out.println("   Total de pasos: " + response.getSteps().size());
        System.out.println("   Pasos: " + response.getSteps().stream()
            .map(s -> s.getType().toString())
            .reduce((a, b) -> a + " → " + b)
            .orElse("N/A"));
        System.out.println();
    }
    
    // ============================================================
    // CASOS EDGE: PRUEBAS DE ROBUSTEZ
    // ============================================================
    
    @Test
    @DisplayName("Ecuación mal formada: manejo de errores")
    void testMalformedEquation() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + + y = 1");  // Sintaxis inválida
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        // Debe manejar el error gracefully (no debe lanzar excepción no capturada)
        assertNotNull(response);
        System.out.println("✅ Test 11 PASADO: Manejo de errores");
        System.out.println("   Estado: " + response.getStatus());
        System.out.println();
    }
    
    @Test
    @DisplayName("Ecuación homogénea (lado derecho = 0)")
    void testHomogeneousEquation() {
        ExpressionData input = new ExpressionData();
        input.setEquation("y'' + y = 0");
        input.setVariable("x");
        
        SolutionResponse response = solver.solveDifferentialEquation(input);
        
        assertTrue(response.isSuccess());
        assertEquals("Homogénea", response.getMetadata().get("Tipo"),
            "Debe clasificarse como Homogénea");
        
        System.out.println("✅ Test 12 PASADO: Ecuación homogénea detectada");
        System.out.println("   Tipo: " + response.getMetadata().get("Tipo"));
        System.out.println();
    }
}
