import { Component } from '@angular/core';
import { PositionService } from '../../services/position.service';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { Position } from '../../models/Position';

@Component({
  selector: 'app-update-position',
  templateUrl: './update-position.component.html',
  styleUrl: './update-position.component.css'
})
export class UpdatePositionComponent {
  conflictError: string = '';
  position: Position = {
    id: 0,
    name: ''
  }
  positionId !: number;
  showSuccessToast() {
    this.toastService.showToast('Position update   successfully!', 'success');
  }

  constructor(private positionService: PositionService, private router: Router, private toastService: ToastService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      const decodedStringId = atob(params['id']);
      this.positionId = parseInt(decodedStringId, 10);
    });
    this.positionService.getPositionById(this.positionId).subscribe({
      next: (data) => {
        this.position = data;
      }
    })
  }

  onSubmit(form: NgForm) {
    this.position.name = this.position.name.trim();
    if (this.position.name != '') {
      if (form.valid) {
        this.positionService.updatePosition(this.position.id, this.position).subscribe({
          next: (data) => {
            this.showSuccessToast();
            this.router.navigate(['/acknowledgeHub/position/list']);
          }, error: (errorResponse: HttpErrorResponse) => {
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

  updatePositionInput(): void {
    this.conflictError = '';
  }
}
