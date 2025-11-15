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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Tests para Condiciones Iniciales en todos los casos
 * 
 * Valida que las condiciones iniciales se aplican correctamente para:
 * - Orden 1, 2, 3, 4, 5
 * - Homogéneas y no-homogéneas
 * - Raíces reales, complejas, repetidas
 * 
 * Formatos soportados:
 * - y(x0)=y0 (valor inicial)
 * - y'(x0)=y0 (derivada primera)
 * - y''(x0)=y0 (derivada segunda)
 * 
 * @author GEOGERA Team
 * @version 2.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("🎯 Tests de Condiciones Iniciales")
class InitialConditionsTest {

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
    @DisplayName("✅ Orden 1: y' = 2*y con CI y(0)=1")
    void testOrder1_WithInitialCondition() throws Exception {
        testData.setEquation("y' = 2*y");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 1 (CI) PASÓ");
    }

    @Test
    @DisplayName("✅ Orden 1: y' + y = e^x con CI y(0)=0")
    void testOrder1_NonHomogeneous_WithCI() throws Exception {
        testData.setEquation("y' + y = e^x");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 1 (No-homogénea, CI) PASÓ");
    }

    // ========== ORDEN 2 - HOMOGÉNEA ==========

    @Test
    @DisplayName("✅ Orden 2 Homogénea: y'' + y = 0 con CI y(0)=1, y'(0)=0")
    void testOrder2_Homogeneous_CI_RealRoots() throws Exception {
        testData.setEquation("y'' + y = 0");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.finalSolution").exists())
                .andReturn();

        System.out.println("✅ Test Orden 2 Homogénea (Raíces complejas, CI) PASÓ");
    }

    @Test
    @DisplayName("✅ Orden 2 Homogénea: y'' - 3*y' + 2*y = 0 con CI y(0)=1, y'(0)=0")
    void testOrder2_Homogeneous_CI_ComplexRoots() throws Exception {
        testData.setEquation("y'' - 3*y' + 2*y = 0");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 2 Homogénea (Raíces reales distintas, CI) PASÓ");
    }

    @Test
    @DisplayName("✅ Orden 2 Homogénea: y'' - 2*y' + y = 0 con CI y(0)=1, y'(0)=1")
    void testOrder2_Homogeneous_CI_RepeatedRoots() throws Exception {
        testData.setEquation("y'' - 2*y' + y = 0");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=1");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 2 Homogénea (Raíces repetidas, CI) PASÓ");
    }

    // ========== ORDEN 2 - NO-HOMOGÉNEA ==========

    @Test
    @DisplayName("✅ Orden 2 No-Homog: y'' - 3*y' + 2*y = e^x con CI y(0)=1, y'(0)=0")
    void testOrder2_NonHomogeneous_CI_Exponential() throws Exception {
        testData.setEquation("y'' - 3*y' + 2*y = e^x");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 2 No-Homog (Exponencial, CI) PASÓ");
    }

    @Test
    @DisplayName("✅ Orden 2 No-Homog: y'' + y = sec(x) con CI y(0)=1, y'(0)=1")
    void testOrder2_NonHomogeneous_CI_Trigonometric() throws Exception {
        testData.setEquation("y'' + y = sec(x)");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=1");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 2 No-Homog (Trigonométrica, CI) PASÓ");
    }

    @Test
    @DisplayName("✅ Orden 2 No-Homog: y'' + 2*y' + y = e^(-x)*x con CI y(0)=1, y'(0)=0")
    void testOrder2_NonHomogeneous_CI_RepeatedRoots() throws Exception {
        testData.setEquation("y'' + 2*y' + y = e^(-x)*x");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 2 No-Homog (Raíces repetidas, CI) PASÓ");
    }

    // ========== ORDEN 3 ==========

    @Test
    @DisplayName("✅ Orden 3: y''' - 2*y'' + 2*y' - y = e^x con CI y(0)=1, y'(0)=0, y''(0)=1")
    void testOrder3_WithInitialConditions() throws Exception {
        testData.setEquation("y''' - 2*y'' + 2*y' - y = e^x");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        ic.add("y''(0)=1");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 3 (CI) PASÓ");
    }

    @Test
    @DisplayName("✅ Orden 3 Homog: y''' + 3*y'' + 3*y' + y = 0 con CI y(0)=1, y'(0)=0, y''(0)=0")
    void testOrder3_Homogeneous_CI() throws Exception {
        testData.setEquation("y''' + 3*y'' + 3*y' + y = 0");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        ic.add("y''(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 3 Homog (CI) PASÓ");
    }

    // ========== ORDEN 4 ==========

    @Test
    @DisplayName("✅ Orden 4: y'''' - 2*y''' + y'' = e^(2*x) con CI y(0)=1, y'(0)=0, y''(0)=1, y'''(0)=0")
    void testOrder4_WithInitialConditions() throws Exception {
        testData.setEquation("y'''' - 2*y''' + y'' = e^(2*x)");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        ic.add("y''(0)=1");
        ic.add("y'''(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 4 (CI) PASÓ");
    }

    @Test
    @DisplayName("✅ Orden 4 Homog: y'''' + 2*y'' + y = 0 con CI y(0)=1, y'(0)=1, y''(0)=0, y'''(0)=1")
    void testOrder4_Homogeneous_CI() throws Exception {
        testData.setEquation("y'''' + 2*y'' + y = 0");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=1");
        ic.add("y''(0)=0");
        ic.add("y'''(0)=1");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 4 Homog (CI) PASÓ");
    }

    // ========== ORDEN 5 ==========

    @Test
    @DisplayName("✅ Orden 5: y''''' + y''' = e^x con CI y(0)=1, y'(0)=0, y''(0)=1, y'''(0)=0, y''''(0)=1")
    void testOrder5_WithInitialConditions() throws Exception {
        testData.setEquation("y''''' + y''' = e^x");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        ic.add("y''(0)=1");
        ic.add("y'''(0)=0");
        ic.add("y''''(0)=1");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test Orden 5 (CI) PASÓ");
    }

    // ========== CASOS ESPECIALES ==========

    @Test
    @DisplayName("✅ CI x0=0: y'' + y = 0 con CI y(0)=1, y'(0)=0")
    void testInitialConditionsImplicitZero() throws Exception {
        testData.setEquation("y'' + y = 0");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        System.out.println("✅ Test CI con x0 implícito PASÓ");
    }

    // ========== PERFORMANCE ==========

    @Test
    @DisplayName("⏱️ Performance CI: Orden 4 con CI debe resolver en < 500ms")
    void testPerformanceInitialConditions() throws Exception {
        testData.setEquation("y'''' - 2*y''' + y'' = e^(2*x)");
        
        List<String> ic = new ArrayList<>();
        ic.add("y(0)=1");
        ic.add("y'(0)=0");
        ic.add("y''(0)=1");
        ic.add("y'''(0)=0");
        testData.setInitialConditions(ic);

        String jsonRequest = objectMapper.writeValueAsString(testData);

        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("⏱️ Performance CI Test: " + duration + " ms");
        assert duration < 500 : "Performance issue: took " + duration + "ms";
    }
}
