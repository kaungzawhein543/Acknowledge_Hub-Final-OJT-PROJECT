import { Component, OnInit } from '@angular/core';
import { DepartmentService } from '../../services/department.service';
import { CompanyService } from '../../services/company.service';
import { data } from 'jquery';
import { Company } from '../../models/Company';
import { Department } from '../../models/Department';
import { Form, NgForm } from '@angular/forms';
import { ToastService } from '../../services/toast.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-add-department',
  templateUrl: './add-department.component.html',
  styleUrl: './add-department.component.css',
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        query('.card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(200, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
          ])
        ]),
      ]),
    ]),
  ],
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
              this.toastService.showToast("Department Add Successfully",'success');
              this.router.navigate(['/acknowledgeHub/department/list']);
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
