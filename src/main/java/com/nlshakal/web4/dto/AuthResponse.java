package com.nlshakal.web4.dto;

import lombok.*;

/**
 * DTO для ответа сервера при аутентификации.
 * Содержит JWT токен при успехе или информацию об ошибке и требованиях CAPTCHA.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    /** JWT токен (присутствует только при успешной аутентификации) */
    private String token;

    /** Email пользователя */
    private String username;

    /** Сообщение от сервера (успех или ошибка) */
    private String message;

    /** Флаг необходимости прохождения CAPTCHA */
    private boolean requiresCaptcha;

    /** Количество неудачных попыток входа */
    private int failedAttempts;
}

