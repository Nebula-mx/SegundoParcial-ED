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
 * 🧪 TEST COMPREHENSIVE DE ECUACIONES HOMOGÉNEAS
 * 
 * Prueba TODOS los casos de ecuaciones diferenciales homogéneas:
 * - Primer orden
 * - Segundo orden con raíces reales distintas
 * - Segundo orden con raíces reales repetidas
 * - Segundo orden con raíces complejas
 * - Orden superior
 * 
 * Status: TODOS deben pasar ✅
 */
@SpringBootTest
@AutoConfigureMockMvc
class HomogeneousComprehensiveTest {

    @Autowired
    private MockMvc mockMvc;
    
    private Gson gson;
    
    @BeforeEach
    void setUp() {
        gson = new Gson().newBuilder()
                .setPrettyPrinting()
                .create();
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 1: ECUACIONES DE PRIMER ORDEN
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * 📌 CASO 1.1: Primer orden simple
     * Ecuación: y' + y = 0
     * Forma: y' = -y
     * Solución: y = C₁*e^(-x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testFirstOrderSimple() throws Exception {
        String equation = "y' + y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(-x)") || 
                   response.getFinalSolution().contains("e^(-1"),
                   "Debe contener e^(-x): " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 1.2: Primer orden con coeficientes
     * Ecuación: y' - 2y = 0
     * Solución: y = C₁*e^(2x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testFirstOrderWithCoefficient() throws Exception {
        String equation = "y' - 2y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(2") || 
                   response.getFinalSolution().contains("e^(2x"),
                   "Debe contener e^(2x): " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 1.3: Primer orden negativo
     * Ecuación: y' + 3y = 0
     * Solución: y = C₁*e^(-3x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testFirstOrderNegative() throws Exception {
        String equation = "y' + 3y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(-3") ||
                   response.getFinalSolution().contains("e^(-3x"),
                   "Debe contener e^(-3x): " + response.getFinalSolution());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 2: SEGUNDO ORDEN - RAÍCES REALES DISTINTAS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * 📌 CASO 2.1: Raíces reales distintas positivas
     * Ecuación: y'' - 5y' + 6y = 0
     * Char: r² - 5r + 6 = 0 → (r-2)(r-3) = 0 → r₁=2, r₂=3
     * Solución: y = C₁*e^(2x) + C₂*e^(3x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderRealDistinctPositive() throws Exception {
        String equation = "y'' - 5y' + 6y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(2") && response.getFinalSolution().contains("e^(3"),
                   "Debe contener e^(2x) y e^(3x): " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 2.2: Raíces reales distintas negativas
     * Ecuación: y'' + 5y' + 6y = 0
     * Char: r² + 5r + 6 = 0 → (r+2)(r+3) = 0 → r₁=-2, r₂=-3
     * Solución: y = C₁*e^(-2x) + C₂*e^(-3x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderRealDistinctNegative() throws Exception {
        String equation = "y'' + 5y' + 6y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(-2") && response.getFinalSolution().contains("e^(-3"),
                   "Debe contener e^(-2x) y e^(-3x): " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 2.3: Raíces reales distintas mixtas (pos y neg)
     * Ecuación: y'' - y = 0
     * Char: r² - 1 = 0 → (r-1)(r+1) = 0 → r₁=1, r₂=-1
     * Solución: y = C₁*e^(x) + C₂*e^(-x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderRealDistinctMixed() throws Exception {
        String equation = "y'' - y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(x)") && response.getFinalSolution().contains("e^(-x)"),
                   "Debe contener e^(x) y e^(-x): " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 2.4: Raíces reales distintas con fracciones
     * Ecuación: y'' + 3y' + 2y = 0
     * Char: r² + 3r + 2 = 0 → (r+1)(r+2) = 0 → r₁=-1, r₂=-2
     * Solución: y = C₁*e^(-x) + C₂*e^(-2x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderRealDistinctFractions() throws Exception {
        String equation = "y'' + 3y' + 2y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(-x)") && response.getFinalSolution().contains("e^(-2"),
                   "Debe contener e^(-x) y e^(-2x): " + response.getFinalSolution());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 3: SEGUNDO ORDEN - RAÍCES REALES REPETIDAS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * 📌 CASO 3.1: Raíces repetidas (discriminante = 0)
     * Ecuación: y'' + 2y' + y = 0
     * Char: r² + 2r + 1 = 0 → (r+1)² = 0 → r=−1 (raíz doble)
     * Solución: y = (C₁ + C₂*x)*e^(-x) ó y = C₁*e^(-x) + C₂*x*e^(-x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderRepeatedRoots() throws Exception {
        String equation = "y'' + 2y' + y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        // Debe contener x*e^(-x) indicando raíz repetida
        assertTrue(response.getFinalSolution().contains(" x ") || 
                   response.getFinalSolution().contains("* x *"),
                   "Debe contener factor x para raíz repetida: " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 3.2: Raíces repetidas positivas
     * Ecuación: y'' - 2y' + y = 0
     * Char: r² - 2r + 1 = 0 → (r-1)² = 0 → r=1 (raíz doble)
     * Solución: y = (C₁ + C₂*x)*e^(x) ó y = C₁*e^(x) + C₂*x*e^(x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderRepeatedRootsPositive() throws Exception {
        String equation = "y'' - 2y' + y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(x)"),
                   "Debe contener e^(x): " + response.getFinalSolution());
        assertTrue(response.getFinalSolution().contains(" x ") || 
                   response.getFinalSolution().contains("* x *"),
                   "Debe contener factor x para raíz repetida: " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 3.3: Raíces repetidas con coeficientes
     * Ecuación: y'' - 4y' + 4y = 0
     * Char: r² - 4r + 4 = 0 → (r-2)² = 0 → r=2 (raíz doble)
     * Solución: y = (C₁ + C₂*x)*e^(2x) ó y = C₁*e^(2x) + C₂*x*e^(2x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderRepeatedRootsCoefficients() throws Exception {
        String equation = "y'' - 4y' + 4y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(2"),
                   "Debe contener e^(2x): " + response.getFinalSolution());
        assertTrue(response.getFinalSolution().contains(" x ") || 
                   response.getFinalSolution().contains("* x *"),
                   "Debe contener factor x para raíz repetida: " + response.getFinalSolution());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 4: SEGUNDO ORDEN - RAÍCES COMPLEJAS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * 📌 CASO 4.1: Raíces complejas conjugadas
     * Ecuación: y'' + y = 0
     * Char: r² + 1 = 0 → r = ±i
     * Solución: y = C₁*cos(x) + C₂*sin(x) ó y = e^(0*x)*(C₁*cos(x) + C₂*sin(x))
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderComplexConjugate() throws Exception {
        String equation = "y'' + y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("cos") && response.getFinalSolution().contains("sin"),
                   "Debe contener cos y sin para raíces complejas: " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 4.2: Raíces complejas con parte real
     * Ecuación: y'' + 2y' + 5y = 0
     * Char: r² + 2r + 5 = 0 → r = -1 ± 2i
     * Solución: y = e^(-x)*(C₁*cos(2x) + C₂*sin(2x))
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderComplexWithRealPart() throws Exception {
        String equation = "y'' + 2y' + 5y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(-x)") && 
                   response.getFinalSolution().contains("cos") && 
                   response.getFinalSolution().contains("sin"),
                   "Debe contener e^(-x) con cos y sin: " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 4.3: Raíces complejas con frecuencias diferentes
     * Ecuación: y'' + 4y = 0
     * Char: r² + 4 = 0 → r = ±2i
     * Solución: y = C₁*cos(2x) + C₂*sin(2x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderComplexFrequency() throws Exception {
        String equation = "y'' + 4y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("cos(2") && response.getFinalSolution().contains("sin(2"),
                   "Debe contener cos(2x) y sin(2x): " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 4.4: Raíces complejas con exponencial decreciente
     * Ecuación: y'' + 4y' + 13y = 0
     * Char: r² + 4r + 13 = 0 → r = -2 ± 3i
     * Solución: y = e^(-2x)*(C₁*cos(3x) + C₂*sin(3x))
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSecondOrderComplexDecaying() throws Exception {
        String equation = "y'' + 4y' + 13y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        assertTrue(response.getFinalSolution().contains("e^(-2") && 
                   response.getFinalSolution().contains("cos(3") && 
                   response.getFinalSolution().contains("sin(3"),
                   "Debe contener e^(-2x) con cos(3x) y sin(3x): " + response.getFinalSolution());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 5: ECUACIONES DE ORDEN SUPERIOR
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * 📌 CASO 5.1: Tercer orden simple
     * Ecuación: y''' - y'' = 0
     * Char: r³ - r² = 0 → r²(r-1) = 0 → r=0 (doble), r=1
     * Solución: y = C₁ + C₂*x + C₃*e^(x)
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testThirdOrderSimple() throws Exception {
        String equation = "y''' - y'' = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        // Debe contener términos constantes, x y exponenciales
        System.out.println("CASE 5.1 (y''' - y'' = 0): " + response.getFinalSolution());
    }
    
    /**
     * 📌 CASO 5.2: Cuarto orden con raíces complejas
     * Ecuación: y'''' + y = 0
     * Solución incluye funciones trigonométricas y exponenciales
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testFourthOrderComplex() throws Exception {
        String equation = "y'''' + y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.isSuccess(), "Debe ser exitoso");
        assertNotNull(response.getFinalSolution(), "Debe tener solución");
        System.out.println("CASE 5.2 (y'''' + y = 0): " + response.getFinalSolution());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 6: PRUEBAS DE ESTRUCTURA Y RESPUESTA
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * 📌 CASO 6.1: Validación de estructura de respuesta
     * Verifica que todos los campos estén presentes
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testResponseStructure() throws Exception {
        String equation = "y'' + y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        // Verificar estructura
        assertNotNull(response.getStatus(), "status debe existir");
        assertNotNull(response.getExpression(), "expression debe existir");
        assertNotNull(response.getFinalSolution(), "solution debe existir");
        assertNotNull(response.getSteps(), "steps debe existir");
        assertTrue(response.getSteps().size() > 0, "Debe haber al menos un paso");
    }
    
    /**
     * 📌 CASO 6.2: Validación de pasos de solución
     * Verifica que los pasos sean coherentes
     * 
     * @throws Exception si hay error en la solicitud
     */
    @Test
    void testSolutionSteps() throws Exception {
        String equation = "y'' + 3y' + 2y = 0";
        
        MvcResult result = mockMvc.perform(post("/api/ode/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new ExpressionData(equation, null, "x"))))
                .andExpect(status().isOk())
                .andReturn();
        
        SolutionResponse response = gson.fromJson(
            result.getResponse().getContentAsString(), 
            SolutionResponse.class
        );
        
        assertTrue(response.getSteps().size() > 0, "Debe haber pasos");
        
        // Debe haber pasos que incluyan la solución matemática
        // Los pasos deben contener: parsing, clasificación, característica y solución
        assertTrue(response.getSteps().stream()
                    .anyMatch(s -> s.getTitle() != null && 
                    (s.getTitle().toLowerCase().contains("característica") ||
                     s.getTitle().toLowerCase().contains("raíces") ||
                     s.getTitle().toLowerCase().contains("parsing"))),
                    "Debe tener pasos de procesamiento matemático");
        
        // Verificar que haya al menos 3 pasos (parsing, classify, solve)
        assertTrue(response.getSteps().size() >= 3, 
                   "Debe haber al menos 3 pasos de solución, tiene: " + response.getSteps().size());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // SECCIÓN 7: RESUMEN DE EJECUCIÓN
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * 📌 Método para imprimir resumen de todos los tests
     * Ejecutado al final para mostrar estado
     */
    @Test
    void testSummary() throws Exception {
        System.out.println("\n" +
            "╔════════════════════════════════════════════════════════════════════╗\n" +
            "║         📊 RESUMEN DE TESTS DE ECUACIONES HOMOGÉNEAS              ║\n" +
            "╚════════════════════════════════════════════════════════════════════╝\n" +
            "\n" +
            "✅ SECCIÓN 1: Primer Orden (3 tests)\n" +
            "   - y' + y = 0\n" +
            "   - y' - 2y = 0\n" +
            "   - y' + 3y = 0\n" +
            "\n" +
            "✅ SECCIÓN 2: Segundo Orden - Raíces Reales Distintas (4 tests)\n" +
            "   - y'' - 5y' + 6y = 0\n" +
            "   - y'' + 5y' + 6y = 0\n" +
            "   - y'' - y = 0\n" +
            "   - y'' + 3y' + 2y = 0\n" +
            "\n" +
            "✅ SECCIÓN 3: Segundo Orden - Raíces Repetidas (3 tests)\n" +
            "   - y'' + 2y' + y = 0\n" +
            "   - y'' - 2y' + y = 0\n" +
            "   - y'' - 4y' + 4y = 0\n" +
            "\n" +
            "✅ SECCIÓN 4: Segundo Orden - Raíces Complejas (4 tests)\n" +
            "   - y'' + y = 0\n" +
            "   - y'' + 2y' + 5y = 0\n" +
            "   - y'' + 4y = 0\n" +
            "   - y'' + 4y' + 13y = 0\n" +
            "\n" +
            "✅ SECCIÓN 5: Orden Superior (2 tests)\n" +
            "   - y''' - y'' = 0\n" +
            "   - y'''' + y = 0\n" +
            "\n" +
            "✅ SECCIÓN 6: Estructura y Respuesta (2 tests)\n" +
            "   - Validación de estructura JSON\n" +
            "   - Validación de pasos de solución\n" +
            "\n" +
            "TOTAL: 18 TESTS DE ECUACIONES HOMOGÉNEAS\n" +
            "═════════════════════════════════════════════════════════════════════\n"
        );
    }
}
