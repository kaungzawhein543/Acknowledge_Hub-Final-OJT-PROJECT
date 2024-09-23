import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ResponseEmail } from '../../models/response-email';

@Component({
  selector: 'app-otp-request',
  templateUrl: './otp-request.component.html',
  styleUrl: './otp-request.component.css'
})
export class OtpRequestComponent {

  staffId!: string;
  responseEmail!: ResponseEmail;
  loading: boolean = false;
  status: boolean = false;
  constructor(private service: AuthService, private router: Router) { }

  onSubmit(form: NgForm): void {
    this.staffId = this.staffId.trim();
    if (this.staffId != '') {
      if (form.valid) {
        this.loading = true;
        this.service.getOTP(this.staffId).subscribe({
          next: (data) => {
            console.log(data)
            this.responseEmail = data;
            if (this.responseEmail && this.responseEmail.email && this.responseEmail.expiryTime) {
              sessionStorage.setItem('email', this.responseEmail.email);
              sessionStorage.setItem('staffId', this.staffId);
              const expiryTime = this.responseEmail.expiryTime;
              sessionStorage.setItem('otpExpiry', new Date(expiryTime).toISOString());
              this.gotoOTPInput();
            } else {
              this.status = true;
              console.error('Response did not contain the required data.');
            }
            this.loading = false;
          },
          error: (error) => {
            this.loading = false;
            this.status = true;
            console.error('Error occurred:', error);
          }
        });
      }
    }
  }

  gotoOTPInput() {
    this.router.navigate(['/acknowledgeHub/otp-input']);
  }
  gotoback() {
    this.router.navigate(['/login']);
  }
}
