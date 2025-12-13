/**
 * Конфигурация окружения для разработки (development).
 * Содержит все настройки для локальной разработки, включая URL API,
 * OAuth провайдеров и reCAPTCHA.
 */
export const environment = {
    /** Флаг production режима (false для разработки) */
    production: false,

    /** Базовый URL бэкенд API */
    apiUrl: 'http://localhost:8080/web4/api',

    /** URI для редиректа после OAuth аутентификации */
    redirectUri: 'http://localhost:4200/login',

    /** Настройки OAuth 2.0 провайдеров */
    oauth: {
        /** Конфигурация Google OAuth */
        google: {
            /** Client ID приложения в Google Cloud Console */
            clientId: '252259460448-q0mjt0v4unodrsci7ooc9p37k95494lq.apps.googleusercontent.com',
            /** URL для авторизации Google */
            authUrl: 'https://accounts.google.com/o/oauth2/v2/auth'
        },
        /** Конфигурация Yandex OAuth */
        yandex: {
            /** Client ID приложения в Yandex OAuth */
            clientId: '994b0a36684348deb00fc473a494823f',
            /** URL для авторизации Yandex */
            authUrl: 'https://oauth.yandex.ru/authorize'
        }
    },
    /** Настройки Google reCAPTCHA v2 */
    recaptcha: {
        /** Site Key для reCAPTCHA (публичный ключ) */
        siteKey: '6LfjJCgsAAAAAPY2Pjd2vWVgZZdEo0120DWWiyc1'
    }
};