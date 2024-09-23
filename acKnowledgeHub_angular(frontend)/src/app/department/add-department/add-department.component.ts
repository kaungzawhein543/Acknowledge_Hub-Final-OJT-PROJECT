import { Component, OnInit } from '@angular/core';
import { DepartmentService } from '../../services/department.service';
import { CompanyService } from '../../services/company.service';
import { data } from 'jquery';
import { Company } from '../../models/Company';
import { Department } from '../../models/Department';
import { Form, NgForm } from '@angular/forms';
import { ToastService } from '../../services/toast.service';
import { Router } from '@angular/router';

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
  constructor(private departmentService: DepartmentService, 
    private companyService: CompanyService,
    private toastService: ToastService,
    private router: Router,

  ) { }

  showSuccessToast() {
    this.toastService.showToast(' Deperment created successful!', 'success');
  }

  ngOnInit(): void {
    this.companyService.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;
        this.department.company = this.companies[0];
      }
    })
  }

  onSubmit(form: NgForm) {
    console.log("company" + this.department.company.id)
    if (!this.department.company) {
      this.companyError = true;
      console.log('Company is required');
    } else {
      this.companyError = false;
      if (form.valid) {
        this.departmentService.addDepartment(this.department).subscribe({
          next: (data) => {
            console.log('successful')
            this.showSuccessToast();
            this.router.navigate(['department/list']); 

          },
          error: (e) => console.log(e)
        })
      }
    }
  }

}
