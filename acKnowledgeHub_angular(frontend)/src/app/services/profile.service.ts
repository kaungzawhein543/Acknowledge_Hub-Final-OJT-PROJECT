import { Injectable } from '@angular/core';
import { StaffProfileDTO } from '../models/staff';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private profileSubject = new BehaviorSubject<StaffProfileDTO | null>(null);
  profile$ = this.profileSubject.asObservable();

  updateProfile(profile: StaffProfileDTO): void {
    this.profileSubject.next(profile);
  }
}
