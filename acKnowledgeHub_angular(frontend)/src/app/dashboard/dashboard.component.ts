import { Component, OnInit } from '@angular/core';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';

import { StaffProfileDTO } from '../models/staff';
import { AuthService } from '../services/auth.service';
import { AnnouncementListDTO } from '../models/announcement';
import { StaffService } from '../services/staff.service';
import { ChartService } from '../services/chart.service';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        query('.card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(200, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
          ]),
        ]),
      ]),
    ]),
  ],
})
export class DashboardComponent implements OnInit {

  announcements: AnnouncementListDTO[] = [];
  staff: StaffProfileDTO | null = null;
  monthlyNotesCount: { [key: string]: number } = {};  // Object to store the notes count by month



  monthNames: string[] = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  ];

  constructor(private authService: AuthService,
    private staffService: StaffService,
    private chartService: ChartService) { }

  ngOnInit(): void {
    this.loadannouncementListDESC();


    this.authService.getProfile().subscribe(
      (data) => {
        this.staff = data;
        console.log('Profile data:', this.staff);
      },
      (error) => {
        console.error('Error loading profile:', error);
      }
    );
    this.loadNotesCountByMonth();


  }


  loadannouncementListDESC(): void {
    this.staffService.getAnnouncementDESC().subscribe(
      (data: AnnouncementListDTO[]) => {
        this.announcements = data.slice(0, 3); // Get only the first 3 announcements
      },
      error => {
        console.error('Error fetching announcements', error);
      }
    );
  }


  // Extract the year and month part from "YYYY-MM" and return "Month Year" (e.g., "January 2024")
  getMonthNameWithYear(yearMonth: string): string {
    // console.log('Received:', yearMonth); // Debugging

    const [year, monthPart] = yearMonth.split('-'); // Extract year and month part (e.g., "2024" and "01")

    if (year && monthPart) {
      const monthIndex = parseInt(monthPart, 10) - 1; // Convert "01" to 0 for January
      const monthName = this.monthNames[monthIndex] || monthPart; // Get the month name
      return `${year} ${monthName} `; // Combine month name and year (e.g., "January 2024")
    }
    return yearMonth; // Fallback to the original string if parsing fails
  }

  // Get months from the monthlyCount object
  getMonths(monthlyCount: { [month: string]: number } | undefined): string[] {
    return monthlyCount ? Object.keys(monthlyCount) : [];
  }

  loadNotesCountByMonth(): void {
    this.chartService.getAdditionalChartData().subscribe(
      (response) => {
        if (response && response.monthlyCount) {
          this.monthlyNotesCount = response.monthlyCount;
        }
      },
      (error) => {
        console.error('Error fetching notes count by month:', error);
      }
    );
  }
  objectKeys(obj: any): string[] {
    return Object.keys(obj);
  }



}