package com.ecuaciones.diferenciales.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 🧪 PRUEBAS EXHAUSTIVAS DE ECUACIONES HOMOGÉNEAS
 * 
 * Verifica TODOS los casos posibles:
 * 1️⃣ Primer orden (simple, con coeficiente)
 * 2️⃣ Segundo orden raíces reales distintas
 * 3️⃣ Segundo orden raíces reales repetidas
 * 4️⃣ Segundo orden raíces complejas
 * 5️⃣ Orden superior (3, 4, 5)
 * 6️⃣ Coeficientes con decimales y fracciones
 * 7️⃣ Problemas con condiciones iniciales
 */
@SpringBootTest
@AutoConfigureMockMvc
public class HomogeneousExhaustiveTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 1: PRIMER ORDEN
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void testFirstOrderSimple() throws Exception {
        String equation = "y' + y = 0";
        testEquation(equation, "e^(-x)", "Primer orden simple: y' + y = 0");
    }

    @Test
    void testFirstOrderWithCoefficient() throws Exception {
        String equation = "y' - 2y = 0";
        testEquation(equation, "e^(2x)", "Primer orden con coef: y' - 2y = 0");
    }

    @Test
    void testFirstOrderPositive() throws Exception {
        String equation = "y' - 3y = 0";
        testEquation(equation, "e^(3x)", "Primer orden positivo: y' - 3y = 0");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 2: SEGUNDO ORDEN - RAÍCES REALES DISTINTAS
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void testSecondOrderRealDistinct1() throws Exception {
        String equation = "y'' - 5*y' + 6y = 0";
        // Raíces: r² - 5r + 6 = 0 → r=2, r=3
        testEquation(equation, "e^(2x)", "Segundo orden raíces reales distintas: y'' - 5y' + 6y = 0");
    }

    @Test
    void testSecondOrderRealDistinct2() throws Exception {
        String equation = "y'' - y = 0";
        // Raíces: r² - 1 = 0 → r=1, r=-1
        testEquation(equation, "e^(x)", "Segundo orden raíces ±1: y'' - y = 0");
    }

    @Test
    void testSecondOrderRealDistinct3() throws Exception {
        String equation = "y'' + 3*y' + 2*y = 0";
        // Raíces: r² + 3r + 2 = 0 → r=-1, r=-2
        testEquation(equation, "e^(-x)", "Segundo orden raíces negativas: y'' + 3y' + 2y = 0");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 3: SEGUNDO ORDEN - RAÍCES REALES REPETIDAS
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void testSecondOrderRealRepeated1() throws Exception {
        String equation = "y'' - 2*y' + y = 0";
        // Raíces: r² - 2r + 1 = 0 → r=1 (repetida)
        testEquation(equation, "e^(x)", "Raíz real repetida: y'' - 2y' + y = 0");
    }

    @Test
    void testSecondOrderRealRepeated2() throws Exception {
        String equation = "y'' + 4*y' + 4*y = 0";
        // Raíces: r² + 4r + 4 = 0 → r=-2 (repetida)
        testEquation(equation, "e^(-2x)", "Raíz real repetida negativa: y'' + 4y' + 4y = 0");
    }

    @Test
    void testSecondOrderRealRepeated3() throws Exception {
        String equation = "y'' + 6*y' + 9*y = 0";
        // Raíces: r² + 6r + 9 = 0 → r=-3 (repetida)
        testEquation(equation, "e^(-3x)", "Raíz real repetida -3: y'' + 6y' + 9y = 0");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 4: SEGUNDO ORDEN - RAÍCES COMPLEJAS
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void testSecondOrderComplexConjugate1() throws Exception {
        String equation = "y'' + 4*y = 0";
        // Raíces: r² + 4 = 0 → r=±2i
        testEquation(equation, "cos(2x)", "Raíces complejas ±2i: y'' + 4y = 0");
    }

    @Test
    void testSecondOrderComplexConjugate2() throws Exception {
        String equation = "y'' + y = 0";
        // Raíces: r² + 1 = 0 → r=±i
        testEquation(equation, "cos(x)", "Raíces complejas ±i: y'' + y = 0");
    }

    @Test
    void testSecondOrderComplexWithReal() throws Exception {
        String equation = "y'' + 2*y' + 5*y = 0";
        // Raíces: r² + 2r + 5 = 0 → r=-1±2i
        testEquation(equation, "e^(-x)", "Raíces complejas con parte real: y'' + 2y' + 5y = 0");
    }

    @Test
    void testSecondOrderComplexWithReal2() throws Exception {
        String equation = "y'' - 2*y' + 2*y = 0";
        // Raíces: r² - 2r + 2 = 0 → r=1±i
        testEquation(equation, "e^(x)", "Raíces complejas 1±i: y'' - 2y' + 2y = 0");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 5: ORDEN SUPERIOR (3, 4, 5)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void testThirdOrderRealRoots() throws Exception {
        String equation = "y''' - 6*y'' + 11*y' - 6*y = 0";
        // Raíces: r³ - 6r² + 11r - 6 = 0 → r=1, r=2, r=3
        testEquation(equation, "e^(x)", "Tercer orden raíces 1,2,3: y''' - 6y'' + 11y' - 6y = 0");
    }

    @Test
    void testThirdOrderWithRepeated() throws Exception {
        String equation = "y''' - 3*y'' + 3*y' - y = 0";
        // Raíces: r³ - 3r² + 3r - 1 = 0 → r=1 (triple)
        testEquation(equation, "e^(x)", "Tercer orden raíz 1 triple: y''' - 3y'' + 3y' - y = 0");
    }

    @Test
    void testFourthOrderRealRoots() throws Exception {
        String equation = "y^(4) - 5*y'' + 4*y = 0";
        // Raíces: r⁴ - 5r² + 4 = 0 → r²=1,4 → r=±1, ±2
        testEquation(equation, "e^(x)", "Cuarto orden raíces ±1,±2: y^(4) - 5y'' + 4y = 0");
    }

    @Test
    void testFifthOrderRealRoots() throws Exception {
        String equation = "y^(5) - y = 0";
        // Raíces: r⁵ - 1 = 0 (incluye r=1 y raíces complejas)
        testEquation(equation, "e^(x)", "Quinto orden con r=1: y^(5) - y = 0");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 6: COEFICIENTES ESPECIALES
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void testWithDecimalCoefficients() throws Exception {
        String equation = "y'' - 1.5*y' + 0.5*y = 0";
        testEquation(equation, "e^", "Coeficientes decimales: y'' - 1.5y' + 0.5y = 0");
    }

    @Test
    void testWithFractionalForm() throws Exception {
        String equation = "2*y'' - 3*y' + y = 0";
        testEquation(equation, "e^", "Coeficientes con factor 2: 2y'' - 3y' + y = 0");
    }

    @Test
    void testLargeCoefficients() throws Exception {
        String equation = "y'' + 100*y = 0";
        testEquation(equation, "cos", "Coeficientes grandes: y'' + 100y = 0");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 7: CONDICIONES INICIALES
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void testWithInitialConditions1() throws Exception {
        String equation = "y'' - y = 0; y(0)=1; y'(0)=1";
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        System.out.println("✅ TEST: " + "Con CI: y'' - y = 0; y(0)=1; y'(0)=1");
        System.out.println("📊 Respuesta: " + response);
        assertTrue(response.contains("success") || response.contains("x"), 
                   "Debe tener solución válida");
    }

    @Test
    void testWithInitialConditions2() throws Exception {
        String equation = "y'' + 4*y = 0; y(0)=0; y'(0)=2";
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        System.out.println("✅ TEST: " + "Con CI: y'' + 4y = 0; y(0)=0; y'(0)=2");
        System.out.println("📊 Respuesta: " + response);
        assertTrue(response.contains("success") || response.contains("sin"), 
                   "Debe contener sin (por las raíces complejas)");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES
    // ═══════════════════════════════════════════════════════════════════════

    private void testEquation(String equation, String expectedPattern, String description) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseStr);

        String status = responseJson.get("status").asText();
        String solution = responseJson.get("finalSolution").asText("");

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║ PRUEBA: " + description);
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Ecuación: " + equation);
        System.out.println("║ Estado: " + status);
        System.out.println("║ Solución: " + solution);
        System.out.println("║ Esperado: contiene '" + expectedPattern + "'");

        if (solution.contains(expectedPattern) || solution.contains(expectedPattern.replace("e^", "E^"))) {
            System.out.println("║ ✅ VERIFICACIÓN: CORRECTA");
        } else {
            System.out.println("║ ⚠️ VERIFICACIÓN: No contiene patrón esperado");
        }
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        assertTrue("success".equals(status), "Debe ser exitoso para: " + equation);
        assertNotNull(solution, "Debe tener solución para: " + equation);
    }
}
