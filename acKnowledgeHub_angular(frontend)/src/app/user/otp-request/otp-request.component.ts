import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ResponseEmail } from '../../models/response-email';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-otp-request',
  templateUrl: './otp-request.component.html',
  styleUrl: './otp-request.component.css',
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
export class OtpRequestComponent {

  staffId!: string;
  responseEmail!: ResponseEmail;
  loading: boolean = false;
  status: boolean = false;
  errorMessage: string = '';

  constructor(private service: AuthService, private router: Router) {}

  onSubmit(form: NgForm): void {
    this.trimStaffId();
    if (form.valid) {
      this.loading = true;
      this.service.getOTP(this.staffId).subscribe({
        next: (data) => {
          if (typeof data === 'string') {  // Check if the data is a string
            if (data.includes('Please change your password')) {
              this.status = true;
              this.errorMessage = data;  // Set error message
            }
          } else {
            this.responseEmail = data as ResponseEmail;
            if (this.responseEmail.email && this.responseEmail.expiryTime) {
              sessionStorage.setItem('email', this.responseEmail.email);
              sessionStorage.setItem('staffId', this.staffId);
              const expiryTime = this.responseEmail.expiryTime;
              sessionStorage.setItem('otpExpiry', new Date(expiryTime).toISOString());
              this.gotoOTPInput();
            }
          }
          this.loading = false;
        },
        error: (error) => {
          this.loading = false;
          this.status = true;
          this.errorMessage = 'Error occurred: ' + error.message;  // Set error message on error
        }
      });
    }
  }
  trimStaffId() {
    if (this.staffId) {
      this.staffId = this.staffId.trim();
    }
  }

  gotoOTPInput() {
    this.router.navigate(['/acknowledgeHub/otp-input']);
  }
}