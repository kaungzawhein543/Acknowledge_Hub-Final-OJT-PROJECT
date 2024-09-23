import { Component } from '@angular/core';
import { Group } from '../../models/Group';
import { GroupService } from '../../services/group.service';
import { ToastService } from '../../services/toast.service';
import { StaffGroup } from '../../models/staff-group';

@Component({
  selector: 'app-list-group',
  templateUrl: './list-group.component.html',
  styleUrl: './list-group.component.css'
})
export class ListGroupComponent {
  groups: Group[] = [];
  showConfirmBox : boolean = false;
  errorMessage: string | null = null;
  selectedGroup: Group = {
    id: 0,
    name: '',
    status: '',
    createdAt: new Date(),  // Default to current date
    staff: [],              // Empty array for staff
    selected: false         // Default boolean value
  };
  filterStaffListAfterSelect : StaffGroup[] = [];
  staffSearchTermConfirm : string = '';
  selectedStaff: StaffGroup[] = [];
  staffPhotoUrl: string | null = null;

  
  
  constructor(private groupService: GroupService, private toastService: ToastService) {}
  
  ngOnInit(): void {
    this.loadGroups();
  }
  
  showStaffs(group: any): void {
    this.selectedGroup = group;
    this.selectedGroup.staff = group.staff.map((staff: StaffGroup) => {
      this.staffPhotoUrl = "http://localhost:8080" + staff.photoPath + "?"+ Date.now();
      console.log(staff.photoPath);
      return staff; // Return the modified staff object
    });    this.filterStaffListAfterSelect = this.selectedGroup.staff;
    this.showConfirmBox = !this.showConfirmBox;
  }


  showSelectedStaff(): void {
    this.showConfirmBox = !this.showConfirmBox;
  }

  loadGroups(): void {
    this.groupService.getAllGroups().subscribe(
      groups => this.groups = groups,
      error => this.handleError(error)
    );
  }

  filterStaffAfterSelect(): void {
    const term = this.staffSearchTermConfirm.toLowerCase();
    this.filterStaffListAfterSelect = this.selectedGroup.staff.filter(staff =>
      staff.name.toLowerCase().includes(term) ||
      staff.position.name.toLowerCase().includes(term) ||
      staff.staffId.toString().includes(term) ||
      staff.department.name.toLowerCase().includes(term) ||
      staff.company.name.toLowerCase().includes(term)
    );

  }

  deleteGroup(id: number): void {
    this.groupService.deleteGroup(id).subscribe(
      () => {
        this.groups = this.groups.filter(group => group.id !== id);
        this.toastService.showToast('Group deleted successfully!', 'success');
      },
      (error: any) => this.handleError(error)
    );
  }

  private handleError(error: any): void {
    let errorMessage = 'An unknown error occurred.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      errorMessage = `Server-side error: ${error.status} ${error.message}`;
    }
    console.error(errorMessage);
    this.errorMessage = errorMessage;
    this.toastService.showToast('An error occurred!', 'error');
  }
}
