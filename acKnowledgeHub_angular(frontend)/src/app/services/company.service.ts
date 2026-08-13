import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Company } from '../models/Company';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private readonly baseUrl = `${environment.apiBaseUrl}/api/v1/company/sys`;
  constructor(private http: HttpClient) { }

  addCompany(company: Company): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}`, company, { withCredentials: true, responseType: 'text' as 'json' });
  }

  getAllCompany(): Observable<Company[]> {
    return this.http.get<Company[]>(this.baseUrl, { withCredentials: true });
  }
  updateCompany(id: number, company: string): Observable<String> {
    return this.http.put<String>(`${this.baseUrl}/${id}`, company, { withCredentials: true, responseType: 'text' as 'json' });
  }
  getCompanyById(id: number): Observable<Company> {
    return this.http.get<Company>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }
}
