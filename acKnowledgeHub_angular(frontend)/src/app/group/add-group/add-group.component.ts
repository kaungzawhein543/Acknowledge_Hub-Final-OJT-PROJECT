import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl } from '@angular/forms';
import { StaffGroup } from '../../models/staff-group';
import { Company } from '../../models/Company';
import { Department } from '../../models/Department';
import { MatSelectionList, MatSelectionListChange } from '@angular/material/list';
import { StaffService } from '../../services/staff.service';

@Component({
  selector: 'app-add-group',
  templateUrl: './add-group.component.html',
  styleUrls: ['./add-group.component.css']
})
export class AddGroupComponent {
  staffList: StaffGroup[] = [];
  selectedStaff: StaffGroup[] = [];
  companies: Company[] = [];
  departments: Department[] = [];
  filteredStaffList: StaffGroup[] = [];
  status: boolean = false;
  companystatus: number | undefined;
  departmentstatus: number | undefined;
  @ViewChild('staff') staff!: MatSelectionList;

  constructor(private service: StaffService) { }

  ngOnInit(): void {
    this.service.getAllCompany().subscribe({
      next: (data) => {
        this.companies = data;
      },
      error: (e) => console.log(e)
    });
    this.service.getDepartmentListByCompanyId(1).subscribe({
      next: (data) => {
        this.departments = data;
      },
      error: (e) => console.log(e)
    });
    this.service.getStaffList().subscribe({
      next: (data) => {
        this.staffList = data;
        this.showStaff(1);
      },
      error: (e) => console.log(e)
    });
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
      if (this.selectedStaff.length > 0) {
        this.status = true;
      } else {
        this.status = false;
      }
    });
  }

  showDepartment(id: number): void {
    this.service.getDepartmentListByCompanyId(id).subscribe({
      next: (data) => {
        this.departments = data;
        this.companystatus = id;
        if (this.departments.length > 0) {
          const firstDepartment = this.departments[0];
          this.showStaff(firstDepartment.id);
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
  }

  isSelected(staff: StaffGroup): boolean {
    return this.selectedStaff.some(s => s.staffId === staff.staffId);
  }

  get selectedStaffCount(): number {
    return this.selectedStaff.length;
  }

  getSelectedGroup() {
    console.log(this.selectedStaff)
  }

  showicon() {

  }
}
