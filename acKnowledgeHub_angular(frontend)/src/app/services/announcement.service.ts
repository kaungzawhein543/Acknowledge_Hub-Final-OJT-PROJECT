import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
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
    return this.http.post(`${this.BaseUrl}/create`, announcement);
  }

  //Edit Announcement
  editAnnouncement(announcement: announcement): Observable<any>{
    return this.http.put<String>(`${this.BaseUrl}/${announcement.id}`,announcement);
  }

  //Get Announcement
  getAnnouncementById(id : number): Observable<announcement>{
    return this.http.get<announcement>(`${this.BaseUrl}/${id}`);
  }

  //Get Published Announcement
  getPublishAnnouncements() : Observable<announcement[]>{
    return this.http.get<announcement[]>(`${this.BaseUrl}/getPublishedAnnouncements`);
  }

  //Delete Announcement
  deleteAnnouncement(id : number): Observable<string>{
    return this.http.delete<string>(`${this.BaseUrl}/${id}`);
  }


  downloadPdf(publicId: string): Observable<Blob> {
    const headers = new HttpHeaders({ 'Accept': 'application/pdf' });
    return this.http.get(`${this.BaseUrl}/download/${publicId}`, { headers, responseType: 'blob' });
  }
}
