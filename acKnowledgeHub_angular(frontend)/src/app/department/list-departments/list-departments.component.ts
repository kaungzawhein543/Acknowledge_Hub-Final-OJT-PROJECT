import { Component, ViewChild } from '@angular/core';
import { Department } from '../../models/Department';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { CompanyService } from '../../services/company.service';
import { Router } from '@angular/router';
import { DepartmentService } from '../../services/department.service';

@Component({
  selector: 'app-list-departments',
  templateUrl: './list-departments.component.html',
  styleUrl: './list-departments.component.css'
})
export class ListDepartmentsComponent {
  private itemIdToDelete: number | null = null;
  departments: Department[] = [];
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
        },
        error: (e) => console.error(e)
      });
  }
}
