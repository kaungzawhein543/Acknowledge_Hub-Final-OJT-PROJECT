import { Injectable } from '@angular/core';
import { NotedUser } from '../models/noted-user';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// import { UnNotedUser } from '../models/not-noted-user';
import { Company } from '../models/Company';
import { Department } from '../models/Department';
import { Staff } from '../models/user.model';
import { StaffGroup } from '../models/staff-group';
import { UnNotedUser } from '../models/un-noted-user';

@Injectable({
  providedIn: 'root'
})
export class StaffService {

  private baseURL = "http://localhost:8080/api/v1/staff";

  constructor(private http: HttpClient) { }

  getNotedUserByAnnouncementList(id: number): Observable<NotedUser[]> {
    return this.http.get<NotedUser[]>(`${this.baseURL}/noted-list/${id}`);
  }

  getUnNotedStaffByAnnouncementList(id: number): Observable<UnNotedUser[]> {
    return this.http.get<UnNotedUser[]>(`${this.baseURL}/not-noted-list/${id}`);
  }

  getAllCompany(): Observable<Company[]> {
    return this.http.get<Company[]>(`http://localhost:8080/api/v1/company`);
  }

  getDepartmentListByCompanyId(companyId: number): Observable<Department[]> {
    return this.http.get<Department[]>(`http://localhost:8080/api/v1/department/company/${companyId}`);
  }

  getAllDepartment(): Observable<Department[]> {
    return this.http.get<Department[]>(`http://localhost:8080/api/v1/department`);
  }

  getStaffList(): Observable<StaffGroup[]> {
    return this.http.get<StaffGroup[]>(`${this.baseURL}/group-staff`);
  }
}