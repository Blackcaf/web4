/**
 * Конфигурация окружения для env.
 */
export const environment = {
    production: true,
    apiUrl: 'http://localhost:8080/web4/api',
    redirectUri: 'http://localhost:4200/login',
    oauth: {
        google: {
            clientId: '252259460448-q0mjt0v4unodrsci7ooc9p37k95494lq.apps.googleusercontent.com',
            authUrl: 'https://accounts.google.com/o/oauth2/v2/auth'
        },
        yandex: {
            clientId: '994b0a36684348deb00fc473a494823f',
            authUrl: 'https://oauth.yandex.ru/authorize'
        }
    },
    recaptcha: {
        siteKey: '6LfjJCgsAAAAAPY2Pjd2vWVgZZdEo0120DWWiyc1'
    }
};