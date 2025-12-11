import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { LoginRequest, AuthResponse } from '../models/auth.model';
import { environment } from '../../environments/environment';
import { CookieService } from '../utils/cookie.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/web4/api/auth';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USERNAME_KEY = 'username';

  private currentUserSubject = new BehaviorSubject<string | null>(this.getUsername());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  register(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, request)
      .pipe(tap(response => this.handleAuth(response)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, request)
      .pipe(tap(response => this.handleAuth(response)));
  }

  logout(): void {
    CookieService.delete(this.TOKEN_KEY);
    CookieService.delete(this.USERNAME_KEY);
    this.currentUserSubject.next(null);
  }

  socialLogin(code: string, provider: 'google' | 'yandex'): Observable<AuthResponse> {
      return this.http.post<AuthResponse>(`${this.API_URL}/social`, { code, provider })
          .pipe(tap(response => this.handleAuth(response)));
  }

  getToken(): string | null {
    return CookieService.get(this.TOKEN_KEY);
  }

  getUsername(): string | null {
    return CookieService.get(this.USERNAME_KEY);
  }

  isAuthenticated(): boolean {
    return CookieService.exists(this.TOKEN_KEY);
  }

  private handleAuth(response: AuthResponse): void {
    if (response.token) {
      CookieService.set(this.TOKEN_KEY, response.token, 1);
    }
    if (response.username) {
      CookieService.set(this.USERNAME_KEY, response.username, 1);
      this.currentUserSubject.next(response.username);
    } else {
      this.currentUserSubject.next(null);
    }
  }
}

