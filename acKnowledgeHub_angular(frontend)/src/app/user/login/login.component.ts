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
  logoutMessage: string = '';
  showPassword: boolean = false;
  constructor(private authService: AuthService, private router: Router) { }

  onLogin() {
    
    this.authService.login(this.staffId, this.password).subscribe(
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
            this.router.navigate(['/change-password/', staffId]);
          }
        } else {
          this.authService.getUser().subscribe(user => {
            console.log('User data:', user);
            if (user.user.role === "USER" && user.position !== "HR_MAIN") {
                this.router.navigate(['/staff-dashboard']);
            } else if (user.user.role === "ADMIN" || user.position === "HR_MAIN") {
                this.router.navigate(['/dashboard']);
            }
        });
        }
      },
      error => {
        this.errorMessage = error.error;
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


  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

}