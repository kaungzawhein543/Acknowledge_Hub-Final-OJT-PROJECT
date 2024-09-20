import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  staffId: string = '';
  password: string = '';
  errorMessage: string = '';
  logoutMessage: string = '';
  showPassword: boolean = false;
  showError: boolean = false;
  constructor(private authService: AuthService, private router: Router) { }

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

    
    this.authService.login(this.staffId, trimmedPassword).subscribe(
      response => {
        const body = response.body;
        console.log(response);
        if (body?.includes(':')) {
          const [staffId, message] = body.split(':');
          console.log(message.toString());
          console.log(message.toString() === 'Please change your password');
          console.log(typeof message);
          if (message.toString() === 'Please change your password') {
            console.log("hi")
            this.router.navigate(['acknowledgeHub/change-password/', staffId]);
          }
        } else {
          this.authService.getUser().subscribe(user => {
            console.log('User data:', user);
            if (user.user.role === "USER" && user.position !== "HR_MAIN") {
                this.router.navigate(['/acknowledgeHub/staff-dashboard']);
            } else if (user.user.role === "ADMIN" || user.position === "HR_MAIN") {
                this.router.navigate(['/acknowledgeHub/system-dashboard']);
            }
        });
        }
      },
      error => {
        this.errorMessage = error.error;
        // setTimeout(() => {
        //   this.errorMessage = '';
        // }, 5000);
      }
    );
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
}