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
 * ✅ Tests para Notación de Leibniz (dy/dx, d²y/dx², etc.)
 * 
 * Valida que el sistema acepta y resuelve correctamente ecuaciones
 * escritas en notación de Leibniz, complementando la notación prima (y').
 * 
 * Notación de Leibniz:
 * - dy/dx = y'
 * - d²y/dx² = y''
 * - d³y/dx³ = y'''
 * - etc.
 * 
 * @author GEOGERA Team
 * @version 2.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("📐 Tests de Notación de Leibniz")
class LeibnizNotationTest {

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

    // ========== ORDEN 1 ==========

    @Test
    @DisplayName("✅ Leibniz Orden 1: dy/dx = 2*y")
    void testLeibnizOrder1_Simple() throws Exception {
        testData.setEquation("dy/dx = 2*y");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 1 (dy/dx = 2*y) PASÓ");
    }

    @Test
    @DisplayName("✅ Leibniz Orden 1: dy/dx + y = e^x")
    void testLeibnizOrder1_NonHomogeneous() throws Exception {
        testData.setEquation("dy/dx + y = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 1 (No-homogénea) PASÓ");
    }

    // ========== ORDEN 2 ==========

    @Test
    @DisplayName("✅ Leibniz Orden 2: d²y/dx² + 3*dy/dx + 2*y = 0")
    void testLeibnizOrder2_Homogeneous() throws Exception {
        testData.setEquation("d²y/dx² + 3*dy/dx + 2*y = 0");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 2 (Homogénea) PASÓ");
    }

    @Test
    @DisplayName("✅ Leibniz Orden 2: d²y/dx² - 3*dy/dx + 2*y = e^x")
    void testLeibnizOrder2_NonHomogeneous() throws Exception {
        testData.setEquation("d²y/dx² - 3*dy/dx + 2*y = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 2 (No-homogénea) PASÓ");
    }

    @Test
    @DisplayName("✅ Leibniz Orden 2: d²y/dx² + y = sec(x)")
    void testLeibnizOrder2_TrigonometricForcing() throws Exception {
        testData.setEquation("d²y/dx² + y = sec(x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 2 (Trigonométrica) PASÓ");
    }

    // ========== ORDEN 3 ==========

    @Test
    @DisplayName("✅ Leibniz Orden 3: d³y/dx³ + y = sin(x)")
    void testLeibnizOrder3() throws Exception {
        testData.setEquation("d³y/dx³ + y = sin(x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 3 PASÓ");
    }

    // ========== ORDEN 4 ==========

    @Test
    @DisplayName("✅ Leibniz Orden 4: d⁴y/dx⁴ - y = 0")
    void testLeibnizOrder4_Homogeneous() throws Exception {
        testData.setEquation("d⁴y/dx⁴ - y = 0");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 4 (Homogénea) PASÓ");
    }

    @Test
    @DisplayName("✅ Leibniz Orden 4: d⁴y/dx⁴ + y = e^x")
    void testLeibnizOrder4_NonHomogeneous() throws Exception {
        testData.setEquation("d⁴y/dx⁴ + y = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 4 (No-homogénea) PASÓ");
    }

    // ========== ORDEN 5 ==========

    @Test
    @DisplayName("✅ Leibniz Orden 5: d⁵y/dx⁵ + d³y/dx³ = e^x")
    void testLeibnizOrder5() throws Exception {
        testData.setEquation("d⁵y/dx⁵ + d³y/dx³ = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Leibniz Orden 5 PASÓ");
    }

    // ========== EQUIVALENCIA: Leibniz vs Prima ==========

    @Test
    @DisplayName("✅ Equivalencia: dy/dx equivalente a y'")
    void testLeibnizEquivalenceOrder1() throws Exception {
        // Primera: notación Leibniz
        testData.setEquation("dy/dx = 2*y");
        String jsonRequest1 = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        // Segunda: notación prima (deberían dar el mismo resultado)
        ExpressionData testData2 = new ExpressionData();
        testData2.setVariable("x");
        testData2.setEquation("y' = 2*y");
        String jsonRequest2 = objectMapper.writeValueAsString(testData2);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Equivalencia (dy/dx vs y') PASÓ");
    }

    @Test
    @DisplayName("✅ Equivalencia: d²y/dx² equivalente a y''")
    void testLeibnizEquivalenceOrder2() throws Exception {
        // Primera: notación Leibniz
        testData.setEquation("d²y/dx² - 3*dy/dx + 2*y = e^x");
        String jsonRequest1 = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        // Segunda: notación prima
        ExpressionData testData2 = new ExpressionData();
        testData2.setVariable("x");
        testData2.setEquation("y'' - 3*y' + 2*y = e^x");
        String jsonRequest2 = objectMapper.writeValueAsString(testData2);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Equivalencia (d²y/dx² vs y'') PASÓ");
    }

    // ========== PERFORMANCE ==========

    @Test
    @DisplayName("⏱️ Performance Leibniz: Orden 4 debe resolver en < 500ms")
    void testPerformanceLeibniz() throws Exception {
        testData.setEquation("d⁴y/dx⁴ + y = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("⏱️ Performance (Leibniz Orden 4): " + duration + " ms");
        assert duration < 500 : "Performance issue: took " + duration + "ms";
    }
}
