import { Component } from '@angular/core';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { Department } from '../../models/Department';
import { Company } from '../../models/Company';
import { DepartmentService } from '../../services/department.service';
import { CompanyService } from '../../services/company.service';
import { ToastService } from '../../services/toast.service';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
@Component({
  selector: 'app-update-department',
  templateUrl: './update-department.component.html',
  styleUrl: './update-department.component.css',
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
export class UpdateDepartmentComponent {
  companyError: boolean = false;
  department: Department = {
    id: 0,
    name: '',
    company: {} as Company
  };
  conflictError: string = "";
  companies!: Company[];
  departmentId !: number;
  constructor(private departmentService: DepartmentService,
    private companyService: CompanyService,
    private toastService: ToastService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  showSuccessToast() {
    this.toastService.showToast(' Deperment created successful!', 'success');
  }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      const decodedStringId = atob(params['id']);
      this.departmentId = parseInt(decodedStringId, 10);
    });

    // Fetch companies first
    this.companyService.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;

        // Fetch department after companies are available
        this.departmentService.getDepartmentById(this.departmentId).subscribe({
          next: (departmentData) => {
            this.department = departmentData;

            // Auto-select the company in the dropdown by matching company ID
            this.department.company = this.companies.find(
              company => company.id === departmentData.company.id
            )!; // Fallback to null if not found
          },
          error: (errorResponse: HttpErrorResponse) => {
            console.log('An error occurred:', errorResponse.message);
          }
        });
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
          this.departmentService.updateDepartment(this.department.id, this.department).subscribe({
            next: (data) => {
              this.toastService.showToast("Department update Successfully", 'success');
              this.router.navigate(['/acknowledgeHub/department/list']);
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
  }
  departmentIntput(): void {
    this.conflictError = '';
  }

}
