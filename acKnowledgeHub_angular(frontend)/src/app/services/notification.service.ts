import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Notification } from '../models/Notification';


@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient)  { }

  
  private getIdFromLocalStorage(): number | null {
    const id = localStorage.getItem('id');
    return id ? parseInt(id, 10) : null;
  }

  updateNotification(staffId : number,notificationId : number): Observable<Notification[]>{
    return this.http.get<Notification[]>(`${this.apiUrl}/api/v1/notifications/check/${staffId}/${notificationId}`)
  }

  toggleNotificationStatus(notificationIds: number[]): Observable<void> {
    const staffId = this.getIdFromLocalStorage();

    if (staffId === null) {
      console.error('Staff ID is missing');
      return new Observable<void>(observer => observer.error('Staff ID is missing'));
    }
    
    const headers = new HttpHeaders({
      'X-Staff-Id': staffId.toString() 
    });

    return this.http.put<void>(`${this.apiUrl}/notifications/status`,notificationIds, {  headers,withCredentials: true });
  }
}
