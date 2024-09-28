import { Component, OnInit } from '@angular/core';
import { Company } from '../../models/Company';
import { CompanyService } from '../../services/company.service';
import { ToastService } from '../../services/toast.service';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-update-company',
  templateUrl: './update-company.component.html',
  styleUrl: './update-company.component.css',
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
export class UpdateCompanyComponent implements OnInit {
  company: Company = {
    id: 0,
    name: ''
  };
  companyId !: number;
  conflictError: string = '';
  constructor(private companyService: CompanyService, private toastService: ToastService, private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      const decodedStringId = atob(params['id']);
      this.companyId = parseInt(decodedStringId, 10);
    })
    this.companyService.getCompanyById(this.companyId).subscribe({
      next: (data) => {
        this.company = data;
      }
    })
  }
  onSubmit(form: NgForm) {
    this.company.id = this.company.id;
    this.company.name = this.company.name.trim();
    if (this.company.name != '') {
      if (form.valid) {
        this.companyService.updateCompany(this.company.id, this.company.name).subscribe({
          next: (data) => {
            this.toastService.showToast("Company Update Successfully", 'success');
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
  companyIntput(): void {
    this.conflictError = '';
  }
  showSuccessToast() {
    this.toastService.showToast('Company update successful!', 'success');
  }
}
