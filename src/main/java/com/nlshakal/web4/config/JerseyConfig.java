package com.nlshakal.web4.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация JAX-RS (Jersey) для REST API.
 * Определяет базовый путь /api и сканирует пакет resource для регистрации эндпоинтов.
 */
@Configuration
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    /**
     * Конструктор автоматически регистрирует все классы в пакете com.nlshakal.web4.resource
     * как JAX-RS ресурсы (эндпоинты).
     */
    public JerseyConfig() {
        packages("com.nlshakal.web4.resource");
    }
}

