import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Company } from '../models/Company';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private baseUrl = 'http://localhost:8080/api/v1/company/sys';
  constructor(private http: HttpClient) { }

  addCompany(company: Company): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}`, company, { withCredentials: true, responseType: 'text' as 'json' });
  }

  getAllCompany(): Observable<Company[]> {
    return this.http.get<Company[]>(`http://localhost:8080/api/v1/company/sys`,{ withCredentials: true});
  }
}
