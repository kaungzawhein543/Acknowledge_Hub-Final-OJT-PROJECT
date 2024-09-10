import { Component, OnInit } from '@angular/core';
import { DepartmentService } from '../../services/department.service';
import { CompanyService } from '../../services/company.service';
import { data } from 'jquery';
import { Company } from '../../models/Company';
import { Department } from '../../models/Department';
import { Form, NgForm } from '@angular/forms';

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
    if (this.department.company.id == undefined) {
      this.companyError = true;
    } else {
      this.companyError = false;
      if (form.valid) {
        this.departmentService.addDepartment(this.department).subscribe({
          next: (data) => {
            console.log('successful')
          },
          error: (e) => console.log(e)
        })
      }
    }
  }

}
