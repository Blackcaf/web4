package com.nlshakal.web4.dto;

import lombok.*;

/**
 * DTO для запроса OAuth 2.0 аутентификации через социальные сети.
 * Используется в эндпоинте /api/auth/social.
 */
@Getter
@Setter
@NoArgsConstructor
public class SocialLoginRequest {
    /** Authorization code, полученный от OAuth провайдера */
    private String code;

    /** Название провайдера: "google" или "yandex" */
    private String provider;
}