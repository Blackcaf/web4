import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PointRequest, Result } from '../models/result.model';

@Injectable({
  providedIn: 'root'
})
export class ResultService {
  private readonly API_URL = 'http://localhost:8080/web4/api/v1/results';

  constructor(private http: HttpClient) {}

  checkPoint(request: PointRequest): Observable<Result> {
    return this.http.post<Result>(`${this.API_URL}/check`, request);
  }

  getUserResults(): Observable<Result[]> {
    return this.http.get<Result[]>(this.API_URL);
  }

  clearResults(): Observable<any> {
    return this.http.delete(this.API_URL);
  }
}

