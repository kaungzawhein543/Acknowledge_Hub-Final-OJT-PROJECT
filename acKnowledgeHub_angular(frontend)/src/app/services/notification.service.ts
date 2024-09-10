import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/notifications';

  constructor(private http: HttpClient)  { }

  
  private getIdFromLocalStorage(): number | null {
    const id = localStorage.getItem('id');
    return id ? parseInt(id, 10) : null;
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

    return this.http.put<void>(`${this.apiUrl}/status`,notificationIds, {  headers,withCredentials: true });
  }
}
