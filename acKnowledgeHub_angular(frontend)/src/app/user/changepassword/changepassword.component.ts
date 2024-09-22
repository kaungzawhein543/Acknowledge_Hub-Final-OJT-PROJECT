import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-changepassword',
  templateUrl: './changepassword.component.html',
  styleUrls: ['./changepassword.component.css']
})
export class ChangepasswordComponent implements OnInit {
  staffId: string = '';
  oldPassword: string = '';
  newPassword: string = '';
  showPassword:string = '';
  errorMessage: string = '';
  successMessage: string = '';
  showError: boolean = false;
  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.staffId = params.get('staffId') || '';
    });
  }

  onChangePassword() {
    this.showError = true; 

    if (!this.oldPassword && !this.newPassword) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }
      this.errorMessage = '';

    if (!this.oldPassword || !this.newPassword) {
      return;
    }
    this.authService.changePassword(this.staffId, this.oldPassword, this.newPassword).subscribe(
      response => {
        this.successMessage = response;
        this.errorMessage = '';
        const cardElement = document.querySelector('.ChangePw-card')!;
        cardElement.classList.add('back-clicked');
        this.showError = false; 
        setTimeout(() => {
          this.router.navigate(['/acknowledgeHub/login']);
        }, 1000); // Wait for 2 seconds before redirecting
      },
      error => {
        this.errorMessage = error.error;
        this.successMessage = '';
      }
    );
  }
  backLogin() {
    const cardElement = document.querySelector('.ChangePw-card')!;
    cardElement.classList.add('back-clicked'); // Trigger animations
    setTimeout(() => {
      this.router.navigate(['/acknowledgeHub/login']);
    }, 1000); 
  }
  
  
}
