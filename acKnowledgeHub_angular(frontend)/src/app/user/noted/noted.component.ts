import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { switchMap, of, catchError } from 'rxjs';
import { userInfo } from 'os';

@Component({
  selector: 'app-noted',
  templateUrl: './noted.component.html',
  styleUrl: './noted.component.css'
})
export class NotedComponent {
  publicId: string | null = '';
  dashboardUrl : string='';
  constructor(private router: Router,private authService : AuthService) { }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe(
            userInfo => {
              if(userInfo.isLoggedIn === false){
                this.dashboardUrl  ='/acknowledgeHub/login';
              }
              if(userInfo.user.role === 'ADMIN' || userInfo.position === 'Human Resource(Main)') {
                this.dashboardUrl = '/acknowledgeHub/system-dashboard';
              } else if (userInfo.user.role === 'USER') {
                this.dashboardUrl = '/acknowledgeHub/staff-dashboard';
              }
            }
          
        );
  }


}
