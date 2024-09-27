import { Component, HostListener, OnInit } from '@angular/core';
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
import { ToastService } from '../../services/toast.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import e from 'express';


@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css',
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
export class AddUserComponent implements OnInit {
  positions: Position[] = [];
  departments: Department[] = [];
  companies: Company[] = [];
  filteredDepartments: Department[] = [];
  staff: AddStaff = {
    companyStaffId: '',
    email: '',
    name: '',
    role: Role.USER,
    positionId: 0,
    departmentId: 0,
    companyId: 0
  }
  emailFormatError : boolean = false;
  emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
  roles = Object.values(Role);
  alreadyExistStaffId : string = '';
  conflictStaffIdMessage : string = '';
  alreadyExistStaffEmail : string = '';
  conflictEmaildMessage : string = '';
  isDropdownOpen = false;
  
  constructor(private staffService: StaffService, private positionService: PositionService,
    private departmentService: DepartmentService, private companyService: CompanyService,
    private toastService: ToastService,
    private router: Router,) { }

    showSuccessToast() {
      this.toastService.showToast('Staff Add Successfully!', 'success');
    }

  ngOnInit(): void {
    this.departmentService.getDepartmentListByCompanyId(1).subscribe({
      next: (data) => {
        this.departments = data;
        if(this.departments.length >0){
          this.staff.departmentId = this.departments[0].id;
        }else{
          console.log("There is no dapartments")
        }
      },
      error: (e) => console.log(e)
    });
    this.companyService.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;
        if(this.companies.length >0){
          this.staff.companyId = this.companies[0].id;
        }
      },
      error: (e) => console.log(e)
    });
    this.positionService.getAllPosition().subscribe({
      next: (data) => {
        this.positions = data;
        if(this.positions.length >0){
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
    
    this.staff.companyStaffId = this.staff.companyStaffId?.trim();
    this.staff.name = this.staff.name?.trim();
    this.staff.email = this.staff.email?.trim() || '';

    const isEmailValid = this.emailRegex.test(this.staff.email);

    if (this.staff.companyStaffId != '' && this.staff.name != '' && this.staff.email != '' && isEmailValid) {
      if (form.valid) {
        this.staffService.addStaff(this.staff).subscribe({
          next: (data) => {
            this.toastService.showToast("Staff Add Successfully",'success');
            this.router.navigate(['/acknowledgeHub/users/list']);
          },
          error: (error: HttpErrorResponse) => {
            // Handle 409 Conflict error
            console.log(error)
            if (error.status === 409) {
              if(error.error === "StaffId is already exist!"){
                this.alreadyExistStaffId = this.staff.companyStaffId || '';
                this.conflictStaffIdMessage = error.error || "Conflict occurred. Please try again.";
              }else if(error.error === "Email is already exist!"){
                this.alreadyExistStaffEmail = this.staff.email || '';
                this.conflictEmaildMessage = error.error || "Conflict occurred. Please try again.";
              }
            } else {
              // Handle other errors
              this.toastService.showToast("An error occurred. Please try again.", 'error');
              console.error("Error:", error);
            }
          }
        });
      }
    }else if(!isEmailValid){
      this.emailFormatError = true;
    }else{
      console.log(isEmailValid)
    }
  }
  onEmailInput(): void {
    // Trim the email input to avoid leading/trailing spaces
    const trimmedEmail = this.staff.email?.trim() || '';
  
    // Check if the email is provided and validate its format
    this.emailFormatError = !this.emailRegex.test(trimmedEmail);
  
    if (this.emailFormatError) {
      // If email format is invalid, clear the conflict message
      this.conflictEmaildMessage = "";
    } else {
      // Only check for conflicts if the email format is valid
      if (this.alreadyExistStaffEmail?.trim() === trimmedEmail) {
        this.conflictEmaildMessage = "Email already exists!";
        console.log('same');
      } else {
        this.conflictEmaildMessage = ""; // Clear message if no conflict
      }
    }
  }
  
  onStaffIdInput(): void {
    if (this.alreadyExistStaffId !== '' && this.staff.companyStaffId !== undefined) {
      // Check if alreadyExistStaffId matches the staff.companyStaffId
      if(this.alreadyExistStaffId === this.staff.companyStaffId){
        this.conflictStaffIdMessage = 'StaffId is already exist!';
      }else{
        this.conflictStaffIdMessage = '';
      }
    }
  }
  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  @HostListener('document:click', ['$event'])
  closeDropdownOnClickOutside(event: Event) {
    const clickedInsideDropdown = (event.target as HTMLElement).closest('.relative');
    if (!clickedInsideDropdown) {
      this.isDropdownOpen = false;
    }
  }
}