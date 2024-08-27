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
    this.authService.hasRole("ADMIN").subscribe(
      (data)=> {
        if(data){
          this.buttonRoute = "/admindashboard";
        }else{this.authService.hasPostion("HR_MAIN").subscribe(
          (data)=>{
            if(data){
              this.buttonRoute ="/hr-dashboard";
            }else{
              this.buttonRoute = "/staff-dashboard"
            }
          }
        )

        }
      }
    )
  }

  
}
