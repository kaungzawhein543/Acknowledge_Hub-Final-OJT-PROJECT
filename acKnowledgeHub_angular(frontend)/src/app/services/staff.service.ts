import { Injectable } from '@angular/core';
import { NotedUser } from '../models/noted-user';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Company } from '../models/Company';
import { Department } from '../models/Department';
import { StaffGroup } from '../models/staff-group';
import { UnNotedUser } from '../models/un-noted-user';
import { Staff } from '../models/staff';
import { AddStaff } from '../models/addStaff';




@Injectable({
  providedIn: 'root'
})
export class StaffService {

  private baseURL = "http://localhost:8080/api/v1/staff";

  constructor(private http: HttpClient) { }

  addStaff(staff: AddStaff): Observable<any> {
    return this.http.post(`${this.baseURL}/add`, staff);
  }

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


  getStaffs(page: number, size: number, searchTerm: string): Observable<any> {
    const apiUrl = `${this.baseURL}?page=${page}&size=${size}&searchTerm=${searchTerm}`;
    return this.http.get<any>(apiUrl);
  }


  getStaffList(): Observable<StaffGroup[]> {
    return this.http.get<StaffGroup[]>(`${this.baseURL}/group-staff`);
  }
}