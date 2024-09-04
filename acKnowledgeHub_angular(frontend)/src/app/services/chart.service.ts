import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AnnouncementStaffCountDTO } from '../models/announcement';

@Injectable({
  providedIn: 'root'
})
export class ChartService {
  private apiUrl = 'http://localhost:8080/api/v1/announcement'; 
  private staffUrl = 'http://localhost:8080/api/v1/staff';

  constructor(private http: HttpClient) { }

  getAnnouncementStaffCounts(): Observable<AnnouncementStaffCountDTO[]> {
    return this.http.get<AnnouncementStaffCountDTO[]>(`${this.apiUrl}/staff-counts`,{ withCredentials: true });
  }

  getStaffCountByAnnouncement(): Observable<AnnouncementStaffCountDTO[]> {
    return this.http.get<AnnouncementStaffCountDTO[]>(`${this.staffUrl}/staff-count-by-announcement`, { withCredentials: true });
  }
  
  getMonthlyAnnouncementCount(): Observable<any> {
    return this.http.get<any>(`${this.staffUrl}/announcements/count`,{ withCredentials: true });
  }

  getAdditionalChartData(): Observable<any> {
    return this.http.get<any>(`${this.staffUrl}/notesCountByMonth`, { withCredentials: true });
  }
  
  
}
