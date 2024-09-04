import { Injectable } from '@angular/core';
import { Group } from '../models/Group';
import { Staff } from '../models/staff';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExampleDataServiceService {

  constructor() { }
   private groups: Group[] = [
    { id: 1, name: 'Marketing',  status: 'Active' },
    { id: 2, name: 'Development',  status: 'Inactive' },
    { id: 3, name: 'Sales', status: 'Active' }
  ];

  private staffs: Staff[] = [
    { id: 1, name: 'John', position: 'Manager', groupId: 1 },
    { id: 2, name: 'Jane', position: 'Developer', groupId: 2 },
    { id: 3, name: 'Alice', position: 'Analyst', groupId: 3 },
    { id: 4, name: 'John', position: 'Analyst', groupId: 3 },
    { id: 5, name: 'Miya', position: 'Analyst', groupId: 3 },
    { id: 6, name: 'Alucard', position: 'Analyst', groupId: 3 },
    { id: 7, name: 'Yougi', position: 'Analyst', groupId: 3 },
    { id: 8, name: 'Fogie', position: 'Analyst', groupId: 3 },
    { id: 9, name: 'Gindo', position: 'Analyst', groupId: 3 },
    { id: 10, name: 'Barbie', position: 'Analyst', groupId: 3 },
    // Add more staff as needed
  ];

  getGroups(): Observable<Group[]> {
    return of(this.groups);
  }

  getStaffs(): Observable<Staff[]> {
    return of(this.staffs);
  }
}