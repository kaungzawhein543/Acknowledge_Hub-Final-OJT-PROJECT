import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { StaffProfileDTO } from '../../models/announcement';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  profile: StaffProfileDTO | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getProfile().subscribe(
      (data) => {
        this.profile = data;
        console.log('Profile data:', this.profile);
      },
      (error) => {
        console.error('Error loading profile:', error);
      }
    );
  }
}
