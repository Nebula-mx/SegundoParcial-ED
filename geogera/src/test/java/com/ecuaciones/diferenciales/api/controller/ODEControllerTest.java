package com.ecuaciones.diferenciales.api.controller;

import com.ecuaciones.diferenciales.api.dto.ExpressionData;
import com.ecuaciones.diferenciales.api.dto.SolutionResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🧪 Tests para la API REST /api/ode/solve
 * 
 * Prueba:
 * - EDOs de primer orden
 * - EDOs de segundo orden
 * - Ecuaciones homogéneas
 * - Ecuaciones no-homogéneas
 * - Condiciones iniciales
 * - Casos de error
 */
@SpringBootTest
@AutoConfigureMockMvc
class ODEControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    private Gson gson;
    
    @BeforeEach
    void setUp() {
        gson = new Gson().newBuilder()
                .setPrettyPrinting()
                .create();
    }
    
    // ═══════════════════════════════════════════════════════════
    // TESTS DE ÉXITO
    // ═══════════════════════════════════════════════════════════
    
    /**
     * ✅ Test 1: EDO de primer orden lineal simple
     * Ecuación: y' + y = 0
     * Esperado: SUCCESS
     */
    @Test
    void testFirstOrderLinearHomogeneous() throws Exception {
        ExpressionData input = new ExpressionData(
            "y' + y = 0",
            null,
            "x"
        );
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.expression").exists())
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getExpression());
        assertEquals("y' + y = 0", response.getExpression());
        assertNotNull(response.getSteps());
    }
    
    /**
     * ✅ Test 2: EDO de segundo orden homogénea
     * Ecuación: y'' + 3y' + 2y = 0
     * Esperado: SUCCESS, varios pasos
     */
    @Test
    void testSecondOrderLinearHomogeneous() throws Exception {
        ExpressionData input = new ExpressionData(
            "y'' + 3y' + 2y = 0",
            null,
            "x"
        );
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.steps").isArray())
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertTrue(response.isSuccess());
        assertTrue(response.getSteps().size() > 0, "Debería haber al menos un paso");
    }
    
    /**
     * ✅ Test 3: EDO de segundo orden no-homogénea
     * Ecuación: y'' - 3y' + 2y = e^x
     * Esperado: SUCCESS, solución particular + homogénea
     */
    @Test
    void testSecondOrderNonHomogeneous() throws Exception {
        ExpressionData input = new ExpressionData(
            "y'' - 3y' + 2y = e^x",
            null,
            "x"
        );
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getFinalSolution());
    }
    
    /**
     * ✅ Test 4: EDO con raíces complejas
     * Ecuación: y'' + y = 0 (y'' + 0*y' + 1*y = 0)
     * Esperado: SUCCESS, raíces complejas (±i)
     */
    @Test
    void testComplexRoots() throws Exception {
        ExpressionData input = new ExpressionData(
            "y'' + y = 0",
            null,
            "x"
        );
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getSteps());
    }
    
    /**
     * ✅ Test 5: EDO con raíces reales repetidas
     * Ecuación: y'' - 2y' + y = 0 (característica: (r-1)^2 = 0)
     * Esperado: SUCCESS, raíz repetida r=1
     */
    @Test
    void testRepeatedRoots() throws Exception {
        ExpressionData input = new ExpressionData(
            "y'' - 2y' + y = 0",
            null,
            "x"
        );
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertTrue(response.isSuccess());
    }
    
    /**
     * ✅ Test 6: EDO de primer orden no-homogénea
     * Ecuación: y' + 2y = e^(-x)
     * Esperado: SUCCESS, método factor integrante
     */
    @Test
    void testFirstOrderNonHomogeneous() throws Exception {
        ExpressionData input = new ExpressionData(
            "y' + 2y = e^(-x)",
            null,
            "x"
        );
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertTrue(response.isSuccess());
    }
    
    // ═══════════════════════════════════════════════════════════
    // TESTS DE ERROR - VALIDACIÓN
    // ═══════════════════════════════════════════════════════════
    
    /**
     * ❌ Test 7: Ecuación vacía
     * Esperado: ERROR 400, "La ecuación no puede estar vacía"
     */
    @Test
    void testEmptyEquation() throws Exception {
        ExpressionData input = new ExpressionData("", null, "x");
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("La ecuación no puede estar vacía"));
    }
    
    /**
     * ❌ Test 8: Variable inválida (más de un carácter)
     * Esperado: ERROR 400, "La variable debe ser un solo carácter"
     */
    @Test
    void testInvalidVariable() throws Exception {
        ExpressionData input = new ExpressionData("y' + y = 0", null, "xx");
        
        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("La variable independiente debe ser un solo carácter (ej: x, t, u)"));
    }
    
    /**
     * ❌ Test 9: Ecuación sin función y
     * Esperado: ERROR 400, "La ecuación debe contener y o derivadas"
     */
    @Test
    void testEquationWithoutY() throws Exception {
        ExpressionData input = new ExpressionData("x^2 + 3 = 0", null, "x");
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertFalse(response.isSuccess());
    }
    
    /**
     * ❌ Test 10: Ecuación demasiado larga
     * Esperado: ERROR 400, "demasiado larga"
     */
    @Test
    void testEquationTooLong() throws Exception {
        String longEquation = "y' + y = " + "x".repeat(1001);
        ExpressionData input = new ExpressionData(longEquation, null, "x");
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("larga"));
    }
    
    // ═══════════════════════════════════════════════════════════
    // TESTS DE ESTRUCTURA
    // ═══════════════════════════════════════════════════════════
    
    /**
     * ✅ Test 11: Validar estructura de respuesta
     * Esperado: response siempre tiene expression y status
     */
    @Test
    void testResponseStructure() throws Exception {
        ExpressionData input = new ExpressionData("y' + y = 0", null, "x");
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expression").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.steps").exists())
                .andReturn();
        
        String jsonResponse = result.getResponse().getContentAsString();
        SolutionResponse response = gson.fromJson(jsonResponse, SolutionResponse.class);
        
        assertNotNull(response.getExpression());
        assertNotNull(response.getStatus());
        assertNotNull(response.getMessage());
        assertNotNull(response.getSteps());
    }
    
    /**
     * ✅ Test 12: Health check endpoint
     */
    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").exists());
    }
    
    /**
     * ✅ Test 13: Examples endpoint
     */
    @Test
    void testExamplesEndpoint() throws Exception {
        mockMvc.perform(get("/api/ode/examples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.examples").isArray());
    }
}
