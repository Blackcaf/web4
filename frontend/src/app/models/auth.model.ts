/**
 * Модель запроса для входа и регистрации пользователя.
 */
export interface LoginRequest {
  /** Email пользователя */
  username: string;
  /** Пароль в открытом виде */
  password: string;
  /** Опциональный токен Google reCAPTCHA (требуется после 5 неудачных попыток) */
  captchaToken?: string;
}

/**
 * Модель ответа сервера при аутентификации.
 * Содержит JWT токен при успехе или информацию об ошибке.
 */
export interface AuthResponse {
  /** JWT токен (присутствует только при успешной аутентификации) */
  token?: string;
  /** Email пользователя */
  username?: string;
  /** Сообщение от сервера (успех или ошибка) */
  message?: string;

  /** Флаг необходимости прохождения CAPTCHA */
  requiresCaptcha?: boolean;
  /** Количество неудачных попыток входа */
  failedAttempts?: number;
}


