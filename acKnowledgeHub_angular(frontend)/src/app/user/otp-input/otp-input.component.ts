import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { interval, Subscription } from 'rxjs';
import { ResponseEmail } from '../../models/response-email';
import { Router } from '@angular/router';

@Component({
  selector: 'app-otp',
  templateUrl: './otp-input.component.html',
  styleUrls: ['./otp-input.component.css']
})
export class OtpInputComponent implements OnInit {
  otp: string[] = Array(6).fill('');
  email!: string;
  OTP!: string;
  staffId!: string;
  responseEmail!: ResponseEmail;
  countdown: string = '00:00';
  validationError: boolean = false;
  loading: boolean = false;
  private countdownSubscription!: Subscription;

  constructor(private service: AuthService, private router: Router) { }

  ngOnInit(): void {
    const sessionEmail = sessionStorage.getItem('email');
    const otpExpiry = sessionStorage.getItem('otpExpiry');
    if (sessionEmail != null) {
      this.email = sessionEmail;
    }

    if (otpExpiry) {
      this.startCountdown(new Date(otpExpiry));
    }
  }

  startCountdown(expiryTime: Date): void {
    this.loading = false;
    this.countdownSubscription = interval(1000).subscribe(() => {
      const now = new Date();
      const timeRemaining = expiryTime.getTime() - now.getTime();
      if (timeRemaining <= 0) {
        this.countdown = '00:00';
        this.countdownSubscription.unsubscribe();
      } else {
        const minutes = Math.floor(timeRemaining / (1000 * 60));
        const seconds = Math.floor((timeRemaining % (1000 * 60)) / 1000);
        this.countdown = `${this.pad(minutes)}:${this.pad(seconds)}`;
      }
    });
  }

  pad(value: number): string {
    return value.toString().padStart(2, '0');
  }

  onInput(index: number): void {
    const inputElements = document.querySelectorAll('#otp > input') as NodeListOf<HTMLInputElement>;
    const input = inputElements[index];

    if (input.value.length === 1 && index < 5) {
      (inputElements[index + 1] as HTMLInputElement).focus();

    } else if (input.value.length === 0 && index > 0) {
      (inputElements[index - 1] as HTMLInputElement).focus();
      this.checkValidation();
    }
    if (this.otp.every(o => o.length === 1)) {
      this.checkValidation();
    }
  }

  validateOtp(): void {
    this.checkValidation();
    this.OTP = this.otp.join('');
    this.service.sendOTP(this.email, this.OTP).subscribe({
      next: (data) => {
        if (data === 1) {
          console.log("Successful");
          this.router.navigate(['/add-password']);
        } else {
          this.resetOtp();
        }
      },
      error: (e) => console.log(e)
    });
  }

  resetOtp(): void {
    this.otp = ['', '', '', '', '', ''];
    const firstInput = document.querySelector<HTMLInputElement>('#otp input');
    if (firstInput) {
      firstInput.focus();
    }
  }

  checkValidation(): void {
    this.validationError = this.otp.some(o => o.trim() === '');
  }

  resend(): void {
    this.loading = true;
    console.log("reach resend")
    const sessionStaffId = sessionStorage.getItem('staffId');
    if (sessionStaffId != null) {
      this.staffId = sessionStaffId;
    }
    this.service.getOTP(this.staffId).subscribe({
      next: (data) => {
        console.log("here is in  otp " + data.email)
        this.responseEmail = data;
        if (this.responseEmail && this.responseEmail.email && this.responseEmail.expiryTime) {
          const expiryTime = this.responseEmail.expiryTime;
          if (expiryTime) {
            this.startCountdown(new Date(expiryTime));
          } else {
            console.error('Expiry time is undefined.');
          }
        } else {
          this.loading = false;
          console.error('Response did not contain the required data.');
        }
      },
      error: (error) => {
        this.loading = false;
        console.error('Error occurred:', error);
      }
    });
  }

  isCountdownActive(): boolean {
    return this.countdown !== '00:00';
  }
}
