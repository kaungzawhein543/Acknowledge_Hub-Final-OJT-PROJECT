import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Company } from '../models/Company';
import { Department } from '../models/Department';
import { Position } from '../models/Position';
import { User } from '../models/user';
import { Group } from '../models/Group';

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private baseURL = 'http://localhost:8080/api/v1/group';

  constructor(private http: HttpClient) { }


  getAllGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.baseURL}/sys/getAllGroup`, { withCredentials: true });
  }
  deleteGroup(id: number): Observable<void> {
    return this.http.get<void>(`${this.baseURL}/HRM/softDelete/` + id, { withCredentials: true });
  }

  getGroupsByHR(id: number): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.baseURL}/allHR/HR/${id}`, { withCredentials: true });
  }

  createGroup(userIds: number[], groupName: string): Observable<string> {
    const encodedGroupName = encodeURIComponent(groupName);

    const url = `${this.baseURL}/HRM/create?name=${encodedGroupName}`;

    return this.http.post(url, userIds, {
      responseType: 'text', // Expect plain text response
      withCredentials: true
    })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error creating group', error);
          return throwError('Failed to create group. Please try again later.');
        })
      );
  }


  // getDepartmentsByCompany(companyId: number): Observable<Department[]> {
  //   return this.http.get<Department[]>(`${this.apiUrl}/departments/${companyId}`);
  // }

  getGroupsByAnnouncementId(id: number): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.baseURL}/sys/list-by-announcement/${id}`, { withCredentials: true });
  }

}
