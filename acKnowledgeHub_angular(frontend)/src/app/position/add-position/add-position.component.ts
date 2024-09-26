import { Component } from '@angular/core';
import { Position } from '../../models/Position';
import { NgForm } from '@angular/forms';
import { PositionService } from '../../services/position.service';
import { Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-add-position',
  templateUrl: './add-position.component.html',
  styleUrl: './add-position.component.css',
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
export class AddPositionComponent {

  position: Position = {
    id: 0,
    name: ''
  }
  
  showSuccessToast() {
    this.toastService.showToast('Position created successful!', 'success');
  }

  constructor(private positionService: PositionService,private router: Router,private toastService: ToastService) { }

  onSubmit(form: NgForm) {
    this.position.name = this.position.name.trim();
    if (this.position.name != '') {
      if (form.valid) {
        this.positionService.addPosition(this.position).subscribe({
          next: (data) => {
            this.showSuccessToast();
            this.router.navigate(['/acknowledgeHub/position/list']); 
          }, error: (e) => console.log(e)
        })
      }
    }
  }
}
