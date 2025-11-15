package com.ecuaciones.diferenciales.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC para GEOGERA
 * 
 * - CORS habilitado globalmente
 * - Recursos estáticos servidos correctamente
 * - Manejo de rutas
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configurar CORS para permitir peticiones desde cualquier origen
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    /**
     * Configurar recursos estáticos (HTML, CSS, JS, etc)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        "classpath:/public/",
                        "classpath:/resources/",
                        "classpath:/META-INF/resources/",
                        "file:src/main/webapp/"
                )
                .setCachePeriod(3600);
    }
}
