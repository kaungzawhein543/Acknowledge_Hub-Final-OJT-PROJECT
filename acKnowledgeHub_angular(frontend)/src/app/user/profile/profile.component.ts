import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { StaffProfileDTO } from '../../models/staff';
import { StaffService } from '../../services/staff.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  profile: StaffProfileDTO | null = null;
  selectedFile: File | null = null;
  newPhotoUrl: string | ArrayBuffer | null = null; // To store the preview of the new photo
  oldPhotoUrl: string | null = null; // To store the old photo URL
  showModal: boolean = false; // To control the visibility of the modal
  timestamp: number = Date.now();
  baseUrl = 'http://localhost:8080';

  constructor(
    private authService: AuthService,
    private staffService: StaffService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.authService.getProfile().subscribe(
      (data) => {
        this.profile = data;
        this.timestamp = Date.now(); // Update the timestamp when profile is loaded
        this.oldPhotoUrl = this.baseUrl + this.profile?.photoPath + '?' + this.timestamp;
        console.log('Resolved photoPath:', this.profile?.photoPath);
        console.log('Profile data:', this.profile);
      },
      (error) => {
        console.error('Error loading profile:', error);
      }
    );
  }

  // Handle undefined or null monthlyCount safely
  getMonths(monthlyCount: { [month: string]: number } | undefined): string[] {
    return monthlyCount ? Object.keys(monthlyCount) : [];
  }

  openModal(): void {
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.newPhotoUrl = null; // Clear new photo preview
    this.selectedFile = null; // Clear selected file
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];

      // Create a preview for the new photo
      const reader = new FileReader();
      reader.onload = () => {
        this.newPhotoUrl = reader.result; // Store the preview URL
        this.cdr.detectChanges(); // Trigger change detection to update the view
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  uploadPhoto(): void {
    if (this.selectedFile) {
      this.staffService.uploadProfilePhoto(this.selectedFile).subscribe(
        response => {
          console.log('Photo uploaded successfully', response);
          this.ngOnInit();  
          this.closeModal(); 
        },
        error => {
          console.error('Error uploading photo', error);
        }
      );
    }
  }
}