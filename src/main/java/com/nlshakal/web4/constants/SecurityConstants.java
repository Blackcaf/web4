package com.nlshakal.web4.constants;

/**
 * Константы безопасности для защиты приложения.
 * Определяет лимиты на попытки входа, rate limiting и обнаружение подозрительной активности.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int RATE_LIMIT_MAX_REQUESTS = 10;
    public static final int SUSPICIOUS_ACTIVITY_THRESHOLD = 10;
}

