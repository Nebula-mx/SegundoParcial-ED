package com.ecuaciones.diferenciales.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test paso-a-paso de ecuaciones críticas con verificación manual de pasos.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class StepByStepVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void testEquation(String equation, String expectedPattern, String description) throws Exception {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST: " + description);
        System.out.println("═".repeat(70));
        System.out.println("Ecuación: " + equation);
        System.out.println("Esperado (patrón): " + expectedPattern);
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(content);
        
        // Manejo seguro de nulls
        String status = json.has("status") && !json.get("status").isNull() ? json.get("status").asText() : "unknown";
        String homogeneous = json.has("homogeneousSolution") && !json.get("homogeneousSolution").isNull() ? json.get("homogeneousSolution").asText() : "N/A";
        String particular = json.has("particularSolution") && !json.get("particularSolution").isNull() ? json.get("particularSolution").asText() : "N/A";
        String final_sol = json.has("finalSolution") && !json.get("finalSolution").isNull() ? json.get("finalSolution").asText() : "N/A";
        
        System.out.println("\nResultados:");
        System.out.println("Status: " + status);
        System.out.println("y_h (Homogénea): " + homogeneous);
        System.out.println("y_p (Particular): " + particular);
        System.out.println("y (Final): " + final_sol);
        
        boolean matches = final_sol.contains(expectedPattern);
        String icon = matches ? "✅" : "❌";
        System.out.println("\n" + icon + " Verificación: " + (matches ? "CORRECTO" : "INCORRECTO"));
        
        if (!matches) {
            System.out.println("   ERROR: No contiene el patrón esperado '" + expectedPattern + "'");
            System.out.println("   Solución obtenida: " + final_sol);
        }
    }

    @Test
    public void test01_HomogeneaOrden1_Real() throws Exception {
        testEquation(
            "y' - 2y = 0",
            "e^(2x)",
            "Homogénea Orden 1 - Raíz real (r=2)"
        );
    }

    @Test
    public void test02_HomogeneaOrden2_RealesDistintas() throws Exception {
        testEquation(
            "y'' - 5y' + 6y = 0",
            "e^(2x)",
            "Homogénea Orden 2 - Raíces reales distintas (r=2,3)"
        );
    }

    @Test
    public void test03_HomogeneaOrden2_Complejas() throws Exception {
        testEquation(
            "y'' + 4y = 0",
            "cos(2x)",
            "Homogénea Orden 2 - Raíces complejas (±2i)"
        );
    }

    @Test
    public void test04_HomogeneaOrden2_Repetidas() throws Exception {
        testEquation(
            "y'' - 6y' + 9y = 0",
            "e^(3x)",
            "Homogénea Orden 2 - Raíces repetidas (r=3 doble)"
        );
    }

    @Test
    public void test05_UCOrden2_Polinomio() throws Exception {
        testEquation(
            "y'' - 4y = 3",
            "e^(2x)",
            "UC Orden 2 - Término forzante: constante (3)"
        );
    }

    @Test
    public void test06_UCOrden2_Exponencial() throws Exception {
        testEquation(
            "y'' - 4y = e^x",
            "e^(2x)",
            "UC Orden 2 - Término forzante: exponencial e^x"
        );
    }

    @Test
    public void test07_UCOrden2_Sinusoidal() throws Exception {
        testEquation(
            "y'' + 4y = 2*sin(x)",
            "sin",
            "UC Orden 2 - Término forzante: sinusoidal sin(x)"
        );
    }

    @Test
    public void test08_ResonanciaSinusoidal() throws Exception {
        testEquation(
            "y'' + y = sin(x)",
            "x*sin",
            "Resonancia Sinusoidal - y'' + y = sin(x) debe tener y_p con factor x"
        );
    }

    @Test
    public void test09_ResonanciaExponencial() throws Exception {
        testEquation(
            "y'' - y = e^x",
            "x*e",
            "Resonancia Exponencial - y'' - y = e^x debe tener y_p con factor x"
        );
    }

    @Test
    public void test10_ResonanciaCoseno() throws Exception {
        testEquation(
            "y'' + 4y = cos(2x)",
            "x*sin",
            "Resonancia Coseno - y'' + 4y = cos(2x) debe tener y_p con factor x"
        );
    }

    @Test
    public void test11_OrdenSuperior3() throws Exception {
        testEquation(
            "y''' - y'' - y' + y = 0",
            "e^x",
            "Homogénea Orden 3 - Raíces múltiples (±1, 1)"
        );
    }

    @Test
    public void test12_OrdenSuperior4() throws Exception {
        testEquation(
            "y'''' - 2y'' + y = 0",
            "cos",
            "Homogénea Orden 4 - Raíces complejas"
        );
    }

    @Test
    public void test13_NoResonancia_Frecuencias() throws Exception {
        testEquation(
            "y'' + 4y = sin(x)",
            "sin(1x)",
            "NO Resonancia - y'' + 4y = sin(x): ω_forzante=1 ≠ ω_característica=2"
        );
    }
}
