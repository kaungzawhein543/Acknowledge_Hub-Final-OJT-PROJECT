import { Component, OnInit } from '@angular/core';
import { DepartmentService } from '../../services/department.service';
import { CompanyService } from '../../services/company.service';
import { data } from 'jquery';
import { Company } from '../../models/Company';
import { Department } from '../../models/Department';
import { Form, NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-department',
  templateUrl: './add-department.component.html',
  styleUrl: './add-department.component.css'
})
export class AddDepartmentComponent implements OnInit {
  companyError: boolean = false;
  department: Department = {
    id: 0,
    name: '',
    company: {} as Company
  };
  companies!: Company[];
  constructor(private departmentService: DepartmentService, private companyService: CompanyService) { }

  ngOnInit(): void {
    this.companyService.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;
        if (this.companies.length > 0) {
          this.department.company = this.companies[0];
        }
      },
      error: (e) => console.log(e)
    });
  }

  onCompanyChange() {
    if (this.department.company && this.department.company.id) {
      this.companyError = false;
    }
  }

  onSubmit(form: NgForm) {
    this.department.name = this.department.name.trim();
    if (this.department.name != '') {
      if (this.department.company.id == undefined) {
        this.companyError = true;
      } else {
        this.companyError = false;
        if (form.valid) {
          this.departmentService.addDepartment(this.department).subscribe({
            next: (data) => {
              console.log('successful')
            },
            error: (errorResponse: HttpErrorResponse) => {
              if (errorResponse.status === 409) {
                console.log('Department already exists.');
              } else {
                console.log('An error occurred:', errorResponse.message);
              }
            }
          })
        }
      }
    }
  }

}
