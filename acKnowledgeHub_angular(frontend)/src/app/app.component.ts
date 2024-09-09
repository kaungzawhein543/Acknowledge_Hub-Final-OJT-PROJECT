import { Component, OnInit, HostListener, Inject, PLATFORM_ID, AfterViewInit } from '@angular/core';
import { SidebarService } from './services/sidebar.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter, debounceTime } from 'rxjs';
import { Subject } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { LoadingService } from './services/loading.service';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'acKnowledgeHub_angular';
  isSidebarOpen = true;
  isLoginPage = false;
  isChangePasswordPage = false;
  is404Page = false;
  screenWidth: number = 0;
  resizeSubject = new Subject<void>();
  isLoading = true;
  
  constructor(    private authService: AuthService,private sidebarService: SidebarService, private router: Router,@Inject(PLATFORM_ID) private platformId: Object,private loadingService:LoadingService) { }

  ngOnInit(): void {
    this.loadingService.show(); 
    if (isPlatformBrowser(this.platformId)) {
      this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
      ).subscribe(() => {
        this.isLoginPage = this.router.url === '/login';
        this.isChangePasswordPage = this.router.url.includes('change-password');
        this.is404Page = this.router.url.includes('404');
      });
      this.screenWidth = window.innerWidth ?? 0;
      if (this.screenWidth < 800) {
        this.isSidebarOpen = false;
        this.sidebarService.toggle();
      }
  
      this.resizeSubject.pipe(
        debounceTime(500) // adjust the debounce time as needed
      ).subscribe(() => {
        this.updateSidebarState();
      });
      this.authService.isLoggedIn().subscribe(isAuthenticated => {
        this.isLoading = false;
        this.loadingService.hide();
        if (!isAuthenticated) {
          this.router.navigate(['/login']);
        }
      });
    }
    
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.screenWidth = event.target ? event.target.innerWidth : 0;
    this.resizeSubject.next();
  }

  updateSidebarState() {
    if (this.screenWidth < 800 && this.isSidebarOpen) {
      this.isSidebarOpen = false;
      this.sidebarService.toggle();
    } else if (this.screenWidth >= 800 && !this.isSidebarOpen) {
      this.isSidebarOpen = true;
    }
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
    this.sidebarService.toggle();
  }
}