import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

import { WebSocketService } from '../../services/web-socket.service';
import { Notification } from '../../models/Notification';
import { NotificationService } from '../../services/notification.service';

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

  constructor(private sidebarService: SidebarService,private authService: AuthService,private notificationService: NotificationService,
    private webSocketService: WebSocketService,private router : Router) {}
  
  ngOnInit(): void {
   this.subscribeToNotifications();
   this.subscribeToStatusUpdates();
  }



  private subscribeToNotifications(): void {
    this.webSocketService.getNotifications().subscribe({
      next: (notifications) => {
       this.notifications = [...notifications, ...this.notifications];
        this.updateUnreadNotificationCount();
      },
      error: (error) => {
        console.error('Error receiving notifications:', error);
      }
    });
  }

  private updateUnreadNotificationCount(): void {
    this.unreadNotificationCount = this.notifications.filter(notification => notification.status === 'active').length;
  }

  private subscribeToStatusUpdates(): void {
    this.webSocketService.getStatusUpdates().subscribe({
      next: (updatedIds) => {
        
        this.notifications.forEach(notification => {
          if (updatedIds.includes(notification.id)) {
            notification.status = 'inactive';
          }
        });

        
        this.updateUnreadNotificationCount();
      },
      error: (error) => {
        console.error('Error receiving status updates:', error);
      }
    });
  }

 
  toggleSidebar() {
    this.sidebarService.toggle(); 
  }
  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  
    if (this.unreadNotificationCount > 0) {
      
      const notificationIds = this.notifications
        .filter(notification => notification.status === 'active') 
        .map(notification => notification.id);
  
     
      this.notificationService.toggleNotificationStatus(notificationIds).subscribe({
        next: () => {
          
          this.notifications.forEach(notification => {
            if (notificationIds.includes(notification.id)) {
              notification.status = 'inactive'; 
            }
          });
          this.updateUnreadNotificationCount();
        },
        error: (error) => {
          console.error('Error updating notification status:', error);
        }
      });
    }
  }
  
  trackNotificationById(index: number, notification: Notification): number {
    return notification.id;
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