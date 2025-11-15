package com.ecuaciones.diferenciales.api.controller;

import com.ecuaciones.diferenciales.api.dto.ExpressionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Tests para ecuaciones de orden MUY ALTO (orden >= 6)
 * 
 * Valida que la solución generalizada de Variación de Parámetros
 * funciona correctamente para:
 * - Orden 6, 7, 8, 9, 10
 * - Homogéneas y no-homogéneas
 * - Funciones de forzamiento variadas
 * 
 * El sistema es completamente generalizado para cualquier orden n.
 * 
 * @author GEOGERA Team
 * @version 2.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("🚀 Tests de Orden MUY ALTO (6-10)")
class VeryHighOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ExpressionData testData;

    @BeforeEach
    void setUp() {
        testData = new ExpressionData();
        testData.setVariable("x");
    }

    // ========== ORDEN 6 ==========

    @Test
    @DisplayName("✅ Test Orden 6: y'''''' - y = e^x")
    void testOrder6_ExponentialForcing() throws Exception {
        testData.setEquation("y'''''' - y = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.equation").value("y'''''' - y = e^x"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 6 (Exponencial) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 6: y'''''' + y'''' + y'' + y = 0 (Homogénea)")
    void testOrder6_Homogeneous() throws Exception {
        testData.setEquation("y'''''' + y'''' + y'' + y = 0");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 6 (Homogénea) PASÓ");
    }

    // ========== ORDEN 7 ==========

    @Test
    @DisplayName("✅ Test Orden 7: y''''''' + y''''' = sin(x)")
    void testOrder7_TrigonometricForcing() throws Exception {
        testData.setEquation("y''''''' + y''''' = sin(x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 7 (Trigonométrica) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 7: y''''''' - 2*y''''' + y''' = cos(x)")
    void testOrder7_CosinusForcing() throws Exception {
        testData.setEquation("y''''''' - 2*y''''' + y''' = cos(x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 7 (Coseno) PASÓ");
    }

    // ========== ORDEN 8 ==========

    @Test
    @DisplayName("✅ Test Orden 8: y'''''''' + y'''''' + y = e^(2*x)")
    void testOrder8_ExponentialForcing() throws Exception {
        testData.setEquation("y'''''''' + y'''''' + y = e^(2*x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 8 (Exponencial) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 8: y'''''''' - y'''' = x^2 (Polinomial)")
    void testOrder8_PolynomialForcing() throws Exception {
        testData.setEquation("y'''''''' - y'''' = x^2");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 8 (Polinomial) PASÓ");
    }

    // ========== ORDEN 9 ==========

    @Test
    @DisplayName("✅ Test Orden 9: y''''''''' + y = x")
    void testOrder9_PolynomialForcing() throws Exception {
        testData.setEquation("y''''''''' + y = x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 9 (Polinomial) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 9: y''''''''' - y''''' + y''' = e^x")
    void testOrder9_ExponentialForcing() throws Exception {
        testData.setEquation("y''''''''' - y''''' + y''' = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 9 (Exponencial) PASÓ");
    }

    // ========== ORDEN 10 ==========

    @Test
    @DisplayName("✅ Test Orden 10: y'''''''''' - y = 0 (Homogénea)")
    void testOrder10_Homogeneous() throws Exception {
        testData.setEquation("y'''''''''' - y = 0");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 10 (Homogénea) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 10: y'''''''''' + y''''''' = e^x")
    void testOrder10_ExponentialForcing() throws Exception {
        testData.setEquation("y'''''''''' + y''''''' = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 10 (Exponencial) PASÓ");
    }

    // ========== PERFORMANCE ==========

    @Test
    @DisplayName("⏱️ Performance: Orden 10 debe resolver en < 1000ms")
    void testPerformanceVeryHighOrder() throws Exception {
        testData.setEquation("y'''''''''' + y''''''' = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("⏱️ Performance (Orden 10): " + duration + " ms");
        assert duration < 1000 : "Performance issue: took " + duration + "ms";
    }
}
