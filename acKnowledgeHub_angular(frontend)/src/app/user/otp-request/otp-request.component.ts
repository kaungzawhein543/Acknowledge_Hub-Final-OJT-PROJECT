import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ResponseEmail } from '../../models/response-email';
import { StaffService } from '../../services/staff.service';
import * as bcrypt from 'bcryptjs';
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
  error : string = '';
  constructor(private service: AuthService, private router: Router,private staffService :StaffService) { }

  onSubmit(form: NgForm): void {
    this.staffId = this.staffId.trim();
    if (this.staffId != '') {
      if (form.valid) {
        this.checkOldPassword(this.staffId);
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

  checkOldPassword(staffId: string): void {
    this.staffService.checkOldPassword(staffId).subscribe(
      (isPasswordMatch: boolean) => {
        if (isPasswordMatch) {
          this.error = "You need to change your default password first!";
          setTimeout(() => {
            this.router.navigate(["/acknowledgeHub/login"]);
          }, 2000);
          return;
        } 
      },
      error => {
        console.error('Error checking old password:', error);
        this.error = "An error occurred while checking the password. Please try again.";
      }
    );
  }
}
