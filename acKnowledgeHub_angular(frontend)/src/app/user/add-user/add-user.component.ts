

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

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css'
})
export class AddUserComponent implements OnInit {

  positions: Position[] = [];
  departments: Department[] = [];
  companies: Company[] = [];
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
    private departmentService: DepartmentService, private companyService: CompanyService) { }

    ngOnInit(): void {
      this.companyService.getAllCompany().subscribe({
        next: (data) => {
          this.companies = data;
          if (this.companies.length > 0) {
            this.staff.companyId = this.companies[0].id;
            this.getDepartmentsByCompanyId(this.staff.companyId);
          }
        },
        error: (e) => console.log(e)
      });
      this.positionService.getAllPosition().subscribe({
        next: (data) => {
          this.positions = data;
          if (this.positions.length > 0) {
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
      console.log(form)
      if (form.valid) {
        this.staffService.addStaff(this.staff).subscribe({
          next: (data) => {
            console.log("add staff is successful");
          },
          error: (e) => console.log(e)
        });
      }
  
    }
}