import { Component, OnInit } from '@angular/core';
import { AnnouncementListDTO, AnnouncementStatsDTO, MonthlyCountDTO } from '../models/announcement';
import { AnnouncementService } from '../services/announcement.service';
import { StaffSummaryCount } from '../models/staff';
import { StaffService } from '../services/staff.service';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-hrdashboard',
  templateUrl: './hrdashboard.component.html',
  styleUrl: './hrdashboard.component.css',
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
export class HRdashboardComponent implements OnInit {

  stats: AnnouncementStatsDTO | null = null;
  announcements: AnnouncementListDTO[] = [];
  monthlyCounts: MonthlyCountDTO[] = [];
  staffSummaryCount : StaffSummaryCount | null = null;


  constructor(private announcementService: AnnouncementService, private staffService: StaffService) { }

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
      },
      (error) => {
        console.error('Error fetching announcement statistics', error);
      }
    );
  }

  loadAnnouncementListDESC(): void {
    this.announcementService.getPublishedAnnouncements().subscribe(
      (data: AnnouncementListDTO[]) => {
        this.announcements = data.slice(0, 3); // Get only the first 3 announcements
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
      'January','February','March','April','May','June','July',
      'August','September','October','November','December'
    ];
    return monthNames[month - 1]; // subtract 1 because month is 1-based (1 = January, 2 = February, etc.)
  }

  loadStaffSummaryCount(): void{
    this.staffService.getStaffCount().subscribe(
      (data:StaffSummaryCount)=>{
        this.staffSummaryCount = data;
      },
      (error) =>{
        console.error('Error ',error);
      }
    )
  }
}