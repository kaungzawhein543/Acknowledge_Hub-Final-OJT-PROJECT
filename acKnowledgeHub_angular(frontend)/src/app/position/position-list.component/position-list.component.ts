import { Component, ViewChild } from '@angular/core';
import { Position } from '../../models/Position'; 
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { PositionService } from '../../services/position.service';
import { Router } from '@angular/router';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';


@Component({
  selector: 'app-position-list',
  templateUrl: './position-list.component.html',
  styleUrl: './position-list.component.css',
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
export class PositionListComponent {
  private itemIdToDelete: number | null = null;
  positions: Position[] = [];
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  constructor(private positionService: PositionService, private router: Router) { }

  ngOnInit(): void {
    this.getPositions();
  }

  private getPositions(): void {
    this.positionService.getAllPosition()
      .subscribe({
        next: (data) => {
          this.positions = data;
          console.log(this.positions)
        },
        error: (e) => console.error(e)
      });
  }
}
