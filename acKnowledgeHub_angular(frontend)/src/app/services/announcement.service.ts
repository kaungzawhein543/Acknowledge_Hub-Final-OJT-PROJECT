import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { announcement, AnnouncementListDTO, AnnouncementStatsDTO, MonthlyCountDTO } from '../models/announcement';
import saveAs from 'file-saver';
import { staffNotedAnnouncement } from '../models/staff-noted-announcement';
import { announcementList } from '../models/announcement-list';
import { updateAnnouncement } from '../models/updateAnnouncement';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private BaseUrl = "http://localhost:8080/api/v1/announcement";

  constructor(private http: HttpClient) { }

  //Create Announcement
  createAnnouncement(formData: FormData, userId: number): Observable<any> {
    return this.http.post(`${this.BaseUrl}/create?createUserId=${userId}`, formData,{responseType: 'text' as'json'});
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
  getAnnouncementById(id: number): Observable<updateAnnouncement> {
    return this.http.get(`${this.BaseUrl}/${id}`, { responseType: 'text' }).pipe(
      map(response => {
        try {
          return JSON.parse(response) as updateAnnouncement; // Parse JSON response
        } catch (e) {
          console.error('Error parsing JSON:', e);
          throw new Error('Invalid JSON response');
        }
      }),
      catchError(this.handleError)
    );
  }

   //Get Announcement
   getLatestAnnouncementById(id: number): Observable<updateAnnouncement> {
    return this.http.get(`${this.BaseUrl}/latest-version-by-id/${id}`, { responseType: 'text' }).pipe(
      map(response => {
        try {
          return JSON.parse(response) as updateAnnouncement; // Parse JSON response
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
  downloadFile(filePath: string): void {
    const params = new HttpParams().set('file', filePath);
    this.http.get(`${this.BaseUrl}/downloadfile`, { params, responseType: 'blob' }).subscribe(blob => {
      // Create a new Blob object using the response data of the file
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = this.getFileName(filePath); // Set the file name for the download
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
    }, error => {
      console.error('Download error:', error);
    });
  }

  //Announcement stats card
  getAnnouncementStats(): Observable<AnnouncementStatsDTO> {
    return this.http.get<AnnouncementStatsDTO>(`${this.BaseUrl}/stats`, { withCredentials: true });
  }

  getPublishedAnnouncements(): Observable<AnnouncementListDTO[]> {
    return this.http.get<AnnouncementListDTO[]>(`${this.BaseUrl}/getPublishedAnnouncements`,{ withCredentials: true });
  }

  getMonthlyAnnouncementCounts(): Observable<MonthlyCountDTO[]> {
    return this.http.get<MonthlyCountDTO[]>(`${this.BaseUrl}/monthly-counts`,{ withCredentials: true });
  }
  private getFileName(filePath: string): string {
    // Extract file name from filePath if needed
    return filePath.split('/').pop() || 'downloaded-file';
  }
  getAnnouncementVersion(file : string):Observable<string[]>{
    return this.http.get<string[]>(`${this.BaseUrl}/announcement-versions/${file}`);
  }

}
