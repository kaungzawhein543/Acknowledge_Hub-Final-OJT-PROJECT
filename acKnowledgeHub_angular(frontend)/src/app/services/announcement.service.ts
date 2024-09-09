import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { announcement } from '../models/announcement';
import saveAs from 'file-saver';
import { staffNotedAnnouncement } from '../models/staff-noted-announcement';
import { announcementList } from '../models/announcement-list';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private BaseUrl = "http://localhost:8080/api/v1/announcement";

  constructor(private http: HttpClient) { }

  //Create Announcement
  createAnnouncement(formData: FormData, userId: number): Observable<any> {
    return this.http.post(`${this.BaseUrl}/create?createUserId=${userId}`, formData);
  }

  //Get Latest Version of Announcement
  getLatestVersionAnnouncement(baseFile: string):Observable<any>{
      return this.http.get(`${this.BaseUrl}/announcement-latest-version/${baseFile}`,{withCredentials:true ,responseType: 'text' as 'json'});
  }

  getUrlOfAnnouncement(fileName: string): Observable<any>{
    return this.http.get(`${this.BaseUrl}/announcement-get-url?fileName=${fileName}`,{responseType:"text" as "json", withCredentials:true});
  }

  //Edit Announcement
  editAnnouncement(announcement: announcement): Observable<any> {
    return this.http.put<String>(`${this.BaseUrl}/${announcement.id}`, announcement);
  }

  //Get Announcement
  getAnnouncementById(id: number): Observable<announcement> {
    return this.http.get(`${this.BaseUrl}/${id}`, { responseType: 'text' }).pipe(
      map(response => {
        console.log(`Response length: ${response.length}`); // Log the length of the response
  
        try {
          return JSON.parse(response) as announcement; // Parse JSON response
        } catch (e) {
          console.error('Error parsing JSON:', e);
          throw new Error('Invalid JSON response');
        }
      }),
      catchError(this.handleError)
    );
  }
  
  //Get Published Announcement
  getPublishAnnouncements(): Observable<announcement[]> {
    return this.http.get<announcement[]>(`${this.BaseUrl}/getPublishedAnnouncements`);
  }

  //Delete Announcement
  deleteAnnouncement(id: number): Observable<string> {
    return this.http.delete<string>(`${this.BaseUrl}/${id}`);
  }


  downloadPdf(publicId: string): Observable<Blob> {
    const headers = new HttpHeaders({ 'Accept': 'application/pdf' });
    return this.http.get(`${this.BaseUrl}/download/${publicId}`, { headers, responseType: 'blob' });
  }

  userNotedAnnouncement(staffId: number): Observable<staffNotedAnnouncement[]> {
    return this.http.get<staffNotedAnnouncement[]>(`${this.BaseUrl}/staff-noted/${staffId}`);
  }

  userUnNotedAnnouncement(staffId: number): Observable<announcementList[]> {
    return this.http.get<announcementList[]>(`${this.BaseUrl}/staff-unnoted/${staffId}`);
  }

  userAnnouncement(staffId: number): Observable<announcementList[]> {
    return this.http.get<announcementList[]>(`${this.BaseUrl}/staff/${staffId}`);
  }

  pendingAnnouncementBySchedule(): Observable<announcementList[]> {
    return this.http.get<announcementList[]>(`${this.BaseUrl}/pending-list`)
  }

  //Get All
  getAnnouncements(): Observable<announcement[]> {
    return this.http.get<announcement[]>(`${this.BaseUrl}/all`, { withCredentials: true });
  }

  //Report 
  getAnnouncementsReport(startDateTime: string, endDateTime: string): Observable<announcement[]> {
    const params = new HttpParams()
      .set('start', startDateTime)
      .set('end', endDateTime);

    return this.http.get<announcement[]>(`${this.BaseUrl}/report`, { params, withCredentials: true });
  }
  private handleError(error: HttpErrorResponse) {
    // Handle different error statuses
    if (error.status === 404) {
      console.error('Announcement not found');
    } else {
      console.error('An unexpected error occurred:', error.message);
    }
    return throwError(() => new Error('Something went wrong; please try again later.'));
  }


}
