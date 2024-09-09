import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { error } from 'node:console';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isDropdownOpen = false;
  position: string = '';
  name: string = '';
  constructor(private sidebarService: SidebarService, private authService: AuthService, private router: Router) { }


  ngOnInit(): void {
    this.authService.getUserInfo().subscribe(
      data => {
        this.position = data.position;
        this.name = data.user.name;
      },
    )
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
