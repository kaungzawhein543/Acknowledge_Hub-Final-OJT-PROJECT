import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { CompanyService } from '../../services/company.service';
import { Company } from '../../models/Company';
import { HttpErrorResponse } from '@angular/common/http';


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
  constructor(private companyService: CompanyService) { }
  onSubmit(form: NgForm) {
    this.company.name = this.company.name.trim();
    if (this.company.name != '') {
      if (form.valid) {
        this.companyService.addCompany(this.company).subscribe({
          next: (data: string) => {
            console.log(data);
          },
          error: (errorResponse: HttpErrorResponse) => {
            if (errorResponse.status === 409) {
              console.log('Company already exists.');
            } else {
              console.log('An error occurred:', errorResponse.message);
            }
          }
        })
      }
    }

  }
}
