import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Department } from '../models/Department';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {

  private baseUrl = 'http://localhost:8080/api/v1/department'
  constructor(private http: HttpClient) { }

  addDepartment(department: Department): Observable<any> {
    return this.http.post(`${this.baseUrl}/sys/createDepartment`, department,{ withCredentials: true});
  }

  getDepartmentListByCompanyId(companyId: number): Observable<Department[]> {
    return this.http.get<Department[]>(`${this.baseUrl}/sys/company/${companyId}`,{ withCredentials: true});
  }

  getAllDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(`${this.baseUrl}/sys/getAllCompany`,{ withCredentials: true});
  }
}
