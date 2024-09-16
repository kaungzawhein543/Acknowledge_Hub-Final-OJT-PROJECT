import { Component, OnInit } from '@angular/core';
import { AnnouncementListDTO, AnnouncementStatsDTO, MonthlyCountDTO } from '../models/announcement';
import { AnnouncementService } from '../services/announcement.service';
import { StaffSummaryCount } from '../models/staff';
import { StaffService } from '../services/staff.service';
import { ChangeDetectorRef } from '@angular/core';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-hrdashboard',
  templateUrl: './hrdashboard.component.html',
  styleUrls: ['./hrdashboard.component.css'],
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        query('.card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(200, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
          ])
        ]),
      ]),
    ]),
  ],
})
export class HRdashboardComponent implements OnInit {

  stats: AnnouncementStatsDTO | null = null;
  announcements: AnnouncementListDTO[] = [];
  monthlyCounts: MonthlyCountDTO[] = [];
  progressBarWidth: string = '0%'; 
  staffSummaryCount : StaffSummaryCount | null = null;

  animatedStats = {
    totalAnnouncements: 0,
    publishedAnnouncements: 0,
    unpublishedAnnouncements: 0,
    totalStaff: 0,
    activeStaff: 0,
    inactiveStaff: 0,
  };

  constructor(
    private announcementService: AnnouncementService,
    private staffService: StaffService,
    private cdr: ChangeDetectorRef  // Inject ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAnnouncementStats();
    this.loadAnnouncementListDESC();
    this.loadAnnouncementCountByMonth();
    this.loadStaffSummaryCount();
  }
  
  loadAnnouncementStats(): void {
    this.announcementService.getAnnouncementStats().subscribe(
      (data: AnnouncementStatsDTO) => {
        this.stats = data;

        this.animateNumbers('totalAnnouncements', data.totalAnnouncements, 1000);
        this.animateNumbers('publishedAnnouncements', data.publishedAnnouncements, 1000);
        this.animateNumbers('unpublishedAnnouncements', data.unpublishedAnnouncements, 1000);

        this.cdr.detectChanges();
        setTimeout(() => {
          this.progressBarWidth = '100%'; 
        }, 0); 
      },
      (error) => {
        console.error('Error fetching announcement statistics', error);
      }
    );
  }

  loadAnnouncementListDESC(): void {
    this.announcementService.getPublishedAnnouncements().subscribe(
      (data: AnnouncementListDTO[]) => {
        this.announcements = data.slice(0, 3);
      },
      error => {
        console.error('Error fetching announcements', error);
      }
    );
  }

  loadAnnouncementCountByMonth(): void {
    this.announcementService.getMonthlyAnnouncementCounts().subscribe((data: MonthlyCountDTO[]) => {
      this.monthlyCounts = data;
    });
  }

  getMonthName(month: number): string {
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June', 'July',
      'August', 'September', 'October', 'November', 'December'
    ];
    return monthNames[month - 1]; 
  }

  loadStaffSummaryCount(): void{
    this.staffService.getStaffCount().subscribe(
      (data: StaffSummaryCount) => {
        this.staffSummaryCount = data;
        this.animateNumbers('totalStaff', data.totalStaff, 1000);
        this.animateNumbers('activeStaff', data.activeStaff, 1000);
        this.animateNumbers('inactiveStaff', data.inactiveStaff, 1000);
      },
      (error) => {
        console.error('Error fetching staff count', error);
      }
    );
  }

  animateNumbers(field: keyof typeof this.animatedStats, targetValue: number, duration: number): void {
    const increment = targetValue / (duration / 100); 
    const interval = setInterval(() => {
      if (this.animatedStats[field] < targetValue) {
        this.animatedStats[field] += Math.ceil(increment);
    
        this.cdr.detectChanges();
      } else {
        this.animatedStats[field] = targetValue; 
        clearInterval(interval);
      }
    }, 100); // Update every 100ms
  }
}
