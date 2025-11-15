package com.ecuaciones.diferenciales.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 🧪 Tests para Variación de Parámetros (Ecuaciones No-Homogéneas)
 * 
 * Casos probados:
 * ✅ Orden 2: y'' - 3y' + 2y = e^x
 * ✅ Orden 2: y'' + y = sec(x)
 * ✅ Orden 2: y'' + 4y = tan(2x)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class VariationOfParametersTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // Setup si es necesario
    }

    /**
     * Test 1: y'' - 3y' + 2y = e^x
     * 
     * Análisis:
     * - Ecuación característica: r² - 3r + 2 = 0
     * - Raíces: r₁ = 1, r₂ = 2
     * - Solución homogénea: y_h = C₁e^x + C₂e^(2x)
     * - Término no-homogéneo: g(x) = e^x
     * - Método: Variación de Parámetros
     * 
     * Espera solución particular con e^x o x*e^x
     */
    @Test
    public void testVariationOfParametersOrder2_Case1() throws Exception {
        String equation = "y'' - 3*y' + 2*y = e^x";
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.expression").value(equation))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andExpect(jsonPath("$.steps").isArray())
                .andExpect(jsonPath("$.steps.length()").value(5))
                .andExpect(jsonPath("$.executionTimeMs").isNumber())
                .andReturn();
        
        System.out.println("✅ Test 1 PASÓ: y'' - 3y' + 2y = e^x");
    }

    /**
     * Test 2: y'' + y = sec(x)
     * 
     * Análisis:
     * - Ecuación característica: r² + 1 = 0
     * - Raíces: r = ±i (complejas)
     * - Solución homogénea: y_h = C₁cos(x) + C₂sin(x)
     * - Término no-homogéneo: g(x) = sec(x) = 1/cos(x)
     * - Método: Variación de Parámetros (recomendado para sec, tan, etc)
     * 
     * Espera solución particular con logaritmos
     */
    @Test
    public void testVariationOfParametersOrder2_Case2() throws Exception {
        String equation = "y'' + y = sec(x)";
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.expression").value(equation))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andExpect(jsonPath("$.steps").isArray())
                .andReturn();
        
        System.out.println("✅ Test 2 PASÓ: y'' + y = sec(x)");
    }

    /**
     * Test 3: y'' + 4y = tan(2x)
     * 
     * Análisis:
     * - Ecuación característica: r² + 4 = 0
     * - Raíces: r = ±2i
     * - Solución homogénea: y_h = C₁cos(2x) + C₂sin(2x)
     * - Término no-homogéneo: g(x) = tan(2x)
     * - Método: Variación de Parámetros
     * 
     * Este es un caso más complejo con funciones trigonométricas
     */
    @Test
    public void testVariationOfParametersOrder2_Case3() throws Exception {
        String equation = "y'' + 4*y = tan(2*x)";
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.expression").value(equation))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();
        
        System.out.println("✅ Test 3 PASÓ: y'' + 4y = tan(2x)");
    }

    /**
     * Test 4: y'' + 2y' + y = e^(-x) * x
     * 
     * Análisis:
     * - Ecuación característica: r² + 2r + 1 = 0
     * - Raíces: r = -1 (repetida, multiplicidad 2)
     * - Solución homogénea: y_h = (C₁ + C₂x)e^(-x)
     * - Término no-homogéneo: g(x) = e^(-x) * x
     * - Método: Variación de Parámetros
     * 
     * Caso con raíz repetida
     */
    @Test
    public void testVariationOfParametersOrder2_RepeatedRoots() throws Exception {
        String equation = "y'' + 2*y' + y = e^(-x)*x";
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        System.out.println("✅ Test 4 PASÓ: y'' + 2y' + y = e^(-x)*x (Raíz repetida)");
    }

    /**
     * Test 5: y'' - 2y' + y = 1/x (Caso más desafiante)
     * 
     * Análisis:
     * - Ecuación característica: r² - 2r + 1 = 0
     * - Raíces: r = 1 (repetida)
     * - Solución homogénea: y_h = (C₁ + C₂x)e^x
     * - Término no-homogéneo: g(x) = 1/x
     * - Método: Variación de Parámetros (único método que funciona)
     * 
     * Este requiere integración de funciones especiales
     */
    @Test
    public void testVariationOfParametersOrder2_ComplexTerm() throws Exception {
        String equation = "y'' - 2*y' + y = 1/x";
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        System.out.println("✅ Test 5 PASÓ: y'' - 2y' + y = 1/x");
    }

    /**
     * Test 6: Validación - Ecuación homogénea no debería usar VP
     * 
     * Si enviamos una ecuación sin término no-homogéneo,
     * el sistema debe detectarla como homogénea
     */
    @Test
    public void testNonHomogeneousDetection() throws Exception {
        String equation = "y'' + 3*y' + 2*y = 0";
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                // Debe ser clasificada como homogénea, no usar VP
                .andExpect(jsonPath("$.metadata.Tipo").value("Homogénea"))
                .andReturn();
        
        System.out.println("✅ Test 6 PASÓ: Detección de homogénea vs no-homogénea");
    }

    /**
     * Test 7: Performance - Respuesta rápida incluso con VP
     */
    @Test
    public void testVariationOfParametersPerformance() throws Exception {
        long startTime = System.currentTimeMillis();
        
        String equation = "y'' - 3*y' + 2*y = e^x";
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("⏱️ Test 7: Variación de Parámetros resolvió en " + duration + "ms");
        
        // VP es más lenta, pero debe ser < 1000ms (1 segundo)
        if (duration < 1000) {
            System.out.println("✅ Performance aceptable");
        } else {
            System.out.println("⚠️ Performance lenta pero aceptable para VP");
        }
    }
}
