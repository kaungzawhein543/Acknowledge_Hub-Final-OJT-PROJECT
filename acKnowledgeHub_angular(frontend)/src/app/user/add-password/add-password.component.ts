import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-password',
  templateUrl: './add-password.component.html',
  styleUrl: './add-password.component.css'
})
export class AddPasswordComponent {
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  email!: string;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;
  constructor(private authService: AuthService, private router: Router) { }

  addPassword() {
    const sessionEmail = sessionStorage.getItem('email');
    if (sessionEmail != null) {
      this.email = sessionEmail;
    }
    this.authService.addPassword(this.email, this.password).subscribe(
      response => {
        this.successMessage = response;
        this.errorMessage = '';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000); // Wait for 2 seconds before redirecting
      },
      error => {
        this.errorMessage = error.error;
        this.successMessage = '';
      }
    );
  }


  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
