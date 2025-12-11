export interface LoginRequest {
  username: string;
  password: string;
  captchaToken?: string;
}

export interface AuthResponse {
  token?: string;
  username?: string;
  message?: string;

  requiresCaptcha?: boolean;
  failedAttempts?: number;
}

export interface PasswordStrength {
  score: number;
  message: string;
  color: string;
}

export interface User {
  username: string;
}

