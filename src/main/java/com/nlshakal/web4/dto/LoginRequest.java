package com.nlshakal.web4.dto;

import lombok.*;

/**
 * DTO для запроса входа или регистрации пользователя.
 * Используется в эндпоинтах /api/auth/login и /api/auth/register.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    /** Email пользователя */
    private String username;

    /** Пароль в открытом виде (будет хеширован на сервере) */
    private String password;

    /** Опциональный токен Google reCAPTCHA (требуется после 5 неудачных попыток) */
    private String captchaToken;
}

