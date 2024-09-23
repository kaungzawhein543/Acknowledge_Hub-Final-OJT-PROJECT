import { Component, ViewChild } from '@angular/core';
import { Position } from '../../models/Position';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { PositionService } from '../../services/position.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-position-list',
  templateUrl: './position-list.component.html',
  styleUrl: './position-list.component.css'
})
export class PositionListComponent {
  private itemIdToDelete: number | null = null;
  positions: Position[] = [];
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  constructor(private positionService: PositionService, private router: Router) { }

  ngOnInit(): void {
    this.getDepartments();
  }

  private getDepartments(): void {
    this.positionService.getAllPosition()
      .subscribe({
        next: (data) => {
          this.positions = data;
        },
        error: (e) => console.error(e)
      });
  }
}
