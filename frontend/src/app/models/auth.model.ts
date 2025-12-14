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


