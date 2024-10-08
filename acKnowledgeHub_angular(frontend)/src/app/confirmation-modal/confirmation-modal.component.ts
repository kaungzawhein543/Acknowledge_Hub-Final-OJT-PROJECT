import { Component, EventEmitter, Output, Input } from '@angular/core';

@Component({
  selector: 'app-confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.css']
})
export class ConfirmationModalComponent {
  isOpen = false;
  @Output() confirmed = new EventEmitter<void>();
  @Input() message = 'Are you sure you want to proceed?';
  @Input() confirmAction: (() => void) | null = null;

  open(action: (() => void) | null = null) {
    this.isOpen = true;
    this.confirmAction = action;
  }

  close() {
    this.isOpen = false;
  }

  confirm() {
    if (this.confirmAction) {
      this.confirmAction();
    }
    this.isOpen = false;
    this.confirmed.emit();
  }
}
