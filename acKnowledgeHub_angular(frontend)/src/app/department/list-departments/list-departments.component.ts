import { Component, ViewChild } from '@angular/core';
import { Department } from '../../models/Department';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { CompanyService } from '../../services/company.service';
import { Router } from '@angular/router';
import { DepartmentService } from '../../services/department.service';

@Component({
  selector: 'app-list-departments',
  templateUrl: './list-departments.component.html',
  styleUrls: ['./list-departments.component.css'] // Fixed styleUrls typo
})
export class ListDepartmentsComponent {
  private itemIdToDelete: number | null = null;
  departments: Department[] = [];
  groupedDepartments: { companyName: string, departments: Department[] }[] = [];
  
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;

  constructor(private departmentService: DepartmentService, private router: Router) { }

  ngOnInit(): void {
    this.getDepartments();
  }

  private getDepartments(): void {
    this.departmentService.getAllDepartments()
      .subscribe({
        next: (data) => {
          this.departments = data;
          this.groupDepartmentsByCompany();
        },
        error: (e) => console.error(e)
      });
  }

  private groupDepartmentsByCompany(): void {
    const grouped = this.departments.reduce((acc, department) => {
      const companyName = department.company.name;
      if (!acc[companyName]) {
        acc[companyName] = [];
      }
      acc[companyName].push(department);
      return acc;
    }, {} as { [key: string]: Department[] });

    this.groupedDepartments = Object.keys(grouped).map(companyName => ({
      companyName,
      departments: grouped[companyName]
    }));
  }
}
