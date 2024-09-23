import { Component } from '@angular/core';
import { Position } from '../../models/Position';
import { NgForm } from '@angular/forms';
import { PositionService } from '../../services/position.service';
import { Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-add-position',
  templateUrl: './add-position.component.html',
  styleUrl: './add-position.component.css'
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
