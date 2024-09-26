import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { StaffProfileDTO } from '../../models/staff';
import { StaffService } from '../../services/staff.service';
import { ProfileService } from '../../services/profile.service';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { ChangePasswordRequest } from '../../models/change-password-request.model';
import { ToastService } from '../../services/toast.service';


@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        query('.card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(200, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
          ]),
        ]),
      ]),
    ]),
  ],
})
export class ProfileComponent implements OnInit {
  profile: StaffProfileDTO | null = null;
  selectedFile: File | null = null;
  newPhotoUrl: string | ArrayBuffer | null = null; // To store the preview of the new photo
  oldPhotoUrl: string | null = null; // To store the old photo URL
  showModal: boolean = false; // To control the visibility of the modal
  timestamp: number = Date.now();
  baseUrl = 'http://localhost:8080';
  selectedMonthCount: number | null = null; // To store the count for the selected month
  selectedMonth: string | null = null; // Property to store selected month
  monthlyCount: number = 0; // Property to store the count for the selected month

  // Properties for password change
  showChangePasswordModal: boolean = false;
  oldPassword: string = '';
  newPassword: string = '';
  changePasswordMessage: string | null = null;
  successMessage:string | null = null;


  constructor(
    private authService: AuthService,
    private staffService: StaffService,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef,
    private profileService: ProfileService // Inject the service


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
        console.log(this.oldPhotoUrl)
        this.profileService.updateProfile(this.profile);
        // Initialize the selected month count
        if (this.profile?.monthlyCount) {
          const firstMonth = Object.keys(this.profile.monthlyCount)[0];
          this.selectedMonth = firstMonth; // Set default selected month
          this.selectedMonthCount = this.profile.monthlyCount[firstMonth] ?? 0;
        }
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

  openChangePasswordModal(): void {
    this.showChangePasswordModal = true;
  }

  closeChangePasswordModal(): void {
    this.showChangePasswordModal = false;
    this.oldPassword = '';
    this.newPassword = '';
    this.changePasswordMessage = null;
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

  onMonthChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const selectedMonth = selectElement.value;
    if (this.profile?.monthlyCount) {
      this.selectedMonth = selectedMonth;
      this.selectedMonthCount = this.profile.monthlyCount[selectedMonth] ?? 0;
    }
  }

  // Method to handle password change
  changePassword(): void {
    if (this.oldPassword === this.newPassword) {
      this.changePasswordMessage = 'New password cannot be the same as old password';
      return;
    }
  
    const request: ChangePasswordRequest = {
      staffId: this.profile?.companyStaffId ?? '',
      oldPassword: this.oldPassword,
      newPassword: this.newPassword
    };
  
    this.staffService.changeOldPassword(request).subscribe(
      response => {
        this.changePasswordMessage = null;
        this.successMessage = response;
        this.oldPassword = '';
        this.newPassword = '';
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
        this.closeChangePasswordModal();
        this.showSuccessToast();
      },
      error => {
        console.error('Error changing password', error);
        this.changePasswordMessage = 'Failed to change password. Please try again.';
      }
    );
  }

  showSuccessToast() {
    this.toastService.showToast('Change Password successful!', 'success');
  }
}