import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Position } from '../models/Position';

@Injectable({
  providedIn: 'root'
})
export class PositionService {

  private baseUrl = 'http://localhost:8080/api/v1/position';
  constructor(private http: HttpClient) { }

  getAllPosition(): Observable<Position[]> {
    return this.http.get<Position[]>(`${this.baseUrl}/sys/list`, { withCredentials: true });
  }

  addPosition(position: Position): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/sys/addPosition`, position, { withCredentials: true, responseType: 'text' as 'json' });
  }

  getPositionById(id: number): Observable<Position> {
    return this.http.get<Position>(`${this.baseUrl}/all/${id}`);
  }

  updatePosition(id: number, position: Position): Observable<string> {
    return this.http.put<string>(`${this.baseUrl}/all/${id}`, position, { withCredentials: true, responseType: 'text' as 'json' });
  }
}
