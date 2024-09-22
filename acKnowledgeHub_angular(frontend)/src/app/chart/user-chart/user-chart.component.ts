import { Platform } from '@angular/cdk/platform';
import { isPlatformBrowser } from '@angular/common';
import { AfterViewInit, ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { ChartService } from '../../services/chart.service';

@Component({
  selector: 'app-user-chart',
  templateUrl: './user-chart.component.html',
  styleUrl: './user-chart.component.css'
})
export class UserChartComponent implements AfterViewInit {
  monthlyCount: any = {};  // This will hold the monthly count data
  newChartData: any = {};  // This will hold the new chart data
  startDate: string | null = null;
  endDate: string | null = null;
  chartInstance: Chart | null = null; // Keep track of the chart instance
  showNoData: boolean = false; // Added property to control visibility of "No data" message

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private chartService: ChartService,
    private cdr: ChangeDetectorRef // Inject ChangeDetectorRef
  ) {
    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadData();
    }
  }

  loadData(): void {
    this.chartService.getMonthlyAnnouncementCount().subscribe(data => {
      if (data && data.monthlyCount) {
        this.monthlyCount = data.monthlyCount;
        this.chartService.getAdditionalChartData().subscribe(data => {
          if (data && data.monthlyCount) {
            this.newChartData = data.monthlyCount;
            this.createCombinedChart();
          }
        });
      }
    });
  }

  getCurrentDate(): string {
    const currentDate = new Date();
    return currentDate.toISOString().slice(0, 10); // Format: yyyy-MM-dd
  }

  onStartDateChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.startDate = input.value;
    this.filterData();  // Trigger filtering and chart update
  }

  onEndDateChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.endDate = input.value;
    this.filterData();  // Trigger filtering and chart update
  }

  filterData(): void {
    if (this.startDate && this.endDate) {
      const filteredMonthlyCount = this.filterByDateRange(this.monthlyCount);
      const filteredNewChartData = this.filterByDateRange(this.newChartData);

      if (Object.keys(filteredMonthlyCount).length === 0 && Object.keys(filteredNewChartData).length === 0) {
        this.showNoData = true;
        this.destroyChart();
      } else {
        this.showNoData = false;
        this.createCombinedChart(filteredMonthlyCount, filteredNewChartData);
      }
    }
  }

  filterByDateRange(data: any): any {
    const start = new Date(this.startDate!);
    const end = new Date(this.endDate!);

    const filteredData: any = {};
    for (const key in data) {
      if (data.hasOwnProperty(key)) {
        const date = new Date(key);
        if (date >= start && date <= end) {
          filteredData[key] = data[key];
        }
      }
    }
    return filteredData;
  }

  createCombinedChart(monthlyCountData: any = this.monthlyCount, newChartData: any = this.newChartData): void {
    // Check if canvas element is rendered
    this.cdr.detectChanges();
    const canvas = document.getElementById('combinedChart') as HTMLCanvasElement | null;
  
    if (!canvas) {
      console.error('Canvas element not found');
      return;
    }
  
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      console.error('Failed to get canvas context');
      return;
    }
  
    // Destroy the existing chart instance if it exists
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  
    // Predefined color arrays for bars and lines
    const barColors = [
      'rgba(153, 102, 255, 0.2)',
      'rgba(255, 159, 64, 0.2)',
      'rgba(255, 99, 132, 0.2)',
      'rgba(75, 192, 192, 0.2)',
      'rgba(54, 162, 235, 0.2)',
      'rgba(255, 206, 86, 0.2)',
      'rgba(104, 132, 245, 0.2)'
    ];
  
    const barBorderColors = [
      'rgba(153, 102, 255, 1)',
      'rgba(255, 159, 64, 1)',
      'rgba(255, 99, 132, 1)',
      'rgba(75, 192, 192, 1)',
      'rgba(54, 162, 235, 1)',
      'rgba(255, 206, 86, 1)',
      'rgba(104, 132, 245, 1)'
    ];
  
    const lineColors = [
      'rgba(255, 99, 132, 0.2)',
      'rgba(54, 162, 235, 0.2)',
      'rgba(255, 206, 86, 0.2)',
      'rgba(75, 192, 192, 0.2)',
      'rgba(153, 102, 255, 0.2)',
      'rgba(255, 159, 64, 0.2)',
      'rgba(104, 132, 245, 0.2)'
    ];
  
    const lineBorderColors = [
      'rgba(255, 99, 132, 1)',
      'rgba(54, 162, 235, 1)',
      'rgba(255, 206, 86, 1)',
      'rgba(75, 192, 192, 1)',
      'rgba(153, 102, 255, 1)',
      'rgba(255, 159, 64, 1)',
      'rgba(104, 132, 245, 1)'
    ];
  
    // Ensure color arrays match the number of data points
    const barBackgroundColors = Object.keys(newChartData).map((_, index) => barColors[index % barColors.length]);
    const barBorderColorsArray = Object.keys(newChartData).map((_, index) => barBorderColors[index % barBorderColors.length]);
  
    const lineBackgroundColors = Object.keys(monthlyCountData).map((_, index) => lineColors[index % lineColors.length]);
    const lineBorderColorsArray = Object.keys(monthlyCountData).map((_, index) => lineBorderColors[index % lineBorderColors.length]);
  
    this.chartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: Object.keys(monthlyCountData),
        datasets: [
          {
            label: 'Announcements made this month',
            type: 'line',
            data: Object.values(monthlyCountData),
            backgroundColor: lineBackgroundColors,
            borderColor: lineBorderColorsArray,
            borderWidth: 2,
            fill: false,
            pointRadius: 5,
            tension: 0.3
          },
          {
            label: 'The number of noted announcements',
            type: 'bar',
            data: Object.values(newChartData),
            backgroundColor: barBackgroundColors,
            borderColor: barBorderColorsArray,
            borderWidth: 1
          }
        ]
      },
      options: {
        scales: {
          x: {
            title: {
              display: true,
              text: 'Month'
            },
            stacked: false
          },
          y: {
            title: {
              display: true,
              text: 'Noted Count'
            },
            beginAtZero: true,
            min: 0,
            max: 7
          }
        }
      }
    });
  }
  

  destroyChart(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
      this.chartInstance = null;
    }
  }
}