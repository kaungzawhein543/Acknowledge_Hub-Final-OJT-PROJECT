import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { CompanyService } from '../../services/company.service';
import { Company } from '../../models/Company';
import { ToastService } from '../../services/toast.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-add-company',
  templateUrl: './add-company.component.html',
  styleUrl: './add-company.component.css'
})
export class AddCompanyComponent {
  company: Company = {
    id: 0,
    name: ''
  };
  constructor(private companyService: CompanyService, private toastService: ToastService,    private router: Router,
  ) { }
  onSubmit(form: NgForm) {
    if (form.valid) {
      this.companyService.addCompany(this.company).subscribe({
        next: (data) => {
          console.log('successful')
          this.showSuccessToast();
          this.router.navigate(['/acknowledgeHub/company/list']); 

        },
        error: (e) => console.log(e)
      })
    }

  }
  showSuccessToast() {
    this.toastService.showToast('Company Created successful!', 'success');
  }
}
