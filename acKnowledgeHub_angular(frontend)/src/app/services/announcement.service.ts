import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { announcement, AnnouncementStatsDTO, MonthlyCountDTO } from '../models/announcement';
import { staffNotedAnnouncement } from '../models/staff-noted-announcement';
import { announcementList, listAnnouncement, requestAnnouncement } from '../models/announcement-list';
import { announcementVersion } from '../models/announcement-version';
import { updateAnnouncement } from '../models/updateAnnouncement';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private BaseUrl = "http://localhost:8080/api/v1/announcement";

  constructor(private http: HttpClient) { }

  //Create Announcement
  createAnnouncement(formData: FormData, userId: number): Observable<any> {
    return this.http.post(`${this.BaseUrl}/allHR/create?createUserId=${userId}`, formData, {withCredentials: true,responseType: 'text'},);
  }

  //Get Latest Version of Announcement
  getLatestVersionAnnouncement(baseFile: string): Observable<any> {
    return this.http.get(`${this.BaseUrl}/announcement-latest-version/${baseFile}`, { withCredentials: true, responseType: 'text' as 'json' });
  }

  getUrlOfAnnouncement(fileName: string): Observable<any> {
    return this.http.get(`${this.BaseUrl}/all/announcement-get-url?fileName=${fileName}`, { responseType: "text" as "json", withCredentials: true });
  }

  //Edit Announcement
  editAnnouncement(announcement: announcement): Observable<any> {
    return this.http.put<String>(`${this.BaseUrl}/${announcement.id}`, announcement,{ withCredentials: true});
  }

  //Get Announcement
  getAnnouncementById(id: number): Observable<updateAnnouncement> {
    return this.http.get(`${this.BaseUrl}/all/${id}`, {  withCredentials: true,responseType: 'text' }).pipe(
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
    return this.http.get(`${this.BaseUrl}/HRM/latest-version-by-id/${id}`, {  withCredentials: true,responseType: 'text' }).pipe(
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
  getPublishAnnouncements(): Observable<listAnnouncement[]> {
    return this.http.get<listAnnouncement[]>(`${this.BaseUrl}/all/getPublishedAnnouncements`);
  }

  //Delete Announcement
  deleteAnnouncement(id: number): Observable<string> {
    return this.http.delete<string>(`${this.BaseUrl}/${id}`,{ withCredentials: true});
  }


  downloadPdf(publicId: string): Observable<Blob> {
    const headers = new HttpHeaders({ 'Accept': 'application/pdf' });
    return this.http.get(`${this.BaseUrl}/all/download/${publicId}`, { headers, withCredentials: true, responseType: 'blob' });
  }

  userNotedAnnouncement(staffId: number): Observable<staffNotedAnnouncement[]> {
    return this.http.get<staffNotedAnnouncement[]>(`${this.BaseUrl}/STF/staff-noted/${staffId}`,{ withCredentials: true});
  }

  userUnNotedAnnouncement(staffId: number): Observable<announcementList[]> {
    return this.http.get<announcementList[]>(`${this.BaseUrl}/STF/staff-unnoted/${staffId}`,{ withCredentials: true});
  }

  userAnnouncement(staffId: number): Observable<announcementList[]> {
    return this.http.get<announcementList[]>(`${this.BaseUrl}/STF/staff/${staffId}`,{ withCredentials: true});
  }

  pendingAnnouncementBySchedule(): Observable<announcementList[]> {
    return this.http.get<announcementList[]>(`${this.BaseUrl}/sys/pending-list`,{ withCredentials: true})
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
    this.http.get(`${this.BaseUrl}/all/downloadfile`, { params, withCredentials: true, responseType: 'blob' }).subscribe(blob => {
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

  getAnnouncementVersions(id: number): Observable<announcementVersion[]> {
    return this.http.get<announcementVersion[]>(`${this.BaseUrl}/HRM/versions/${id}`,{ withCredentials: true})
  }
  //Announcement stats card
  getAnnouncementStats(): Observable<AnnouncementStatsDTO> {
    return this.http.get<AnnouncementStatsDTO>(`${this.BaseUrl}/sys/stats`, { withCredentials: true });
  }

  getMonthlyAnnouncementCounts(): Observable<MonthlyCountDTO[]> {
    return this.http.get<MonthlyCountDTO[]>(`${this.BaseUrl}/sys/monthly-counts`, { withCredentials: true });
  }
  private getFileName(filePath: string): string {
    // Extract file name from filePath if needed
    return filePath.split('/').pop() || 'downloaded-file';
  }
  getAnnouncementVersion(announcementId: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.BaseUrl}/all/announcement-versions/${announcementId}`,{ withCredentials: true});
  }

  getRequestAnnouncementList(): Observable<requestAnnouncement[]> {
    return this.http.get<requestAnnouncement[]>(`${this.BaseUrl}/HRM/request-list`,{ withCredentials: true});
  }

  getAnnouncementListByStaffRequest(id: number): Observable<listAnnouncement[]> {
    return this.http.get<listAnnouncement[]>(`${this.BaseUrl}/request-list/${id}`);
  }

  approvedRequestAnnouncement(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.BaseUrl}/HRM/approved/${id}`,{ withCredentials: true});
  }

  rejectRequestAnnouncement(id: number): Observable<boolean> {
  return this.http.get<boolean>(`${this.BaseUrl}/HRM/reject/${id}`,{ withCredentials: true});
  }



  cancelPendingAnnouncement(id: number): Observable<any> {
    return this.http.get(`${this.BaseUrl}/cancel/${id}`, { withCredentials: true, responseType: 'text' as 'json' });
  }
}
