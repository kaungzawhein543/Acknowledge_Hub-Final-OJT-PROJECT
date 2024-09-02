import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { announcement } from '../models/announcement';
import saveAs from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private BaseUrl = "http://localhost:8080/api/v1/announcement";

  constructor(private http: HttpClient) { }

  //Create Announcement
  createAnnouncement(announcement: announcement): Observable<any> {
    return this.http.post(`${this.BaseUrl}/create`, announcement, {withCredentials: true});
  }

  //Edit Announcement
  editAnnouncement(announcement: announcement): Observable<any>{
    return this.http.put<String>(`${this.BaseUrl}/${announcement.id}`,announcement, {withCredentials: true});
  }

    
 //Get All
 getAnnouncements(): Observable<announcement[]> {
  return this.http.get<announcement[]>(`${this.BaseUrl}/all`, {withCredentials: true});
}


  //Get Announcement
  getAnnouncementById(id : number): Observable<announcement>{
    return this.http.get<announcement>(`${this.BaseUrl}/${id}`, {withCredentials: true});
  }

  //Get Published Announcement
  getPublishAnnouncements() : Observable<announcement[]>{
    return this.http.get<announcement[]>(`${this.BaseUrl}/getPublishedAnnouncements`, {withCredentials: true});
  }

  //Delete Announcement
  deleteAnnouncement(id : number): Observable<string>{
    return this.http.delete<string>(`${this.BaseUrl}/${id}`, {withCredentials: true});
  }

  //Report 
  getAnnouncementsReport(startDateTime: string, endDateTime: string): Observable<announcement[]> {
    const params = new HttpParams()
      .set('start', startDateTime)
      .set('end', endDateTime);
  
    return this.http.get<announcement[]>(`${this.BaseUrl}/report`, { params , withCredentials: true});
  }


  downloadPdf(publicId: string): Observable<Blob> {
    const headers = new HttpHeaders({ 'Accept': 'application/pdf' });
    return this.http.get(`${this.BaseUrl}/download/${publicId}`, { headers, responseType: 'blob' , withCredentials: true});
  }
}
