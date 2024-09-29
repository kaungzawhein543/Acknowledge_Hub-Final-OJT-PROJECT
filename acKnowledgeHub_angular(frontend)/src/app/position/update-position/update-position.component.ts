import { Component, OnInit } from '@angular/core';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { Position } from '../../models/Position';
import { PositionService } from '../../services/position.service';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-update-position',
  templateUrl: './update-position.component.html',
  styleUrl: './update-position.component.css',
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
export class UpdatePositionComponent implements OnInit {
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
