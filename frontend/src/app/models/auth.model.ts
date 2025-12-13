/**
 * Модель запроса для входа и регистрации пользователя.
 */
export interface LoginRequest {
  username: string;
  password: string;
  captchaToken?: string;
}

/**
 * Модель ответа сервера при аутентификации.
 * Содержит JWT токен при успехе или информацию об ошибке.
 */
export interface AuthResponse {
  token?: string;
  username?: string;
  message?: string;
  requiresCaptcha?: boolean;
  failedAttempts?: number;
}


