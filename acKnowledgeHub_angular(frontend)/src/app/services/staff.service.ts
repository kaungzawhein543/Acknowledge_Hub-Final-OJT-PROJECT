import { Injectable } from '@angular/core';
import { NotedUser } from '../models/noted-user';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Company } from '../models/Company';
import { Department } from '../models/Department';
import { StaffGroup } from '../models/staff-group';
import { UnNotedUser } from '../models/un-noted-user';
import { Staff } from '../models/staff';

@Injectable({
  providedIn: 'root'
})
export class StaffService {

  private baseURL = "http://localhost:8080/api/v1/staff";

  constructor(private http: HttpClient) { }

  addStaff(staff: Staff): Observable<any> {
    return this.http.post(`${this.baseURL}/add`, staff);
  }

  getNotedUserByAnnouncementList(id: number): Observable<NotedUser[]> {
    return this.http.get<NotedUser[]>(`${this.baseURL}/noted-list/${id}`);
  }

  getUnNotedStaffByAnnouncementList(id: number): Observable<UnNotedUser[]> {
    return this.http.get<UnNotedUser[]>(`${this.baseURL}/not-noted-list/${id}`);
  }

  getStaffList(): Observable<StaffGroup[]> {
    return this.http.get<StaffGroup[]>(`${this.baseURL}/group-staff`);
  }
}