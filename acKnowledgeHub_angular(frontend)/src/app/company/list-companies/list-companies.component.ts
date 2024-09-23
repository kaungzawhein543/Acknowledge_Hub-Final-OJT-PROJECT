import { Component, OnInit, ViewChild } from '@angular/core';
import { CompanyService } from '../../services/company.service';
import { ToastService } from '../../services/toast.service';
import { Company } from '../../models/Company';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { CategoryService } from '../../services/category.service';
import { Router } from '@angular/router';
import { DepartmentService } from '../../services/department.service';
import { error } from 'console';
import { Department } from '../../models/Department';

@Component({
  selector: 'app-list-companies',
  templateUrl: './list-companies.component.html',
  styleUrl: './list-companies.component.css'
})
export class ListCompaniesComponent implements OnInit {
  private itemIdToDelete: number | null = null;
  companies: Company[] = [];
  selectedDepartment: Department[] | null = null;
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  constructor(private companyService: CompanyService, private router: Router, private departmentService: DepartmentService) { }

  ngOnInit(): void {
    this.getCategories();
  }

  private getCategories(): void {
    this.companyService.getAllCompany()
      .subscribe({
        next: (data) => {
          this.companies = data;
        },
        error: (e) => console.error(e)
      });
  }


}
