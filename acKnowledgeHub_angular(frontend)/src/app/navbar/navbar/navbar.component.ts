import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StaffProfileDTO } from '../../models/staff';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit{
  isDropdownOpen = false;
  position : string = '';
  name : string = '';
  profile: StaffProfileDTO | null = null;
  baseUrl = 'http://localhost:8080';
  oldPhotoUrl: string | null = null; // To store the old photo URL



  constructor(private sidebarService: SidebarService,private authService: AuthService,private router : Router,    private profileService: ProfileService // Inject the service
  ) {}
  
  
  ngOnInit(): void {
    this.loadProfile();

    this.authService.getUserInfo().subscribe((data) => {
      this.position = data.position;
      this.name = data.user.name;
    });

    // Listen for profile changes
    this.profileService.profile$.subscribe((profile) => {
      this.profile = profile;
      if (this.profile) {
        this.oldPhotoUrl = this.baseUrl + this.profile.photoPath;
      }
    });
  }

  loadProfile(): void {
    this.authService.getProfile().subscribe(
      (data) => {
        this.profile = data;
        this.oldPhotoUrl = this.baseUrl + this.profile?.photoPath;
        this.profileService.updateProfile(this.profile);

        console.log('Resolved photoPath:', this.profile?.photoPath);
        console.log('Profile data:', this.profile);
      },
      (error) => {
        console.error('Error loading profile:', error);
      }
    );
  }

  toggleSidebar() {
    this.sidebarService.toggle(); 
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout(): void {
    this.authService.logout().subscribe(
      () => {
        this.router.navigate(['/acknowledgeHub/login']);
      },
      (error) => {
        console.error('Logout failed', error);
      }
    );
  }
  
  
  closeDropdown() {
    this.isDropdownOpen = false;
  }
}
