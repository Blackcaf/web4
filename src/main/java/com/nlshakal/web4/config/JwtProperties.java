package com.nlshakal.web4.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настроек JWT токенов.
 * Автоматически загружает свойства с префиксом "jwt" из application.properties или переменных окружения.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    /** Секретный ключ для подписи JWT (минимум 256 бит для HS256) */
    private String secret;

    /** Время жизни токена в миллисекундах (по умолчанию 24 часа = 86400000) */
    private long expiration;
}

