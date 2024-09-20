import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { WebSocketService } from '../../services/web-socket.service';
import { Notification } from '../../models/Notification';
import { NotificationService } from '../../services/notification.service';
import { Subject, takeUntil } from 'rxjs';
import { StaffProfileDTO } from '../../models/staff';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

  notifications: Notification[] = [];
  showNotifications: boolean = false;
  unreadNotificationCount: number = 0;
  private destroy$ = new Subject<void>();
  private updateInterval: any;
  isDropdownOpen = false;
  position: string = '';
  staff_id : number = 0;
  name: string = '';
  private audio: HTMLAudioElement;
  profile: StaffProfileDTO | null = null;
  baseUrl = 'http://localhost:8080';
  oldPhotoUrl: string | null = null;

  constructor(
    private sidebarService: SidebarService, 
    private authService: AuthService, 
    private router: Router,
    private webSocketService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private profileService : ProfileService
  ) {
    this.audio = new Audio('assets/images/sounds/noti-sound.mp3');
    this.audio.load();
  }

  ngOnInit(): void {
    this.loadProfile();

    this.authService.getUserInfo().subscribe(data => {
      this.staff_id = data.user.id;
      this.position = data.position;
      this.name = data.user.name;
    });

    this.profileService.profile$.subscribe((profile) => {
      this.profile = profile;
      if (this.profile) {
        this.oldPhotoUrl = this.baseUrl + this.profile.photoPath;
      }
    });
  
    this.webSocketService.getNotifications().pipe(takeUntil(this.destroy$)).subscribe({
      next: (notifications) => {
        this.notifications = this.filterLatestNotifications(notifications.reverse());
        this.updateUnreadNotificationCount();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error receiving notifications:', error);
      }
    });
  
    this.webSocketService.getNewNotifications().pipe(takeUntil(this.destroy$)).subscribe({
      next: (notification: Notification) => {
        this.notifications = this.filterLatestNotifications([notification, ...this.notifications]);
        this.audio.play().catch(error => {
          console.error('Error playing sound:', error);
        });
        this.incrementUnreadNotificationCount(notification);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error receiving new notifications:', error);
      }
    });
  
    this.subscribeToStatusUpdates();
    this.startUpdateInterval();
  }
  
  // Utility function to filter notifications and keep the latest "ask a question" notification
  filterLatestNotifications(notifications: Notification[]): Notification[] {
    const announcementMap = new Map<number, Notification>();
    let latestAskQuestionNotification: Notification | null = null;
  
    notifications.forEach(notification => {
      const announcementId = notification.announcementDetails?.id;
      const containsAskQuestion = notification.title?.includes("ask a question") || notification.description?.includes("ask a question");
  
      // Check if the notification contains "ask a question"
      if (containsAskQuestion) {
        // If it's the first one or it's newer than the current latest, replace it
        if (!latestAskQuestionNotification || new Date(notification.created_at) > new Date(latestAskQuestionNotification.created_at)) {
          latestAskQuestionNotification = notification;
        }
      } else if (announcementId) {
        // For other notifications, check if they belong to the same announcement and keep the latest
        const existingNotification = announcementMap.get(announcementId);
        if (!existingNotification || new Date(notification.created_at) > new Date(existingNotification.created_at)) {
          announcementMap.set(announcementId, notification);
        }
      } else {
        // If no announcement id, just push the notification (generic case)
        announcementMap.set(notification.id, notification);
      }
    });
  
    // Convert the map back to an array and add the latest "ask a question" notification
    const filteredNotifications = Array.from(announcementMap.values());
    if (latestAskQuestionNotification) {
      filteredNotifications.unshift(latestAskQuestionNotification);
    }
  
    console.log('Filtered Notifications:', filteredNotifications); // Debugging log
    return filteredNotifications;
  }
  
  // ngOnInit(): void {
  //   this.authService.getUserInfo().subscribe(data => {
  //     this.staff_id = data.user.id;
  //     this.position = data.position;
  //     this.name = data.user.name;
  //   });

  //   this.webSocketService.getNotifications().pipe(takeUntil(this.destroy$)).subscribe({
  //     next: (notifications) => {
  //       this.notifications = notifications.reverse();
  //       this.updateUnreadNotificationCount();
  //       this.cdr.detectChanges();
  //     },
  //     error: (error) => {
  //       console.error('Error receiving notifications:', error);
  //     }
  //   });

  //   this.webSocketService.getNewNotifications().pipe(takeUntil(this.destroy$)).subscribe({
  //     next: (notification: Notification) => {
  //       this.notifications = [notification, ...this.notifications];
  //       this.audio.play().catch(error => {
  //         console.error('Error playing sound:', error);
  //       });
  //       this.incrementUnreadNotificationCount(notification);
  //       this.cdr.detectChanges();
  //     },
  //     error: (error) => {
  //       console.error('Error receiving new notifications:', error);
  //     }
  //   });

  //   // Subscribe to status updates
  //   this.subscribeToStatusUpdates();

  //   // Start interval to update "time ago" display
  //   this.startUpdateInterval();
  // }

  loadProfile(): void {
    this.authService.getProfile().subscribe(
      (data) => {
        this.profile = data;
        this.oldPhotoUrl = this.baseUrl + this.profile?.photoPath;
        this.profileService.updateProfile(this.profile);

        console.log('Resolved photoPath:', this.profile?.photoPath);
        console.log('Profile data:', this.profile);
      },
      (error) => {
        console.error('Error loading profile:', error);
      }
    );

  getNotificationIcon(notification: Notification): string {
    const title = notification.title?.toLowerCase();
    const description = notification.description?.toLowerCase();
  
    if (title?.includes("ask a question") || description?.includes("ask a question")) {
      return 'fas fa-question-circle'; // FontAwesome Q&A icon
    } else if (title?.includes("create") || description?.includes("create")) {
      return 'fas fa-bullhorn -rotate-12'; // FontAwesome bull horn icon
    } else if (title?.includes("update") || description?.includes("update")) {
      return 'fas  fa-file-pen'; // FontAwesome update icon
    } else if (title?.includes("reply") || description?.includes("reply")) {
      return 'fas fa-reply'; // FontAwesome reply icon
    }
    return 'fas fa-info-circle'; // Default icon if none match
  }
  
  // Start interval to update "time ago" every 1 minute
  startUpdateInterval() {
    this.updateInterval = setInterval(() => {
      this.cdr.detectChanges();  // Trigger Angular's change detection to update time ago display
    }, 60000);  // Update every 60 seconds
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    if (this.updateInterval) {
      clearInterval(this.updateInterval);  // Clear the interval on component destroy
    }
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
        this.router.navigate(['/acknowledgeHub/login']);
      },
      (error) => {
        console.error('Logout failed', error);
      }
    );
  }

  closeDropdown() {
    this.isDropdownOpen = false;
  }

  private updateUnreadNotificationCount(): void {
    this.unreadNotificationCount = this.notifications.filter(notification => notification.status === 'active').length;
    this.cdr.detectChanges();
  }

  private incrementUnreadNotificationCount(notification: Notification): void {
    if (notification.status === 'active') {
      this.unreadNotificationCount++;
    }
  }

  private subscribeToStatusUpdates(): void {
    this.webSocketService.getStatusUpdates().pipe(takeUntil(this.destroy$)).subscribe({
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

  goAnotherRouteAndupdateNotification(staff_id: number, notificationId: number, notificationUrl: string) {
    const notification = this.notifications.find(n => n.id === notificationId);
    console.log(notificationUrl);
    if (notification) {
      notification.checked = true;
      notification.status = 'inactive';
  
      // this.router.navigate([`${notificationUrl}`]);
      this.notificationService.updateNotification(staff_id, notificationId).subscribe(
        () => {
          this.showNotifications = false;
        }
      );
    } else {
      console.error('Notification not found');
    }
  }
  

  trackNotificationById(index: number, notification: Notification): number {
    return notification.id;
  }
}

// ngOnInit(): void {
//   this.authService.getUserInfo().subscribe(data => {
//     this.staff_id = data.user.id;
//     this.position = data.position;
//     this.name = data.user.name;
//   });

//   this.webSocketService.getNotifications().pipe(takeUntil(this.destroy$)).subscribe({
//     next: (notifications) => {
//       this.notifications = this.filterLatestNotifications(notifications.reverse());
//       console.log(this.notifications)
//       this.updateUnreadNotificationCount();
//       this.cdr.detectChanges();
//     },
//     error: (error) => {
//       console.error('Error receiving notifications:', error);
//     }
//   });

//   this.webSocketService.getNewNotifications().pipe(takeUntil(this.destroy$)).subscribe({
//     next: (notification: Notification) => {
//       // Insert the new notification and filter the list
//       this.notifications = this.filterLatestNotifications([notification, ...this.notifications]);
//       console.log(`Notifications are ${this.notifications}`)
//       this.audio.play().catch(error => {
//         console.error('Error playing sound:', error);
//       });
//       this.incrementUnreadNotificationCount(notification);
//       this.cdr.detectChanges();
//     },
//     error: (error) => {
//       console.error('Error receiving new notifications:', error);
//     }
//   });

//   this.subscribeToStatusUpdates();
//   this.startUpdateInterval();
// }

// // Utility function to filter notifications and keep the latest "ask a question" notification
// filterLatestNotifications(notifications: Notification[]): Notification[] {
// const announcementMap = new Map<number, Notification>();
// let latestAskQuestionNotification: Notification | null = null;

// notifications.forEach(notification => {
//   const announcementId = notification.announcementDetails.id;
//   const containsAskQuestion = notification.title?.includes("ask a question") || notification.description?.includes("ask a question");

//   // Check if the notification contains "ask a question"
//   if (containsAskQuestion) {
//     // If it's the first one or it's newer than the current latest, replace it
//     if (!latestAskQuestionNotification || new Date(notification.created_at) > new Date(latestAskQuestionNotification.created_at)) {
//       latestAskQuestionNotification = notification;
//     }
//   } else if (announcementId) {
//     // For other notifications, check if they belong to the same announcement and keep the latest
//     const existingNotification = announcementMap.get(announcementId);
//     if (!existingNotification || new Date(notification.created_at) > new Date(existingNotification.created_at)) {
//       announcementMap.set(announcementId, notification);
//     }
//   } else {
//     // If no announcement id, just push the notification (generic case)
//     announcementMap.set(notification.id, notification);
//   }
// });

// // Convert the map back to an array and add the latest "ask a question" notification
// const filteredNotifications = Array.from(announcementMap.values());
// if (latestAskQuestionNotification) {
//   filteredNotifications.unshift(latestAskQuestionNotification);
// }

// return filteredNotifications;
// }