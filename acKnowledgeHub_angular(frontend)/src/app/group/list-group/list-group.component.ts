import { Component } from '@angular/core';
import { Group } from '../../models/Group';
import { GroupService } from '../../services/group.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-list-group',
  templateUrl: './list-group.component.html',
  styleUrl: './list-group.component.css'
})
export class ListGroupComponent {
  groups: Group[] = [];
  errorMessage: string | null = null;
  selectedGroup: any = null;  // Store the group for which the modal is displayed

  constructor(private groupService: GroupService, private toastService: ToastService) {}

  ngOnInit(): void {
    this.loadGroups();
  }

  showStaffs(group: any): void {
    this.selectedGroup = group;
  }

  closeModal(): void {
    this.selectedGroup = null;
  }

  loadGroups(): void {
    this.groupService.getAllGroups().subscribe(
      groups => {this.groups = groups,console.log(groups)},
      error => this.handleError(error)
    );
  }

  editGroup(group: Group): void {
    // Implement logic to edit the group
    // For example, open a modal with a form to edit the group
  }

  deleteGroup(id: number): void {
    this.groupService.deleteGroup(id).subscribe(
      () => {
        this.groups = this.groups.filter(group => group.id !== id);
        this.toastService.showToast('Group deleted successfully!', 'success');
      },
      (      error: any) => this.handleError(error)
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
