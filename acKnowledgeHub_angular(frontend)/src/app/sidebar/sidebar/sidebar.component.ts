import { Component, OnInit, AfterViewInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { animate, style, transition, trigger } from '@angular/animations';
import { combineLatest, Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
  animations: [
    trigger('sidebarAnimation', [
      transition(':enter', [
        style({ width: '0px' }),
        animate('300ms ease-out', style({ width: '250px', opacity: '100' }))
      ]),
      transition(':leave', [
        style({ width: '250px' }),
        animate('200ms ease-out', style({ opacity: 0 })),
        animate('300ms ease-out', style({ width: '0px' }))
      ])
    ])
  ]
})
export class SidebarComponent implements OnInit, AfterViewInit {
  isAdmin = false;
  isMainHr = false;
  isHr = false;
  staffId !: number;
  isSidebarOpen = true;
  private currentOpenMenu: string | null = null;

  toggleMenu(menuId: string): void {
    const menu = document.getElementById(menuId);
    if (menu) {
      if (this.currentOpenMenu === menuId) {
        // Close the current menu
        menu.classList.toggle('hidden');
        this.currentOpenMenu = null;
      } else {
        // Close all other menus
        this.closeAllMenus();
        // Open the new menu
        menu.classList.remove('hidden');
        this.currentOpenMenu = menuId;
      }
    }
  }

  closeAllMenus(): void {
    const menus = document.querySelectorAll('.space-y-2.pl-8.mt-2');
    menus.forEach((menu) => {
      menu.classList.add('hidden');
    });
  }


  constructor(private sidebarService: SidebarService, private authService: AuthService) {

  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  ngAfterViewInit() {
    this.sidebarService.getSidebarState().subscribe((state) => {
      this.isSidebarOpen = state;
    });
  }

  ngOnInit() {
    this.authService.hasRole("ADMIN").subscribe(
      (data) => {
        this.isAdmin = data;
      }
    )
    this.authService.hasPostion("HR_MAIN").subscribe(
      (data) => {
        this.isMainHr = data;
        this.authService.hasPostion("HR").subscribe(
          (data) => {
            this.isHr = data;
          }
        )
      }
    )
    this.authService.getUserInfo().subscribe(
      (data) => {
        this.staffId = data.user.id;
      }
    )
  }

}
