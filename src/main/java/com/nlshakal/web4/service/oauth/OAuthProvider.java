package com.nlshakal.web4.service.oauth;

/**
 * Интерфейс для OAuth 2.0 провайдеров (Strategy Pattern).
 * Определяет контракт для получения email пользователя через OAuth авторизацию.
 * Каждый провайдер (Google, Yandex) имплементирует этот интерфейс.
 */
public interface OAuthProvider {
    /**
     * Обменивает authorization code на access token и получает email пользователя.
     *
     * @param code authorization code, полученный от OAuth провайдера
     * @return email пользователя
     * @throws RuntimeException если произошла ошибка авторизации
     */
    String getEmail(String code);

    /**
     * Возвращает название провайдера для идентификации.
     *
     * @return название провайдера в нижнем регистре ("google", "yandex")
     */
    String getProviderName();
}

