import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { StaffSummaryCount } from '../../models/staff';
import { StaffService } from '../../services/staff.service';
import { Chart, registerables } from 'chart.js';

@Component({
  selector: 'app-staff-chart',
  templateUrl: './staff-chart.component.html',
  styleUrl: './staff-chart.component.css',
})
export class StaffChartComponent implements AfterViewInit{

  @ViewChild('pieChart') pieChart: ElementRef | undefined ;

  staffSummaryCount: StaffSummaryCount | undefined;

  constructor(private staffService:StaffService){    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    this.loadStaffSummaryCount();
  }

  loadStaffSummaryCount(): void {
    this.staffService.getStaffCount().subscribe((data: StaffSummaryCount) => {
      this.staffSummaryCount = data;
      this.createPieChart();
    }, (error) => {
      console.error('Error ', error);
    });
  }

  createPieChart(): void {
    const ctx = this.pieChart?.nativeElement?.getContext('2d');
    if (!ctx) return; // or throw an error if ctx is null
  
    const chart = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: ['Total Staff', 'Active Staff', 'Inactive Staff'],
        datasets: [{
          label: 'Staff Summary',
          data: [
            this.staffSummaryCount?.totalStaff ?? 0,
            this.staffSummaryCount?.activeStaff ?? 0,
            this.staffSummaryCount?.inactiveStaff ?? 0
          ],
          backgroundColor: [
            'rgba(255, 206, 86, 0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(255, 99, 132, 0.2)'
            
          ],
          borderColor: [
            'rgba(255, 206, 86, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 99, 132, 1)'

          ],
          borderWidth: 1
        }]
      },
      options: {
        plugins: {
          title: {
            display: true,
            text: 'User Activity'
          },
          legend: {
            display: true,
            position: 'bottom' // This will move the legend to the bottom
          }
        }
      }
    });
  }
}
