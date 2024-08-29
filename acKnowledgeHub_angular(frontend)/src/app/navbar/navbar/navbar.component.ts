import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { Notification } from '../../models/Notification';
import { WebSocketService } from '../../services/web-socket.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  notifications: Notification[] = [];
  showNotifications: boolean = false;
  unreadNotificationCount: number = 0;

  isDropdownOpen = false;
  constructor(private sidebarService: SidebarService,private authService: AuthService,private webSocketService:WebSocketService,private router : Router) {}

ngOnInit(): void {
  this.loadUnreadNotificationCount();
  this.subscribeToNotifications();
}
async loadUnreadNotificationCount() {
  this.unreadNotificationCount = await this.webSocketService.loadUnreadNotificationCount();
}

subscribeToNotifications() {
  this.webSocketService.getNotifications().subscribe(notification => {
    this.notifications.unshift(notification);
    if (!this.showNotifications) {
      this.unreadNotificationCount++;
      this.webSocketService.saveUnreadNotificationCount(this.unreadNotificationCount);
    }
  });
}

get notificationCount(): number {
  return this.unreadNotificationCount;
}

toggleNotifications() {
  if (this.showNotifications) {
    this.webSocketService.markAllAsRead().then(() => {
      this.unreadNotificationCount = 0;
    });
  }
  this.showNotifications = !this.showNotifications;
}
  toggleSidebar() {
    this.sidebarService.toggle(); 
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout(): void {
    this.authService.logout().subscribe(
      () => {
        this.router.navigate(['/login']);
      },
      (error) => {
        console.error('Logout failed', error);
      }
    );
  }
  
  
  closeDropdown() {
    this.isDropdownOpen = false;
  }
}
