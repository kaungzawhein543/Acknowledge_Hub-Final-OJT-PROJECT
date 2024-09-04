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
  private apiUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) { }

  getAllCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(`${this.apiUrl}/companies`);
  }

  getAllGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.apiUrl}/group`, { withCredentials: true });
  }
  

  
  createGroup(userIds: number[], groupName: string): Observable<string> {
    const encodedGroupName = encodeURIComponent(groupName);

    const url = `${this.apiUrl}/group/create?name=${encodedGroupName}`;

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
