package com.nlshakal.web4.service;

import com.nlshakal.web4.constants.SecurityConstants;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для отслеживания неудачных попыток входа.
 * Реализует защиту от брутфорс атак с требованием CAPTCHA после нескольких неудач.
 * Использует in-memory хранилище (ConcurrentHashMap) для подсчета попыток.
 */
@Service
public class LoginAttemptService {

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    /**
     * Регистрирует неудачную попытку входа для пользователя.
     *
     * @param username email пользователя
     */
    public void recordFailedAttempt(String username) {
        LoginAttempt attempt = attempts.computeIfAbsent(username, k -> new LoginAttempt());
        attempt.incrementFailedAttempts();
    }

    /**
     * Сбрасывает счетчик неудачных попыток после успешного входа.
     *
     * @param username email пользователя
     */
    public void resetAttempts(String username) {
        attempts.remove(username);
    }

    /**
     * Проверяет, требуется ли CAPTCHA для пользователя.
     * CAPTCHA требуется после MAX_LOGIN_ATTEMPTS неудачных попыток.
     *
     * @param username email пользователя
     * @return true если требуется CAPTCHA
     */
    public boolean requiresCaptcha(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) {
            return false;
        }
        return attempt.getFailedAttempts() >= SecurityConstants.MAX_LOGIN_ATTEMPTS;
    }

    /**
     * Возвращает количество неудачных попыток входа.
     *
     * @param username email пользователя
     * @return количество неудачных попыток
     */
    public int getFailedAttempts(String username) {
        LoginAttempt attempt = attempts.get(username);
        return attempt != null ? attempt.getFailedAttempts() : 0;
    }

    private static class LoginAttempt {
        private int failedAttempts = 0;

        public void incrementFailedAttempts() {
            this.failedAttempts++;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }
    }
}

