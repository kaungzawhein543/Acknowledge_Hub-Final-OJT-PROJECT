import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { CompanyService } from '../../services/company.service';
import { Company } from '../../models/Company';
import { ToastService } from '../../services/toast.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';



@Component({
  selector: 'app-add-company',
  templateUrl: './add-company.component.html',
  styleUrl: './add-company.component.css',
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
export class AddCompanyComponent {
  company: Company = {
    id: 0,
    name: ''
  };
  conflictError : string = '';
  constructor(private companyService: CompanyService, private toastService: ToastService,    private router: Router,
  ) { }

  onSubmit(form: NgForm) {
    this.company.name = this.company.name.trim();
    if (this.company.name != '') {
      if (form.valid) {
        this.companyService.addCompany(this.company).subscribe({
          next: (data: string) => {
            this.toastService.showToast("Company Add Successfully",'success');
            this.router.navigate(['/acknowledgeHub/company/list']);
          },
          error: (errorResponse: HttpErrorResponse) => {
            if (errorResponse.status === 409) {
              this.conflictError = errorResponse.error;
            } else {
              console.log('An error occurred:', errorResponse.message);
            }
          }
        })
      }
    }
  }
  companyIntput():void{
    this.conflictError = '';
  }
  showSuccessToast() {
    this.toastService.showToast('Company Created successful!', 'success');
  }
}
