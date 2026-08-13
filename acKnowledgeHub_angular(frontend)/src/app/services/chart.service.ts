import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AnnouncementStaffCountDTO } from '../models/announcement';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChartService {
  private readonly apiUrl = `${environment.apiBaseUrl}/api/v1/announcement`;
  private readonly staffUrl = `${environment.apiBaseUrl}/api/v1/staff`;

  constructor(private http: HttpClient) { }

  getAnnouncementStaffCounts(): Observable<AnnouncementStaffCountDTO[]> {
    return this.http.get<AnnouncementStaffCountDTO[]>(`${this.apiUrl}/sys/staff-counts`,{ withCredentials: true });
  }

  getStaffCountByAnnouncement(): Observable<AnnouncementStaffCountDTO[]> {
    return this.http.get<AnnouncementStaffCountDTO[]>(`${this.staffUrl}/sys/staff-count-by-announcement`, { withCredentials: true });
  }
  
  getMonthlyAnnouncementCount(): Observable<any> {
    return this.http.get<any>(`${this.staffUrl}/STF/announcements/count`,{ withCredentials: true });
  }

  getAdditionalChartData(): Observable<any> {
    return this.http.get<any>(`${this.staffUrl}/STF/notesCountByMonth`, { withCredentials: true });
  }
  
  
}
