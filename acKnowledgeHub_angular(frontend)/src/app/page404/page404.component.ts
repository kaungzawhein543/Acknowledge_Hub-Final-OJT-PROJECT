import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-page404',
  templateUrl: './page404.component.html',
  styleUrl: './page404.component.css'
})
export class Page404Component implements OnInit{
  buttonRoute !: string;
  
  constructor(private authService:AuthService){}

   ngOnInit(): void {
    // First, check if the user is an admin
    this.authService.hasRole("ADMIN").subscribe(
      (isAdmin) => {
        if (isAdmin) {
          // Admins can go to HR dashboard
          this.buttonRoute = "/dashboard";
        } else {
          // If not an admin, check if the user has the 'HR_MAIN' position
          this.authService.hasPostion("HR_MAIN").subscribe(
            (hasHrMain) => {
              if (hasHrMain) {
                this.buttonRoute = "/dashboard";
              } else {
                this.buttonRoute = "/staff-dashboard";
              }
            },
            (error) => {
              console.error('Error checking position', error);
              this.buttonRoute = "/staff-dashboard"; // Fallback in case of error
            }
          );
        }
      },
      (error) => {
        console.error('Error checking role', error);
        this.buttonRoute = "/staff-dashboard"; // Fallback in case of error
      }
    );
  }

  
}
