import { Component, ViewChild } from '@angular/core';
import { StaffGroup } from '../../models/staff-group';
import { Company } from '../../models/Company';
import { Department } from '../../models/Department';
import { MatSelectionList, MatSelectionListChange } from '@angular/material/list';
import { StaffService } from '../../services/staff.service';
import { CompanyService } from '../../services/company.service';
import { DepartmentService } from '../../services/department.service';
import { GroupService } from '../../services/group.service';
import { error } from 'console';
import { ToastService } from '../../services/toast.service';
import { map } from 'rxjs';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { Router } from '@angular/router';


@Component({
  selector: 'app-add-group',
  templateUrl: './add-group.component.html',
  styleUrls: ['./add-group.component.css'],
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
export class AddGroupComponent {
  companySearchTerm: string = '';
  departmentSearchTerm: string = '';
  staffSearchTerm: string = '';
  staffSearchTermConfirm : string = '';
  groupName: string = '';
  validationError: string = '';
  selectAll: boolean = false;
  
  companies: Company[] = [];
  filteredCompanies: Company[] = [];

  departments: Department[] = [];
  filteredDepartments: Department[] = [];

  staffList: StaffGroup[] = [];
  filteredStaffList: StaffGroup[] = [];
  selectedStaff: StaffGroup[] = [];
  filterStaffListAfterSelect : StaffGroup[] = [];

  companystatus: number | undefined;
  departmentstatus: number | undefined;
  status: boolean = false;
  showConfirmBox : boolean = false;
  staffForDelete !: StaffGroup;
  @ViewChild('staff') staff!: MatSelectionList;
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;

  //for loading
  loadingCompanies = true;
  loadingDepartments = true;
  loadingStaff = true;

  constructor(private companyService: CompanyService, private departmentService: DepartmentService, private staffService: StaffService, private groupService: GroupService, private toastService: ToastService,private router: Router) { }

  ngOnInit(): void {
    this.filterStaffListAfterSelect = [...this.selectedStaff];
    this.companyService.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;
        this.filteredCompanies = data;
        this.companystatus = data[0].id;
        this.loadingCompanies = false;
      },
      error: (e) => console.log(e)
    });
    this.departmentService.getDepartmentListByCompanyId(1).subscribe({
      next: (data) => {
        this.departments = data;
        this.filteredDepartments = data;
        this.departmentstatus = data[0].id;
        this.loadingDepartments = false;
      },
      error: (e) => {
        console.log(e)
        this.showErrorToast();
      }
    });
    this.staffService.getStaffList().pipe(
      map((data: any[]) =>
        data.map(staff => ({
          ...staff,
          photoPath: staff.photoPath ? `http://localhost:8080${staff.photoPath}?${Date.now()}` : ''
        }))
      )
    ).subscribe({
      next: (data) => {

        console.log(data);
        this.staffList = data;
        this.showStaff(1);
        this.loadingStaff = false;
      },
      error: (e) => {
        console.log(e);
      }
    });
  }

  filterCompany(): void {
    const term = this.companySearchTerm.toLowerCase();
    this.filteredCompanies = this.companies.filter(company =>
      company.name.toLowerCase().includes(term)
    );
  }
  showSelectedStaff(): void {
    if(this.groupName.length === 0 || this.groupName.length <= 3 ){
      this.validationError = this.validateGroupName();
      console.log("Error")
      return;
    }
    this.showConfirmBox = !this.showConfirmBox;
    this.filterStaffListAfterSelect = [...this.selectedStaff]; // Initialize filtered list
  }

  filterDepartment(): void {
    const term = this.departmentSearchTerm.toLowerCase();
    this.filteredDepartments = this.departments.filter(department =>
      department.name.toLowerCase().includes(term)
    );
  }

  filterStaff(): void {
    const term = this.staffSearchTerm.toLowerCase();
    this.filteredStaffList = this.staffList.filter(staff =>
      staff.department.id === this.departmentstatus &&
      (staff.name.toLowerCase().includes(term) || staff.position.name.toLowerCase().includes(term))
    );
    this.updateSelectAllState();
  }

  filterStaffAfterSelect(): void {
    const term = this.staffSearchTermConfirm.toLowerCase();

    // Filter the selected staff based on search term
    this.filterStaffListAfterSelect = this.selectedStaff.filter(staff =>
      staff.name.toLowerCase().includes(term) ||
      staff.position.name.toLowerCase().includes(term) ||
      staff.staffId.toString().includes(term) ||
      staff.department.name.toLowerCase().includes(term) ||
      staff.company.name.toLowerCase().includes(term)
    );

    // Log for debugging
    console.log('Filtered staff:', this.filterStaffListAfterSelect);
  }

  
  toggleSelection(event: MatSelectionListChange): void {
    event.options.forEach(option => {
      const staff = option.value;
      const index = this.selectedStaff.findIndex(s => s.staffId === staff.staffId);
      if (index > -1) {
        this.selectedStaff.splice(index, 1);
      } else {
        this.selectedStaff.push(staff);
      }
      this.status = this.selectedStaff.length > 0;
    });
    this.updateSelectAllState();
  }

  showDepartment(id: number): void {
    this.departmentService.getDepartmentListByCompanyId(id).subscribe({
      next: (data) => {
        this.departments = data;
        this.filteredDepartments = data;
        this.companystatus = id;
        if (this.departments.length > 0) {
          this.showStaff(this.departments[0].id);
        }
      },
      error: (e) => console.log(e)
    });
  }

  showStaff(departmentId: number): void {
    this.filteredStaffList = this.staffList.filter(
      staff => staff.department.id === departmentId
    );

    setTimeout(() => {
      this.staff.options.forEach(option => {
        if (this.selectedStaff.some(s => s.staffId === option.value.staffId)) {
          option.selected = true;
        }
        this.departmentstatus = departmentId;
      });
    }, 0);
    this.updateSelectAllState();
  }

  toggleSelectAll(): void {
    if (this.selectAll) {
      // Select all filtered staff
      this.filteredStaffList.forEach(staff => {
        if (!this.isSelected(staff)) {
          this.selectedStaff.push(staff);
        }
      });
    } else {
      // Deselect all filtered staff
      this.filteredStaffList.forEach(staff => {
        const index = this.selectedStaff.findIndex(s => s.staffId === staff.staffId);
        if (index > -1) {
          this.selectedStaff.splice(index, 1);
        }
      });
    }
    this.staff.options.forEach(option => {
      option.selected = this.selectAll;
    });
    this.status = this.selectedStaff.length > 0;
  }

  isSelected(staff: StaffGroup): boolean {
    return this.selectedStaff.some(s => s.staffId === staff.staffId);
  }

  updateSelectAllState(): void {
    this.selectAll = this.filteredStaffList.every(staff => this.isSelected(staff));
  }

  get selectedStaffCount(): number {
    return this.selectedStaff.length;
  }

  getSelectedGroup(): number[] {
    const selectedStaffIds = this.selectedStaff.map(staff => staff.staffId);
    this.validationError = this.validateGroupName(); // Update validationError property
    this.showConfirmBox = false;
    if (this.validationError) {
      console.error(this.validationError);
    } else {
      this.groupService.createGroup(selectedStaffIds, this.groupName).subscribe(
        data => {
          this.showSuccessToast();
          this.router.navigate(["/acknowledgeHub/group/list"])
        },
        (error: Error) => {
          console.log(error);
        }
      );
    }
    return selectedStaffIds;
  }

  validateGroupName(): string {
    if (!this.groupName) {
      return 'Group name is required';
    } else if (this.groupName.length < 3) {
      return 'Group name must be at least 3 characters long';
    }
    return '';
  }

  onGroupNameChange(): void {
    this.validationError = this.validateGroupName(); // Update validationError on input change
  }

  showSuccessToast() {
    this.toastService.showToast('Add Group  successful!', 'success');
  }

  showErrorToast() {
    this.toastService.showToast('An error occurred!', 'error');
  }

  showInfoToast() {
    this.toastService.showToast('Here is some information.', 'info');
  }

  removeStaff(): void {
   if(this.staffForDelete){
     // Find and remove the staff from the selectedStaff array
     this.selectedStaff = this.selectedStaff.filter(s => s.staffId !== this.staffForDelete.staffId);
     this.filterStaffAfterSelect(); // Reapply filter to update the displayed staff list
   }
  }
  openDeleteModal(staff: StaffGroup) {
    this.staffForDelete = staff;
    this.modal.open();
  }
}
