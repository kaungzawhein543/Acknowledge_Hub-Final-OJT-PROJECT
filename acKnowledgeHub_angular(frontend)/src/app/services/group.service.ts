import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Company } from '../models/Company';
import { Department } from '../models/Department';
import { Position } from '../models/Position';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private apiUrl = 'http://localhost:8080/api/';

  constructor(private http: HttpClient) { }

  getAllCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(`${this.apiUrl}/companies`);
  }

  getDepartmentsByCompany(companyId: number): Observable<Department[]> {
    return this.http.get<Department[]>(`${this.apiUrl}/departments/${companyId}`);
  }

  getPositionsByDepartment(departmentId: number): Observable<Position[]> {
    return this.http.get<Position[]>(`${this.apiUrl}/positions/${departmentId}`);
  }

  getStaffByPosition(positionId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/staffs/${positionId}`);
  }
}
