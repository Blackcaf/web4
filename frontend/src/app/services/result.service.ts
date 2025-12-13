import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PointRequest, Result } from '../models/result.model';

@Injectable({
  providedIn: 'root'
})
export class ResultService {
  private readonly API_URL = 'http://localhost:8080/web4/api/results';

  constructor(private http: HttpClient) {}

  /**
   * Проверяет попадание точки в заданные области.
   *
   * @param request - Координаты точки (x, y, r)
   * @returns Observable с результатом проверки
   */
  checkPoint(request: PointRequest): Observable<Result> {
    return this.http.post<Result>(`${this.API_URL}/check`, request);
  }

  /**
   * Получает историю всех проверок текущего пользователя.
   *
   * @returns Observable со списком всех результатов
   */
  getUserResults(): Observable<Result[]> {
    return this.http.get<Result[]>(this.API_URL);
  }

  /**
   * Удаляет все результаты проверок текущего пользователя.
   *
   * @returns Observable для завершения операции удаления
   */
  clearResults(): Observable<any> {
    return this.http.delete(this.API_URL);
  }
}

