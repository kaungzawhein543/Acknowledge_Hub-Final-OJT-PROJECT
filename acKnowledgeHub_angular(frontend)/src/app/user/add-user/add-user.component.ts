

import { Component, OnInit } from '@angular/core';
import { StaffService } from '../../services/staff.service';
import { PositionService } from '../../services/position.service';
import { DepartmentService } from '../../services/department.service';
import { CompanyService } from '../../services/company.service';
import { Position } from '../../models/Position';
import { Department } from '../../models/Department';
import { Company } from '../../models/Company';
import { Role } from '../../models/ROLE';
import { NgForm } from '@angular/forms';
import { AddStaff } from '../../models/addStaff';
import { ToastService } from '../../services/toast.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css'
})
export class AddUserComponent implements OnInit {
  positions: Position[] = [];
  departments: Department[] = [];
  companies: Company[] = [];
  filteredDepartments: Department[] = [];
  staff: AddStaff = {
    companyStaffId: '',
    email: '',
    name: '',
    role: Role.USER,
    positionId: 0,
    departmentId: 0,
    companyId: 0
  }
  roles = Object.values(Role);
  constructor(private staffService: StaffService, private positionService: PositionService,
    private departmentService: DepartmentService, private companyService: CompanyService,
    private toastService: ToastService,
    private router: Router,) { }

    showSuccessToast() {
      this.toastService.showToast('Staff successful!', 'success');
    }

  ngOnInit(): void {
    this.departmentService.getAllDepartments().subscribe({
      next: (data) => {
        this.departments = data;
        if(this.departments.length >0){
          this.staff.departmentId = this.departments[0].id;
        }else{
          console.log("There is no dapartments")
        }
      },
      error: (e) => console.log(e)
    });
    this.companyService.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;
        if(this.companies.length >0){
          this.staff.companyId = this.companies[0].id;
        }
      },
      error: (e) => console.log(e)
    });
    this.positionService.getAllPosition().subscribe({
      next: (data) => {
        this.positions = data;
        if(this.positions.length >0){
          this.staff.positionId = this.positions[0].id;
        }
      },
      error: (e) => console.log(e)
    });
  }
  onCompanyChange(): void {
    if (this.staff.companyId) {
      this.filteredDepartments = this.departments.filter(department => department.company.id === this.staff.companyId);
    } else {
      this.filteredDepartments = [];
    }
    this.staff.departmentId = 0; // Reset the department selection
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.staffService.addStaff(this.staff).subscribe({
        next: (data) => {
          console.log("Add staff is successful");
          this.showSuccessToast();
          this.router.navigate(['/acknowledgeHub/users/list']);
        },
        error: (error) => {
          console.log("Error occurred while adding staff:", error);
          if (error instanceof HttpErrorResponse) {
            console.log("HttpErrorResponse details:", error);
          }
        }
      });
    }
  }
  
  
}