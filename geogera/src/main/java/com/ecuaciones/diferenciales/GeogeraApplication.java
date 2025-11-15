package com.ecuaciones.diferenciales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GeogeraApplication {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(GeogeraApplication.class, args);
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}
