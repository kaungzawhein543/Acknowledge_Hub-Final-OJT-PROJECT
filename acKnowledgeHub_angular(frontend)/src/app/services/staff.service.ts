import { Injectable } from '@angular/core';
import { NotedUser } from '../models/noted-user';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Company } from '../models/Company';
import { Department } from '../models/Department';
import { StaffGroup } from '../models/staff-group';
import { UnNotedUser } from '../models/un-noted-user';
import { Staff, staffList } from '../models/staff';
import { StaffSummaryCount } from '../models/staff';
import { AddStaff } from '../models/addStaff';
import { announcementList } from '../models/announcement-list';
import { AnnouncementListDTO } from '../models/announcement';
import { ChangePasswordRequest } from '../models/change-password-request.model';
import { text } from 'stream/consumers';




@Injectable({
  providedIn: 'root'
})
export class StaffService {

  private baseURL = "http://localhost:8080/api/v1/staff";

  constructor(private http: HttpClient) { }

  addStaff(staff: AddStaff): Observable<any> {
    return this.http.post(`${this.baseURL}/sys/add`, staff,{ withCredentials: true,responseType: 'text'});
  }

  getNotedUserByAnnouncementList(id: number): Observable<NotedUser[]> {
    return this.http.get<NotedUser[]>(`${this.baseURL}/all/noted-list/${id}`,{ withCredentials: true});
  }

  getUnNotedStaffByAnnouncementList(id: number, groupStatus: number): Observable<UnNotedUser[]> {
    return this.http.get<UnNotedUser[]>(`${this.baseURL}/all/not-noted-list/${id}/${groupStatus}`,{ withCredentials: true});
  }

  // getAllCompany(): Observable<Company[]> {
  //   return this.http.get<Company[]>(`http://localhost:8080/api/v1/company`);
  // }

  // getDepartmentListByCompanyId(companyId: number): Observable<Department[]> {
  //   return this.http.get<Department[]>(`http://localhost:8080/api/v1/department/company/${companyId}`);
  // }

  // getAllDepartment(): Observable<Department[]> {
  //   return this.http.get<Department[]>(`http://localhost:8080/api/v1/department`);
  // }


  makeNotedAnnouncement(userId: number, announcementId: number): Observable<string> {
    return this.http.get<string>(`${this.baseURL}/noted?userId=${userId}&announcementId=${announcementId}`, { withCredentials: true, responseType: 'text' as 'json' });
  }

  checkNotedAnnouncement(userId: number, announcementId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseURL}/check-noted?userId=${userId}&announcementId=${announcementId}`, { withCredentials: true, responseType: 'text' as 'json' });
  }

  getStaffs(page: number, size: number, searchTerm: string): Observable<any> {
    const apiUrl = `${this.baseURL}/allHR/getStaff?page=${page}&size=${size}&searchTerm=${searchTerm}`;
    return this.http.get<any>(apiUrl , { withCredentials: true});
  }

  getStaffList(): Observable<StaffGroup[]> {
    return this.http.get<StaffGroup[]>(`${this.baseURL}/sys/group-staff`,{ withCredentials: true});
  }

  getList(): Observable<staffList[]> {
    return this.http.get<staffList[]>(`${this.baseURL}/sys/list`,{ withCredentials: true});
  }

  getHRList(): Observable<staffList[]> {
    return this.http.get<staffList[]>(`${this.baseURL}/sys/hr-list`,{ withCredentials: true});
  }

  putHRMain(id: number): Observable<staffList[]> {
    return this.http.get<staffList[]>(`${this.baseURL}/sys/put-HR/${id}`,{ withCredentials: true});
  }

  //method to get staff summary count
  getStaffCount(): Observable<StaffSummaryCount> {
    return this.http.get<StaffSummaryCount>(`${this.baseURL}/sys/summary`, { withCredentials: true });
  }

  //method to get announcment by staff id card
  getAnnouncementDESC(): Observable<AnnouncementListDTO[]> {
    return this.http.get<AnnouncementListDTO[]>(`${this.baseURL}/all/staff-announcements`, { withCredentials: true });
  }

  //method to change profile photo
  uploadProfilePhoto(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.baseURL}/all/profile/upload-photo`, formData, { withCredentials: true, responseType: 'text' as 'json' });
  }

  // Method to change old password
  changeOldPassword(request: ChangePasswordRequest): Observable<string> {
    return this.http.post<string>(`${this.baseURL}/all/change_Old_Password`, request, { withCredentials :true , responseType: 'text' as 'json'});
  }
  activateStaff(id: number): Observable<any> {
    return this.http.get(`${this.baseURL}/activate/${id}`, { withCredentials: true, responseType: 'text' as 'json' });
  }

  InactivateStaff(id: number): Observable<any> {
    return this.http.get(`${this.baseURL}/inactivate/${id}`, { withCredentials: true, responseType: 'text' as 'json' });
  }
}