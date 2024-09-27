import { Component, ViewChild } from '@angular/core';
import { staffList } from '../../models/staff';
import { StaffService } from '../../services/staff.service';
import { Router } from '@angular/router';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { ToastService } from '../../services/toast.service';


@Component({
  selector: 'app-add-hr-main',
  templateUrl: './add-hr-main.component.html',
  styleUrl: './add-hr-main.component.css',
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
export class AddHRMainComponent {
  private staffToBeMainHR: number | null = null;
  staffHRList: staffList[] = [];
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  constructor(private staffService: StaffService, private router: Router,private toastService : ToastService) { }

  ngOnInit(): void {
    this.getHRStaffList();
  }

  private getHRStaffList(): void {
    this.staffService.getHRList().subscribe({
      next: (data) => {
        this.staffHRList = data;
        console.log(data);
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
        this.showSuccessToast();
        this.staffHRList = data;
      }
    })
  }
  showSuccessToast() {
    this.toastService.showToast('Add Human Resource (MAIN) successful!', 'success');
  }

}

