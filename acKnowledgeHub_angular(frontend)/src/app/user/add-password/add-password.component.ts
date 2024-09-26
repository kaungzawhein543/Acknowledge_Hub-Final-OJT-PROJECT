import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-add-password',
  templateUrl: './add-password.component.html',
  styleUrl: './add-password.component.css',
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
        this.successMessage = "Change Password Successfully.";
        this.errorMessage = '';
        setTimeout(() => {
          this.router.navigate(['/acknowledgeHub/login']);
        }, 2000); // Wait for 2 seconds before redirecting
      },
      error => {
        this.errorMessage = "Change Password Failed";
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
