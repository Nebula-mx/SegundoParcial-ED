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
import static org.hamcrest.Matchers.containsString;

/**
 * ✅ Tests para ecuaciones diferenciales de orden superior (orden >= 3)
 * 
 * Valida que la solución generalizada de Variación de Parámetros
 * funciona correctamente para:
 * - Orden 3
 * - Orden 4
 * - Raíces complejas en orden superior
 * - Raíces repetidas en orden superior
 * 
 * @author GEOGERA Team
 * @version 2.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("🔬 Tests de Ecuaciones de Orden Superior")
class HigherOrderTest {

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

    // ========== ORDEN 3 ==========

    @Test
    @DisplayName("✅ Test Orden 3: y''' - 2*y'' + 2*y' - y = e^x")
    void testOrder3_ExponentialForcing() throws Exception {
        testData.setEquation("y''' - 2*y'' + 2*y' - y = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.equation").value("y''' - 2*y'' + 2*y' - y = e^x"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 3 (Exponencial) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 3: y''' + 3*y'' + 3*y' + y = x^2")
    void testOrder3_PolynomialForcing() throws Exception {
        testData.setEquation("y''' + 3*y'' + 3*y' + y = x^2");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 3 (Polinomial) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 3: y''' - 6*y'' + 11*y' - 6*y = sin(x)")
    void testOrder3_TrigonometricForcing() throws Exception {
        testData.setEquation("y''' - 6*y'' + 11*y' - 6*y = sin(x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 3 (Trigonométrica) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 3: y''' - 3*y'' + 3*y' - y = e^x*x^2")
    void testOrder3_RepeatedRootsComplex() throws Exception {
        testData.setEquation("y''' - 3*y'' + 3*y' - y = e^x*x^2");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 3 (Raíces repetidas) PASÓ");
    }

    // ========== ORDEN 4 ==========

    @Test
    @DisplayName("✅ Test Orden 4: y'''' - 2*y''' + y'' = e^(2*x)")
    void testOrder4_ExponentialForcing() throws Exception {
        testData.setEquation("y'''' - 2*y''' + y'' = e^(2*x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 4 (Exponencial) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 4: y'''' + 2*y'' + y = cos(x)")
    void testOrder4_TrigonometricForcing() throws Exception {
        testData.setEquation("y'''' + 2*y'' + y = cos(x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 4 (Trigonométrica) PASÓ");
    }

    @Test
    @DisplayName("✅ Test Orden 4: y'''' - 4*y''' + 6*y'' - 4*y' + y = 1/x")
    void testOrder4_RationalForcing() throws Exception {
        testData.setEquation("y'''' - 4*y''' + 6*y'' - 4*y' + y = 1/x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 4 (Racional) PASÓ");
    }

    // ========== ORDEN 5 ==========

    @Test
    @DisplayName("✅ Test Orden 5: y''''' + y''' = e^x")
    void testOrder5_ExponentialForcing() throws Exception {
        testData.setEquation("y''''' + y''' = e^x");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 5 (Exponencial) PASÓ");
    }

    // ========== HOMOGÉNEAS DE ORDEN SUPERIOR ==========

    @Test
    @DisplayName("✅ Test Homogénea Orden 3: y''' - 2*y'' + 2*y' - y = 0")
    void testHomogenousOrder3() throws Exception {
        testData.setEquation("y''' - 2*y'' + 2*y' - y = 0");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Homogénea Orden 3 PASÓ");
    }

    @Test
    @DisplayName("✅ Test Homogénea Orden 4: y'''' - 2*y''' + y'' = 0")
    void testHomogenousOrder4() throws Exception {
        testData.setEquation("y'''' - 2*y''' + y'' = 0");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Homogénea Orden 4 PASÓ");
    }

    // ========== PERFORMANCE ==========

    @Test
    @DisplayName("⏱️ Test Performance: Orden 4 debe resolver en < 500ms")
    void testPerformanceHigherOrder() throws Exception {
        testData.setEquation("y'''' - 2*y''' + y'' = e^(2*x)");

        String jsonRequest = objectMapper.writeValueAsString(testData);

        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("⏱️ Performance Test: " + duration + " ms");
        assert duration < 500 : "Performance issue: took " + duration + "ms";
    }
}
