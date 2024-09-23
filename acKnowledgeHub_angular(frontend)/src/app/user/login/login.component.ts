import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  staffId: string = '';
  alermTooktip: boolean = false;
  rememberMe: boolean = false;
  password: string = '';
  errorMessage: string = '';
  logoutMessage: string = '';
  showPassword: boolean = false;
  showError: boolean = false;
  isLocked: boolean = false;
  countdown: number = 0;
  failedAttempts: number = 0;
  formattedCountdown: string = '';
  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    const lockedData = localStorage.getItem('lockedData');

    if (lockedData) {
      const parsedData = JSON.parse(lockedData);
      const currentTime = new Date().getTime();
      const remainingTime = parsedData.lockEndTime - currentTime;

      if (remainingTime > 0) {
        this.isLocked = true;
        this.countdown = Math.floor(remainingTime / 1000);  // Calculate remaining seconds
        this.updateFormattedCountdown();

        const intervalId = setInterval(() => {
          this.countdown--;
          this.updateFormattedCountdown();

          if (this.countdown <= 0) {
            clearInterval(intervalId);
            this.isLocked = false;
            this.failedAttempts = 0;
            localStorage.removeItem('lockedData');  // Clear lock data when countdown finishes
          }
        }, 1000);
      }
    }
  }

  onLogin(form: NgForm) {
    this.showError = true; // Show validation errors on submit

    if (!this.staffId && !this.password) {
      this.errorMessage = 'Please fill in all required fields.';

      return;
    }
    this.errorMessage = '';

    if (form.invalid) {

      return;
    }

    const trimmedPassword = this.password.trim();


    this.authService.login(this.staffId, trimmedPassword, this.rememberMe).subscribe(
      response => {
        const body = response.body;
        if (body?.includes(':')) {
          const [staffId, message] = body.split(':');
          if (message.toString() === 'Please change your password') {
            console.log("hi")
            this.router.navigate(['acknowledgeHub/change-password/', staffId]);
          }
        } else {
          this.authService.getUser().subscribe(user => {
            if (user.user.role === "USER" && user.position !== "HR_MAIN") {
              this.router.navigate(['/acknowledgeHub/staff-dashboard']);
            } else if (user.user.role === "ADMIN" || user.position === "HR_MAIN") {
              this.router.navigate(['/acknowledgeHub/system-dashboard']);
            }
          });
        }
        this.failedAttempts = 0;
      },
      error => {
        this.errorMessage = error.error;
        // setTimeout(() => {
        //   this.errorMessage = '';
        // }, 5000);
        this.failedAttempts++;

        if (this.failedAttempts >= 5) {
          this.isLocked = true;
          this.countdown = 120;  // Set countdown to 120 seconds (2 minutes)
          const lockEndTime = new Date().getTime() + this.countdown * 1000;  // Store lock end time

          localStorage.setItem('lockedData', JSON.stringify({ lockEndTime }));

          this.updateFormattedCountdown();

          const intervalId = setInterval(() => {
            this.countdown--;
            this.updateFormattedCountdown();

            if (this.countdown <= 0) {
              clearInterval(intervalId);
              this.isLocked = false;
              this.failedAttempts = 0;
              localStorage.removeItem('lockedData');
            }
          }, 1000);
        }
      }
    );
  }

  showToolTip(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.alermTooktip = true;
      this.rememberMe = true;
    } else {
      this.alermTooktip = false;
      this.rememberMe = false;
    }
  }

  private updateFormattedCountdown() {
    const minutes: number = Math.floor(this.countdown / 60);
    const seconds: number = this.countdown % 60;
    this.formattedCountdown =
      `${this.padZero(minutes)}:${this.padZero(seconds)}`;
  }

  // Helper method to pad single digits with a leading zero
  private padZero(value: number): string {
    return value < 10 ? '0' + value : value.toString();
  }

  onLogout() {
    this.authService.logout().subscribe(
      response => {
        console.log(response);
        this.logoutMessage = "Logout Successfully";
      },
      error => {
        console.error('Logout error:', error);
      }
    );
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}