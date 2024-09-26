import { Component, AfterViewInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

import { Platform } from '@angular/cdk/platform';
import { ChartService } from '../../services/chart.service';
import { Chart, registerables } from 'chart.js';
import { AnnouncementStaffCountDTO } from '../../models/announcement';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-mychart',
  templateUrl: './mychart.component.html',
  styleUrls: ['./mychart.component.css'],
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
export class MychartComponent implements AfterViewInit {
  announcementData: AnnouncementStaffCountDTO[] = [];
  announcementByIdData: AnnouncementStaffCountDTO[] = [];
  filteredData: AnnouncementStaffCountDTO[] = [];
  startDate: string | null = null;
  endDate: string | null = null;
  chart: Chart | undefined;
  noData: boolean = false;
  // showSecondChart: boolean = false; // add this variable


  constructor(
    private platform: Platform,
    @Inject(PLATFORM_ID) private platformId: Object,
    private chartService: ChartService,
    private cdr: ChangeDetectorRef
  ) {
    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.fetchAnnouncementData();
    }
  }

  getCurrentDate(): string {
    const currentDate = new Date();
    return currentDate.toISOString().slice(0, 10); // Format: yyyy-MM-dd
  }

  //for announcement
  fetchAnnouncementData() {
    this.chartService.getAnnouncementStaffCounts().subscribe(data => {
      this.announcementData = data.map(item => ({
        ...item,
        createdAt: new Date(item.created_at).toLocaleDateString()
      }));
      this.fetchAnnouncementByIdData(); // Fetch additional data for the line chart
    });
  }

  //for staff
  fetchAnnouncementByIdData() {
    this.chartService.getStaffCountByAnnouncement().subscribe(data => {
      this.announcementByIdData = data.map(item => ({
        ...item,
        createdAt: new Date(item.created_at).toLocaleDateString()
      }));
      this.cdr.detectChanges(); // Ensure DOM updates are applied
      this.filterDataAndCreateChart();
    });
  }

  onDateChange() {
    this.startDate = (document.getElementById('startDate') as HTMLInputElement).value;
    this.endDate = (document.getElementById('endDate') as HTMLInputElement).value;
    this.filterDataAndCreateChart();
  }

  filterDataAndCreateChart() {
    if (this.startDate && this.endDate) {
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);
      start.setHours(0, 0, 0, 0);
      end.setHours(23, 59, 59, 999);
  
      this.filteredData = this.announcementData.filter(item => {
        const itemDate = new Date(item.created_at);
        itemDate.setHours(0, 0, 0, 0);
        return itemDate >= start && itemDate <= end;
      });
    } else {
      this.filteredData = this.announcementData.slice(0,5);
    }
  
    //.slice(0,2)
    this.noData = !(this.filteredData.length > 0 && this.announcementByIdData.length > 0);
    if (this.noData) {
      if (this.chart) {
        this.chart.destroy(); // Destroy the chart if it exists
      }
    } else {
      setTimeout(() => {
        this.createCombinedChart();
        // this.createSecondChart();
      }, 100); // Delay to ensure DOM updates
    }
  }


  // switchChart() {
  //   this.showSecondChart = !this.showSecondChart;
  //   if (this.showSecondChart) {
  //     this.createSecondChart(); // call the method to create the second chart
  //   } else {
  //     this.createCombinedChart(); // call the method to create the original chart
  //   }
  // }
  

  createCombinedChart() {
    if (this.chart) {
      this.chart.destroy();
    }
  
    const canvas = document.getElementById('chart') as HTMLCanvasElement;
    if (!canvas) {
      console.error('Canvas element not found');
      return;
    }
  
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      console.error('Failed to acquire context from the canvas');
      return;
    }
  
    // Calculate the maximum value for the y-axis dynamically
    const maxYValue = Math.max(
      ...this.filteredData.map(item => item.staffCount),
      ...this.announcementByIdData.map(item => item.staffCount)
    );
  
    // Ensure a minimum max value for better readability
    const yAxisMax = Math.max(maxYValue, 7);
  
    const labels = this.filteredData.map(item => {
      const date = new Date(item.created_at + 'T00:00:00');
      const title = item.title.substring(0, 5) + (item.title.length > 10 ? '...' : ''); // truncate to 10 characters
      return `${title} (${date.toLocaleDateString()})`
    });
    // Predefined colors for bars and lines
    const barColors = [
      'rgba(255, 99, 132, 0.2)',
      'rgba(54, 162, 235, 0.2)',
      'rgba(255, 206, 86, 0.2)',
      'rgba(75, 192, 192, 0.2)',
      'rgba(153, 102, 255, 0.2)',
      'rgba(255, 159, 64, 0.2)'
    ];
  
    const lineColors = [
      'rgba(255, 99, 132, 1)',
      'rgba(54, 162, 235, 1)',
      'rgba(255, 206, 86, 1)',
      'rgba(75, 192, 192, 1)',
      'rgba(153, 102, 255, 1)',
      'rgba(255, 159, 64, 1)'
    ];
    // Utility function to darken a color by a percentage
  function darkenColor(color: string, percent: number) {
    const num = parseInt(color.slice(1), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = ((num >> 8) & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;
    return `#${(
      0x1000000 +
      (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
      (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
      (B < 255 ? B < 1 ? 0 : B : 255)
    )
      .toString(16)
      .slice(1)
      .toUpperCase()}`;
  }
  
    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            type: 'bar',
            label: 'Total staff noted',
            data: this.filteredData.map(item => item.staffCount),
            backgroundColor: barColors.slice(0, this.filteredData.length), // Use predefined colors
            borderColor: barColors.map(color => darkenColor(color, 20)).slice(0, this.filteredData.length), // Darken the border color
            borderWidth: 1
            
          },
          {
            type: 'line',
            label: 'Announced staff total',
            data: this.announcementByIdData.map(item => item.staffCount),
            fill: false,
            borderColor: lineColors.slice(0, this.announcementByIdData.length), // Use predefined colors
            borderWidth: 2,
            pointBackgroundColor: lineColors.slice(0, this.announcementByIdData.length), // Use predefined colors for points
            pointRadius: 5,
            tension: 0.3,

          }
        ]
      },
      options: {
        scales: {
          x: {
            title: {
              display: true,
              text: 'Announcement'
            }
          },
          y: {
            beginAtZero: true,
            max: yAxisMax,
            title: {
              display: true,
              text: 'Noted Staff'
            }
          }
        }
      }
    });
  }
  
  // createSecondChart() {
  //   if (this.chart) {
  //     this.chart.destroy();
  //   }
  
  //   const canvas = document.getElementById('chart') as HTMLCanvasElement;
  //   if (!canvas) {
  //     console.error('Canvas element not found');
  //     return;
  //   }
  
  //   const ctx = canvas.getContext('2d');
  //   if (!ctx) {
  //     console.error('Failed to acquire context from the canvas');
  //     return;
  //   }
  
  //   // Calculate the maximum value for the y-axis dynamically
  //   const maxYValue = Math.max(
  //     ...this.filteredData.map(item => item.staffCount),
  //     ...this.announcementByIdData.map(item => item.staffCount)
  //   );
  
  //   // Ensure a minimum max value for better readability
  //   const yAxisMax = Math.max(maxYValue, 7);
  
  //   const labels = this.filteredData.map(item => {
  //     const date = new Date(item.created_at + 'T00:00:00');
  //     const title = item.title.substring(0, 10) + (item.title.length > 10 ? '...' : ''); // truncate to 10 characters
  //     return `${title} (${date.toLocaleDateString()})`;
  //   });
  //   // Predefined colors for bars and lines
  //   const barColors = [
  //     'rgba(255, 99, 132, 0.2)',
  //     'rgba(54, 162, 235, 0.2)',
  //     'rgba(255, 206, 86, 0.2)',
  //     'rgba(75, 192, 192, 0.2)',
  //     'rgba(153, 102, 255, 0.2)',
  //     'rgba(255, 159, 64, 0.2)'
  //   ];
  
  //   const lineColors = [
  //     'rgba(255, 99, 132, 1)',
  //     'rgba(54, 162, 235, 1)',
  //     'rgba(255, 206, 86, 1)',
  //     'rgba(75, 192, 192, 1)',
  //     'rgba(153, 102, 255, 1)',
  //     'rgba(255, 159, 64, 1)'
  //   ];
  //   // Utility function to darken a color by a percentage
  // function darkenColor(color: string, percent: number) {
  //   const num = parseInt(color.slice(1), 16);
  //   const amt = Math.round(2.55 * percent);
  //   const R = (num >> 16) + amt;
  //   const G = ((num >> 8) & 0x00FF) + amt;
  //   const B = (num & 0x0000FF) + amt;
  //   return `#${(
  //     0x1000000 +
  //     (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
  //     (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
  //     (B < 255 ? B < 1 ? 0 : B : 255)
  //   )
  //     .toString(16)
  //     .slice(1)
  //     .toUpperCase()}`;
  // }
  
  //   this.chart = new Chart(ctx, {
  //     type: 'bar',
  //     data: {
  //       labels: labels,
  //       datasets: [
  //         {
  //           type: 'bar',
  //           label: 'Total staff noted',
  //           data: this.filteredData.map(item => item.staffCount),
  //           backgroundColor: barColors.slice(0, this.filteredData.length), // Use predefined colors
  //           borderColor: barColors.map(color => darkenColor(color, 20)).slice(0, this.filteredData.length), // Darken the border color
  //           borderWidth: 1
            
  //         },
  //         {
  //           type: 'line',
  //           label: 'Announced staff total',
  //           data: this.announcementByIdData.map(item => item.staffCount),
  //           fill: false,
  //           borderColor: lineColors.slice(0, this.announcementByIdData.length), // Use predefined colors
  //           borderWidth: 2,
  //           pointBackgroundColor: lineColors.slice(0, this.announcementByIdData.length), // Use predefined colors for points
  //           pointRadius: 5,
  //           tension: 0.3,

  //         }
  //       ]
  //     },
  //     options: {
  //       scales: {
  //         x: {
  //           title: {
  //             display: true,
  //             text: 'Announcement'
  //           }
  //         },
  //         y: {
  //           beginAtZero: true,
  //           max: yAxisMax,
  //           title: {
  //             display: true,
  //             text: 'Noted Staff'
  //           }
  //         }
  //       }
  //     }
  //   });
  // }
  
  
  
}