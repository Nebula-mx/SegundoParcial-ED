package com.ecuaciones.diferenciales.api.controller;

import com.ecuaciones.diferenciales.api.dto.ExpressionData;
import com.ecuaciones.diferenciales.api.dto.SolutionResponse;
import com.ecuaciones.diferenciales.api.service.ODESolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🌐 REST Controller - API Photomath estilo
 * 
 * Endpoints:
 * POST /api/ode/solve - Resuelve una EDO y devuelve pasos
 * GET /api/ode/examples - Devuelve ejemplos disponibles
 */
@RestController
@RequestMapping("/api/ode")
@CrossOrigin(origins = "*")
public class ODEController {
    
    private final ODESolver odeSolver;
    
    public ODEController() {
        this.odeSolver = new ODESolver();
    }
    
    /**
     * 🚀 ENDPOINT PRINCIPAL
     * 
     * POST /api/ode/solve
     * 
     * Request body:
     * {
     *   "equation": "y'' + 3y' + 2y = e^x",
     *   "initialConditions": ["y(0)=1", "y'(0)=0"],
     *   "variable": "x"
     * }
     * 
     * Response:
     * {
     *   "status": "success",
     *   "message": "Ecuación resuelta exitosamente",
     *   "equation": "y'' + 3y' + 2y = e^x",
     *   "steps": [
     *     {
     *       "type": "CLASSIFY",
     *       "order": 1,
     *       "title": "Clasificación",
     *       "explanation": "...",
     *       "expressions": ["..."]
     *     },
     *     ...
     *   ],
     *   "finalSolution": "y(x) = ...",
     *   "solutionLatex": "y(x) = ...",
     *   "executionTimeMs": 150
     * }
     */
    @PostMapping("/solve")
    public ResponseEntity<SolutionResponse> solveDifferentialEquation(@RequestBody ExpressionData input) {
        try {
            // Normalizar entrada
            input.normalize();
            
            // Validar entrada detalladamente
            String validationError = input.getValidationError();
            if (validationError != null) {
                SolutionResponse errorResponse = new SolutionResponse(
                    SolutionResponse.Status.ERROR,
                    validationError
                );
                errorResponse.setExpression(input.getEquation());
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Resolver EDO
            SolutionResponse response = odeSolver.solveDifferentialEquation(input);
            response.setExpression(input.getEquation());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            SolutionResponse errorResponse = new SolutionResponse(
                SolutionResponse.Status.ERROR,
                "Error interno del servidor",
                e.getMessage()
            );
            errorResponse.setExpression(input != null ? input.getEquation() : "");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 📋 Endpoint para obtener ejemplos
     * 
     * GET /api/ode/examples
     * 
     * Devuelve una lista de ecuaciones diferenciales de ejemplo
     * que el frontend puede usar para pruebas
     */
    @GetMapping("/examples")
    public ResponseEntity<?> getExamples() {
        return ResponseEntity.ok(new Object() {
            public final String[] examples = {
                "y'' + 4y = 0",
                "y'' - 3y' + 2y = 0",
                "y'' - 3y' + 2y = e^x",
                "y'' + y = sin(x)",
                "y' + 2y = e^(-x)",
                "y''' - y'' = 0",
                "y'' - y = 0"
            };
            
            public final String description = "Ecuaciones diferenciales de ejemplo. Úsalas para probar la API.";
            
            public final Object examples_with_ci = new Object() {
                public final String equation = "y'' + 4y = 0";
                public final String[] initialConditions = {"y(0)=1", "y'(0)=2"};
                public final String description = "Ecuación homogénea de orden 2 con condiciones iniciales";
            };
        });
    }
    
    /**
     * 🧪 Endpoint para health check
     * 
     * GET /api/ode/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new Object() {
            public final String status = "UP";
            public final String version = "1.0.0";
            public final String service = "Differential Equations Solver API";
        });
    }
    
    /**
     * 📚 Endpoint para documentación
     * 
     * GET /api/ode/docs
     */
    @GetMapping("/docs")
    public ResponseEntity<?> getDocs() {
        return ResponseEntity.ok(new Object() {
            public final String title = "API Resolvedor de Ecuaciones Diferenciales";
            public final String version = "1.0.0";
            public final String baseUrl = "/api/ode";
            
            public final Object[] endpoints = {
                new Object() {
                    public final String method = "POST";
                    public final String path = "/solve";
                    public final String description = "Resuelve una ecuación diferencial y devuelve pasos";
                    public final Object requestBody = new Object() {
                        public final String equation = "y'' + 3y' + 2y = e^x (requerido)";
                        public final String[] initialConditions = {"y(0)=1", "y'(0)=0"};
                        public final String variable = "x (default)";
                    };
                    public final String responseStatus = "200 OK";
                },
                new Object() {
                    public final String method = "GET";
                    public final String path = "/examples";
                    public final String description = "Obtiene ejemplos de ecuaciones para probar";
                    public final String responseStatus = "200 OK";
                },
                new Object() {
                    public final String method = "GET";
                    public final String path = "/health";
                    public final String description = "Verifica el estado de la API";
                    public final String responseStatus = "200 OK";
                },
                new Object() {
                    public final String method = "GET";
                    public final String path = "/docs";
                    public final String description = "Obtiene esta documentación";
                    public final String responseStatus = "200 OK";
                }
            };
        });
    }
}
