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


@Component({
  selector: 'app-add-group',
  templateUrl: './add-group.component.html',
  styleUrls: ['./add-group.component.css']
})
export class AddGroupComponent {
  companySearchTerm: string = '';
  departmentSearchTerm: string = '';
  staffSearchTerm: string = '';
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

  companystatus: number | undefined;
  departmentstatus: number | undefined;
  status: boolean = false;

  @ViewChild('staff') staff!: MatSelectionList;

  constructor(private companyService: CompanyService, private departmentService: DepartmentService, private staffService: StaffService, private groupService: GroupService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.companyService.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;
        this.filteredCompanies = data;
        // Select the first company by default
        this.companystatus = data[0].id;
      },
      error: (e) => console.log(e)
    });
    this.departmentService.getDepartmentListByCompanyId(1).subscribe({
      next: (data) => {
        this.departments = data;
        this.filteredDepartments = data;
        // Select the first department by default
        this.departmentstatus = data[0].id;
      },
      error: (e) => {
        console.log(e)
        this.showErrorToast();
      }
    });
    this.staffService.getStaffList().subscribe({
      next: (data) => {
        this.staffList = data;
        this.showStaff(1);
      },
      error: (e) => {
        console.log(e)
      }
    });
  }

  filterCompany(): void {
    const term = this.companySearchTerm.toLowerCase();
    this.filteredCompanies = this.companies.filter(company =>
      company.name.toLowerCase().includes(term)
    );
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
      (staff.name.toLowerCase().includes(term) || staff.position.toLowerCase().includes(term))
    );
    this.updateSelectAllState();
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
    console.log(selectedStaffIds);
    this.validationError = this.validateGroupName(); // Update validationError property

    if (this.validationError) {
      console.error(this.validationError);
    } else {
      this.groupService.createGroup(selectedStaffIds, this.groupName).subscribe(
        data => {
          console.log(data);
          this.showSuccessToast();
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
}
