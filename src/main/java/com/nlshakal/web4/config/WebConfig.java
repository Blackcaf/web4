package com.nlshakal.web4.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Дополнительная конфигурация CORS для Spring MVC.
 * Дублирует настройки из SecurityConfig для обеспечения корректной работы CORS
 * как в Spring Security, так и в Spring MVC.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Добавляет глобальные настройки CORS для всех эндпоинтов.
     *
     * @param registry реестр CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

