import { Component, EventEmitter, Output, Input } from '@angular/core';

@Component({
  selector: 'app-confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.css']
})
export class ConfirmationModalComponent {
  isOpen = false;
  reason = '';
  //@Output() confirmed = new EventEmitter<void>();
  @Output() confirmed = new EventEmitter<{ reason: string }>();
  @Input() message = 'Are you sure you want to proceed?';
  @Input() confirmAction: (() => void) | null = null;
  @Input() icon = 'fa-solid fa-triangle-exclamation';
  @Input() showReasonInput = false;
  @Input() confirmButtonClass = 'bg-red-600 hover:bg-red-700';
  open(action: (() => void) | null = null) {
    this.isOpen = true;
    this.confirmAction = action;
    this.reason = '';
  }

  close() {
    this.isOpen = false;
  }

  confirm() {
    if (this.confirmAction) {
      this.confirmAction();
    }
    this.isOpen = false;
    // this.confirmed.emit();
    this.confirmed.emit({ reason: this.reason });
  }
}