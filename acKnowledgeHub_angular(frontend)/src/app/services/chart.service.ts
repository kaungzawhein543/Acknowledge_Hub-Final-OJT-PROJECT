import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AnnouncementStaffCountDTO } from '../models/announcement';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChartService {
  private apiUrl = 'http://localhost:8080/api/v1/announcement'; // Your API endpoint

  constructor(private http: HttpClient) { }

  getAnnouncementStaffCounts(): Observable<AnnouncementStaffCountDTO[]> {
    return this.http.get<AnnouncementStaffCountDTO[]>(`${this.apiUrl}/staff-counts`,{ withCredentials: true });
  }
}