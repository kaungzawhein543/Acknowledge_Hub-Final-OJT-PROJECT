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
    return this.http.get<Position[]>(`${this.baseUrl}/list`);
  }

  addPosition(position: Position): Observable<any> {
    return this.http.post(`${this.baseUrl}`, position);
  }
}
