import { Component } from '@angular/core';
import { LoadingService } from '../services/loading.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {
  // constructor(private loadingService: LoadingService) {}

  // ngOnInit() {
  //   this.loadingService.show(); // Show loading indicator when initializing

  //   // Simulate a delay and then hide the loading indicator
  //   setTimeout(() => {
  //     this.loadingService.hide();
  //   }, 2000); // 2-second delay for demonstration
  // }
}
