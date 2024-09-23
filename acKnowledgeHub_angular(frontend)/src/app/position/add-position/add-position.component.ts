import { Component } from '@angular/core';
import { Position } from '../../models/Position';
import { NgForm } from '@angular/forms';
import { PositionService } from '../../services/position.service';

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

  constructor(private positionService: PositionService) { }

  onSubmit(form: NgForm) {
    this.position.name = this.position.name.trim();
    if (this.position.name != '') {
      if (form.valid) {
        this.positionService.addPosition(this.position).subscribe({
          next: (data) => {
            console.log("successful");
          }, error: (e) => console.log(e)
        })
      }
    }
  }
}
