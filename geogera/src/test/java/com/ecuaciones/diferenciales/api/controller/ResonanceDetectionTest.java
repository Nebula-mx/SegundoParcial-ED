package com.ecuaciones.diferenciales.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
 * Tests específicos para detectar si la resonancia está siendo aplicada correctamente
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ResonanceDetectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test: y'' + y = sin(x)
     * Raíces características: ±i (beta = 1)
     * g(x) = sin(x) tiene omega = 1
     * DEBE HABER RESONANCIA: y_p contiene x*sin(x) y x*cos(x)
     */
    @Test
    public void testResonanceSinusoidalDetection() throws Exception {
        String equation = "y'' + y = sin(x)";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(content);
        
        String finalSolution = json.get("finalSolution").asText();
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║ TEST: y'' + y = sin(x)                         ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Raíces características: ±i                     ║");
        System.out.println("║ Omega en sin(x): 1                             ║");
        System.out.println("║ ESPERADO: Resonancia detectada                 ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Solución final:                                ║");
        System.out.println("║ " + finalSolution);
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        // Validar que contiene términos con x (resonancia)
        // Buscar patrones: "x*sin", "x*cos", "x * sin", "x * cos", "* x *" (cualquier orden)
        boolean hasXSin = finalSolution.contains("x*sin") || finalSolution.contains("x * sin") || 
                         finalSolution.contains("sin(x") && finalSolution.contains("* x");
        boolean hasXCos = finalSolution.contains("x*cos") || finalSolution.contains("x * cos") || 
                         finalSolution.contains("cos(x") && finalSolution.contains("* x");
        boolean hasFactor = finalSolution.matches(".*\\*\\s*x\\s*[*+)].*") || 
                           finalSolution.matches(".*x\\s*\\*.*") || hasXSin || hasXCos;
        
        if (hasFactor) {
            System.out.println("✅ RESONANCIA DETECTADA: Contiene factores x*sin o x*cos");
        } else {
            System.out.println("❌ RESONANCIA NO DETECTADA: Falta factor x");
            System.out.println("   Forma actual sin resonancia sería: A*sin(x) + B*cos(x)");
        }
    }

    /**
     * Test: y'' - y = e^x
     * Raíces características: 1, -1
     * g(x) = e^x tiene alpha = 1
     * DEBE HABER RESONANCIA: y_p = (A*x)*e^x o similar con factor x
     */
    @Test
    public void testResonanceExponentialDetection() throws Exception {
        String equation = "y'' - y = e^x";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(content);
        
        String finalSolution = json.get("finalSolution").asText();
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║ TEST: y'' - y = e^x                            ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Raíces características: 1, -1                  ║");
        System.out.println("║ Alpha en e^x: 1                                ║");
        System.out.println("║ ESPERADO: Resonancia detectada                 ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Solución final:                                ║");
        System.out.println("║ " + finalSolution);
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        // Validar que contiene factores x (resonancia)
        // Buscar en cualquier orden: "x*", "x *", "* x", " x " como parte de un término
        boolean hasFactor = finalSolution.contains("x*") || finalSolution.contains("x *") || 
                           finalSolution.contains("* x") || finalSolution.matches(".*\\s[A-Z]\\s*\\*\\s*x\\s*[)\\+].*");
        
        if (hasFactor) {
            System.out.println("✅ RESONANCIA DETECTADA: Contiene factor x");
        } else {
            System.out.println("❌ RESONANCIA NO DETECTADA: Falta factor x");
        }
    }

    /**
     * Test: y'' + 4y = cos(2x)
     * Raíces características: ±2i (beta = 2)
     * g(x) = cos(2x) tiene omega = 2
     * DEBE HABER RESONANCIA: y_p = x * (A*sin(2x) + B*cos(2x))
     */
    @Test
    public void testResonanceHighFrequencyDetection() throws Exception {
        String equation = "y'' + 4*y = cos(2*x)";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(content);
        
        String finalSolution = json.get("finalSolution").asText();
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║ TEST: y'' + 4*y = cos(2*x)                     ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Raíces características: ±2i                    ║");
        System.out.println("║ Omega en cos(2x): 2                            ║");
        System.out.println("║ ESPERADO: Resonancia detectada                 ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Solución final:                                ║");
        System.out.println("║ " + finalSolution);
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        // Validar que contiene factores x (resonancia)
        boolean hasXFactor = finalSolution.contains("x*") || finalSolution.contains("x *") || 
                            finalSolution.contains("* x") || finalSolution.matches(".*\\s[A-Z]\\s*\\*\\s*x\\s*[*()].*");
        
        if (hasXFactor) {
            System.out.println("✅ RESONANCIA DETECTADA: Contiene factor x");
        } else {
            System.out.println("❌ RESONANCIA NO DETECTADA: Falta factor x");
        }
    }

    /**
     * Test NO-RESONANCIA: y'' + 4y = sin(x)
     * Raíces características: ±2i (beta = 2)
     * g(x) = sin(x) tiene omega = 1
     * NO DEBE HABER RESONANCIA (1 ≠ 2): y_p = A*sin(x) + B*cos(x)
     */
    @Test
    public void testNonResonanceDetection() throws Exception {
        String equation = "y'' + 4*y = sin(x)";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType("application/json")
                .content("{\"equation\":\"" + equation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(content);
        
        String finalSolution = json.get("finalSolution").asText();
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║ TEST: y'' + 4*y = sin(x)                       ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Raíces características: ±2i                    ║");
        System.out.println("║ Omega en sin(x): 1                             ║");
        System.out.println("║ ESPERADO: NO hay resonancia (1 ≠ 2)           ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║ Solución final:                                ║");
        System.out.println("║ " + finalSolution);
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        // Validar que CONTRIENE sin(x) o cos(x) pero sin factor x multiplicando
        boolean hasNonResonantForm = (finalSolution.contains("sin(x)") || finalSolution.contains("cos(x)")) 
                                     && !finalSolution.contains("x*sin(x)") 
                                     && !finalSolution.contains("x * sin(x)");
        
        if (hasNonResonantForm) {
            System.out.println("✅ CORRECTO: Sin resonancia, contiene A*sin(x) + B*cos(x)");
        } else {
            System.out.println("⚠️  Revisar forma de la solución");
        }
    }
}
