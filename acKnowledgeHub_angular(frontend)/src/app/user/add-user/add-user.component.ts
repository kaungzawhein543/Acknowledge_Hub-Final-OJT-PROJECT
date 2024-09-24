

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
      this.toastService.showToast('Staff Add Successfully!', 'success');
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
      this.getDepartmentsByCompanyId(this.staff.companyId);
    }
    this.staff.departmentId = 0; // Reset the department selection
  }

  getDepartmentsByCompanyId(companyId: number) {
    this.departmentService.getDepartmentListByCompanyId(companyId).subscribe({
      next: (data) => {
        this.departments = data;
        if (this.departments.length > 0) {
          this.staff.departmentId = this.departments[0].id;
        } else {
          console.log("There is no dapartments")
        }
      },
      error: (e) => console.log(e)
    });
  }

  onSubmit(form: NgForm): void {
    this.staff.companyStaffId = this.staff.companyStaffId?.trim();
    this.staff.name = this.staff.name?.trim();
    this.staff.email = this.staff.email?.trim();
    if (this.staff.companyStaffId != '' && this.staff.name != '' && this.staff.email != '') {
      if (form.valid) {
        this.staffService.addStaff(this.staff).subscribe({
          next: (data) => {
            this.toastService.showToast("Staff Add Successfully",'success');
            this.router.navigate(['/acknowledgeHub/users/list']);
          },
          error: (e) => console.log(e)
        });
      }
    }


  }
}