import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { LoginRequest, AuthResponse } from '../models/auth.model';
import { CookieService } from '../utils/cookie.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/web4/api/auth';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USERNAME_KEY = 'username';

  private currentUserSubject = new BehaviorSubject<string | null>(this.getUsername());

  constructor(private http: HttpClient) {}

  register(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, request, { withCredentials: true })
      .pipe(tap(response => this.handleAuth(response)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, request, { withCredentials: true })
      .pipe(tap(response => this.handleAuth(response)));
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/logout`, {}, { withCredentials: true })
      .pipe(tap(() => {
        CookieService.delete(this.USERNAME_KEY);
        this.currentUserSubject.next(null);
      }));
  }

  socialLogin(code: string, provider: 'google' | 'yandex'): Observable<AuthResponse> {
      return this.http.post<AuthResponse>(`${this.API_URL}/social`, { code, provider }, { withCredentials: true })
          .pipe(tap(response => this.handleAuth(response)));
  }

  /**
   * Получает имя пользователя из cookies.
   */
  getUsername(): string | null {
    return CookieService.get(this.USERNAME_KEY);
  }

  /**
   * Проверяет, аутентифицирован ли пользователь.
   */
  isAuthenticated(): boolean {
    return CookieService.exists(this.TOKEN_KEY);
  }

  private handleAuth(response: AuthResponse): void {
    if (response.username) {
      this.currentUserSubject.next(response.username);
    } else {
      this.currentUserSubject.next(null);
    }
  }
}

