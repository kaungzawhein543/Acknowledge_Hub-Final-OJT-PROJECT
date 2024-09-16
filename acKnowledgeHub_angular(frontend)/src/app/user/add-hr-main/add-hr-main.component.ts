import { Component, ViewChild } from '@angular/core';
import { staffList } from '../../models/staff';
import { StaffService } from '../../services/staff.service';
import { Router } from '@angular/router';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';

@Component({
  selector: 'app-add-hr-main',
  templateUrl: './add-hr-main.component.html',
  styleUrl: './add-hr-main.component.css'
})
export class AddHRMainComponent {
  private staffToBeMainHR: number | null = null;
  staffHRList: staffList[] = [];
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  constructor(private staffService: StaffService, private router: Router) { }

  ngOnInit(): void {
    this.getHRStaffList();
  }

  private getHRStaffList(): void {
    this.staffService.getHRList().subscribe({
      next: (data) => {
        this.staffHRList = data;
      },
      error: (e) => console.error(e)
    });
  }

  openAddHRModal(itemId: number) {
    this.staffToBeMainHR = itemId;
    this.modal.open();
  }

  onDeleteConfirmed() {
    if (this.staffToBeMainHR) {
      this.addHRMain(this.staffToBeMainHR);
      this.staffToBeMainHR = null;
    }
  }

  addHRMain(staffId: number) {
    this.staffService.putHRMain(staffId).subscribe({
      next: (data) => {
        this.staffHRList = data;
      }
    })
  }

}

