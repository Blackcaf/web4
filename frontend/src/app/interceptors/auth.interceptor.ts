import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * HTTP интерсептор для добавления credentials к запросам.
 * Автоматически включает cookies в каждый HTTP запрос для передачи JWT токенов.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  /**
   * Перехватывает исходящие HTTP запросы и добавляет withCredentials: true.
   *
   * @param req - Исходный HTTP запрос
   * @param next - Обработчик следующего интерсептора в цепочке
   * @returns Observable с HTTP событием
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const cloned = req.clone({
      withCredentials: true
    });
    return next.handle(cloned);
  }
}

