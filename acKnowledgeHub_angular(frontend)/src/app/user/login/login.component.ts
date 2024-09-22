import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  staffId: string = '';
  password: string = '';
  errorMessage: string = '';
  emptyFieldError: string = '';
  logoutMessage: string = '';
  showPassword: boolean = false;
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

  onLogin() {
    if (this.isLocked) {
      return; // Prevent login if locked
    }

    this.emptyFieldError = '';  // Reset empty field error message

    if (!this.staffId.trim() || !this.password.trim()) {
      this.emptyFieldError = 'Please enter both Staff ID and Password.';
      return;  // Stop further processing
    }

    this.authService.login(this.staffId, this.password).subscribe(
      response => {
        const body = response.body;
        if (body?.includes(':')) {
          const [staffId, message] = body.split(':');
          if (message.toString() === 'Please change your password') {
            this.router.navigate(['/change-password/', staffId]);
          }
        } else {
          this.authService.getUser().subscribe(user => {
            if (user.user.role === "USER" && user.position !== "HR_MAIN") {
              this.router.navigate(['/staff-dashboard']);
            } else if (user.user.role === "ADMIN" || user.position === "HR_MAIN") {
              this.router.navigate(['/dashboard']);
            }
          });
        }
        this.failedAttempts = 0;
      },
      error => {
        this.errorMessage = error.error;
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