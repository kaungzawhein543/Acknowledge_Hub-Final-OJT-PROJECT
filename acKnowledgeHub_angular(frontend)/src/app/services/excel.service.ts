import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExcelServiceService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  uploadExcelFile(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
  
return this.http.post(`${this.apiUrl}/upload`, formData, { responseType: 'text', withCredentials: true });
  }
}
