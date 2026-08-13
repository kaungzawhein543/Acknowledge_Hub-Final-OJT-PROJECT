import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ExcelServiceService {
  private readonly apiUrl = `${environment.apiBaseUrl}/api/v1/excel`;

  constructor(private http: HttpClient) { }

  uploadExcelFile(file: File,override : number): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    if(override === 1){
      return this.http.post(`${this.apiUrl}/allSys/upload?override=1`, formData, { responseType: 'text', withCredentials: true });
    }else{
      return this.http.post(`${this.apiUrl}/allSys/upload?override=0`, formData, { responseType: 'text', withCredentials: true });
    }
  }
}
