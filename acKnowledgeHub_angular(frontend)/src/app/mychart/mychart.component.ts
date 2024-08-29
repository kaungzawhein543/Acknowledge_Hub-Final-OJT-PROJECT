import { Component, AfterViewInit, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { Platform } from '@angular/cdk/platform';
import { AnnouncementStaffCountDTO } from '../models/announcement';
import { ChartService } from '../services/chart.service';

@Component({
  selector: 'app-mychart',
  templateUrl: './mychart.component.html',
  styleUrls: ['./mychart.component.css']
})
export class MychartComponent implements AfterViewInit {
  announcementData: AnnouncementStaffCountDTO[] = [];
  filteredData: AnnouncementStaffCountDTO[] = [];
  startDate: string | null = null;
  endDate: string | null = null;
  chart: any;
  noData: boolean = false; // New variable to track if there's no data

  constructor(
    private platform: Platform,
    @Inject(PLATFORM_ID) private platformId: Object,
    private chartService: ChartService
  ) {
    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.fetchAnnouncementData();
    }
  }

  fetchAnnouncementData() {
    this.chartService.getAnnouncementStaffCounts().subscribe(data => {
      this.announcementData = data.map(item => ({
        ...item,
        created_at: new Date(item.createdAt).toLocaleDateString() // Format the date
      }));
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
    this.filteredData = this.announcementData.filter(item => {
      const itemDate = new Date(item.createdAt + 'T00:00:00');
      return itemDate >= start && itemDate <= end;
    });
  } else {
    this.filteredData = this.announcementData;
  }

  if (this.filteredData.length > 0) {
    this.createBarChart();
  } else {
    this.showNoDataMessage();
  }
}

showNoDataMessage() {
  if (this.chart) {
    this.chart.destroy();
  }

  const ctx = document.getElementById('barChart') as HTMLCanvasElement;
  const message = 'No announcements found for the selected dates.';
  
  const noDataCanvas = ctx.getContext('2d');
  if (noDataCanvas) {
    noDataCanvas.clearRect(0, 0, ctx.width, ctx.height); // Clear the canvas
    noDataCanvas.font = '10px Arial';
    noDataCanvas.fillStyle = '#777';
    noDataCanvas.textAlign = 'center';
    noDataCanvas.fillText(message, ctx.width / 2, ctx.height / 2);
  }
}

  createBarChart() {
    if (this.chart) {
      this.chart.destroy(); // Destroy the previous chart instance to avoid overlap
    }

    const ctx = document.getElementById('barChart') as HTMLCanvasElement;

    const labels = this.filteredData.map(item => {
      const date = new Date(item.createdAt + 'T00:00:00');
      const formattedDate = date.toLocaleDateString();
      return `${item.title} (${formattedDate})`;
    });

    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Staff Count',
          data: this.filteredData.map(item => item.staffCount),
          backgroundColor: [
            'rgba(255, 99, 132, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(255, 206, 86, 0.2)',
            'rgba(75, 192, 192, 0.2)',
            'rgba(153, 102, 255, 0.2)'
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)'
          ],
          borderWidth: 1
        }]
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
            max: 7,
            title: {
              display: true,
              text: 'Noted Staff'
            }
          }
        }
      }
    });
  }
}