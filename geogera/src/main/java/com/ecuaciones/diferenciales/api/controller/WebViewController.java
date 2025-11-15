package com.ecuaciones.diferenciales.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Controlador para servir la interfaz web principal
 * 
 * Sirve index.html cuando accedes a rutas que no coinciden con /api
 */
@Controller
public class WebViewController {

    /**
     * Servir index.html en la raíz
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/api/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(
            Map.of(
                "service", "Differential Equations API",
                "status", "UP",
                "timestamp", new java.util.Date()
            )
        );
    }
}
