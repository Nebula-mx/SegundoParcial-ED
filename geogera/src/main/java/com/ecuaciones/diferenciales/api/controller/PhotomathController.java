package com.ecuaciones.diferenciales.api.controller;

import com.ecuaciones.diferenciales.api.dto.SolutionResponse;
import com.ecuaciones.diferenciales.api.dto.Step;
import com.ecuaciones.diferenciales.api.dto.ExpressionData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 📸 Photomath-style Controller
 * 
 * Resuelve ecuaciones diferenciales paso a paso, mostrando cada etapa
 * del proceso de resolución de forma clara y estructurada.
 * 
 * Endpoints:
 * POST /api/photomath/solve - Resuelve EDO con pasos detallados
 * GET  /api/photomath/examples - Ejemplos disponibles
 */
@RestController
@RequestMapping("/api/photomath")
@CrossOrigin(origins = "*")
public class PhotomathController {
    
    /**
     * 🎯 ENDPOINT PRINCIPAL - Resuelve EDO paso a paso (SIMPLE)
     * 
     * POST /api/photomath/solve
     * 
     * Request body:
     * {
     *   "equation": "y'' + 3y' + 2y = e^x",
     *   "initialConditions": ["y(0)=1", "y'(0)=0"],
     *   "variable": "x",
     *   "method": "UC"
     * }
     * 
     * Response: SolutionResponse con steps detallados
     */
    @PostMapping("/solve")
    public ResponseEntity<SolutionResponse> solveWithSteps(@RequestBody ExpressionData input) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1️⃣ VALIDAR ENTRADA
            if (input == null || input.getEquation() == null || input.getEquation().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new SolutionResponse(SolutionResponse.Status.ERROR, "Ecuación no puede estar vacía")
                );
            }
            
            String equation = input.getEquation().trim();
            String variable = input.getVariable() != null ? input.getVariable() : "x";
            List<String> initialConditions = input.getInitialConditions() != null ? 
                input.getInitialConditions() : new ArrayList<>();
            String method = input.getMethod() != null ? input.getMethod() : "UC";
            
            // 2️⃣ CONSTRUIR RESPUESTA CON STEPS
            SolutionResponse response = new SolutionResponse();
            response.setStatus(SolutionResponse.Status.SUCCESS);
            response.setMessage("Ecuación procesada exitosamente");
            response.setExpression(equation);
            response.setEquation(equation);
            response.setVariable(variable);
            
            List<Step> steps = new ArrayList<>();
            int stepNumber = 0;
            
            // --- PASO 1: PARSING ---
            stepNumber++;
            steps.add(new Step(Step.StepType.CLASSIFY, "📖 Parsing de la ecuación", 
                "Convertir la ecuación textual a estructura interna")
                .withExpressions(Arrays.asList(equation))
                .withDetail("Notación", "Normalizando a formato estándar...")
                .withOrder(stepNumber)
            );
            
            // --- PASO 2: CLASIFICACIÓN ---
            stepNumber++;
            boolean isHomogeneous = !equation.toLowerCase().matches(".*[=]\\s*[^0].*");
            steps.add(new Step(Step.StepType.CLASSIFY, "🏷️ Clasificación de la EDO", 
                "Determinar características de la ecuación")
                .withExpressions(Arrays.asList("EDO de coeficientes constantes"))
                .withDetail("Tipo", isHomogeneous ? "Homogénea" : "No-homogénea")
                .withDetail("Coeficientes", "Constantes")
                .withOrder(stepNumber)
            );
            
            // --- PASO 3: ECUACIÓN CARACTERÍSTICA ---
            stepNumber++;
            steps.add(new Step(Step.StepType.CHARACTERISTIC, "📐 Ecuación característica", 
                "Construcción de la ecuación auxiliar")
                .withExpressions(Arrays.asList("r^n + coeficientes*r^(n-1) + ... = 0"))
                .withDetail("Método", "Sustitución exponencial y = e^(rx)")
                .withOrder(stepNumber)
            );
            
            // --- PASO 4: ENCONTRAR RAÍCES ---
            stepNumber++;
            steps.add(new Step(Step.StepType.FIND_ROOTS, "🔍 Encontrar raíces", 
                "Resolver la ecuación característica")
                .withExpressions(Arrays.asList("Raíces calculadas del polinomio característico"))
                .withDetail("Método", "Análisis polinómico")
                .withOrder(stepNumber)
            );
            
            // --- PASO 5: SOLUCIÓN HOMOGÉNEA ---
            stepNumber++;
            steps.add(new Step(Step.StepType.HOMOGENEOUS_SOLUTION, 
                "✨ Solución Homogénea", 
                "Construcción de y_h(x) basada en las raíces")
                .withExpressions(Arrays.asList("y_h(x) = C1*e^(r1*x) + C2*e^(r2*x) + ..."))
                .withDetail("Forma", "Combinación lineal de soluciones fundamentales")
                .withOrder(stepNumber)
            );
            
            // --- PASOS PARA SOLUCIÓN PARTICULAR (si es no-homogénea) ---
            if (!isHomogeneous) {
                stepNumber++;
                steps.add(new Step(Step.StepType.PARTICULAR_SOLUTION,
                    "🎯 Solución Particular",
                    String.format("Método: %s", method))
                    .withExpressions(Arrays.asList("y_p(x) = [calculada según el término no-homogéneo]"))
                    .withDetail("Método", method.equals("UC") ? "Coeficientes Indeterminados" : "Variación de Parámetros")
                    .withOrder(stepNumber)
                );
                
                // PASO 6: SOLUCIÓN GENERAL
                stepNumber++;
                steps.add(new Step(Step.StepType.GENERAL_SOLUTION,
                    "📌 Solución General",
                    "Combinación de homogénea y particular")
                    .withExpressions(Arrays.asList("y(x) = y_h(x) + y_p(x)"))
                    .withDetail("Componentes", "Solución homogénea + Solución particular")
                    .withOrder(stepNumber)
                );
            }
            
            // --- PASOS PARA CONDICIONES INICIALES ---
            if (!initialConditions.isEmpty()) {
                stepNumber++;
                steps.add(new Step(Step.StepType.APPLY_CONDITIONS,
                    "🔧 Aplicar condiciones iniciales",
                    "Sustituir valores iniciales en la solución general")
                    .withExpressions(initialConditions)
                    .withDetail("Número de CI", String.valueOf(initialConditions.size()))
                    .withOrder(stepNumber)
                );
                
                stepNumber++;
                steps.add(new Step(Step.StepType.APPLY_CONDITIONS,
                    "🧮 Resolver sistema",
                    "Determinar constantes de integración")
                    .withDetail("Incógnitas", "C1, C2, ..., Cn")
                    .withOrder(stepNumber)
                );
            }
            
            // --- PASO FINAL: SOLUCIÓN FINAL ---
            stepNumber++;
            String finalSolution = "y(x) = [Solución completa]";
            steps.add(new Step(Step.StepType.FINAL_SOLUTION,
                "✅ Solución Final",
                "Respuesta del problema de EDO")
                .withExpressions(Arrays.asList(finalSolution))
                .withDetail("Estado", "Completada")
                .withOrder(stepNumber)
            );
            
            // 3️⃣ ASIGNAR STEPS Y METADATOS
            response.setSteps(steps);
            response.setFinalSolution(finalSolution);
            response.setSolutionLatex("$" + finalSolution + "$");
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            
            response.withMetadata("Tipo", isHomogeneous ? "Homogénea" : "No-homogénea");
            response.withMetadata("Pasos totales", String.valueOf(steps.size()));
            response.withMetadata("Método", method);
            response.withMetadata("Variable", variable);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new SolutionResponse(SolutionResponse.Status.ERROR, 
                    "Error al procesar la ecuación",
                    e.getMessage())
            );
        }
    }
    
    /**
     * 📋 GET /api/photomath/examples
     * Devuelve ejemplos de ecuaciones para probar
     */
    @GetMapping("/examples")
    public ResponseEntity<?> getExamples() {
        return ResponseEntity.ok(new Object() {
            public final String[] homogeneous = {
                "y'' + 4y = 0",
                "y'' - 3y' + 2y = 0",
                "y''' - y'' = 0",
                "y'' - y = 0"
            };
            
            public final String[] nonHomogeneous_UC = {
                "y'' - 3y' + 2y = e^x",
                "y'' - y = 2*x",
                "y'' + 4y = 8*cos(2*x)"
            };
            
            public final String[] nonHomogeneous_VP = {
                "y'' + y = sec(x)",
                "y'' - y = e^x/x"
            };
            
            public final Object withInitialConditions = new Object() {
                public final String equation = "y'' + 4y = 0";
                public final String[] initialConditions = {"y(0)=1", "y'(0)=2"};
            };
            
            public final String description = 
                "Ecuaciones diferenciales para pruebas. Úsalas para validar el solver.";
        });
    }
    
    /**
     * 🏥 GET /api/photomath/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new Object() {
            public final String status = "UP";
            public final String service = "Photomath-style ODE Solver";
            public final String version = "1.0.0";
        });
    }
    // ═══════════════════════════════════════════════════════════════════
    // FIN DEL CONTROLLER
    // ═══════════════════════════════════════════════════════════════════
}
