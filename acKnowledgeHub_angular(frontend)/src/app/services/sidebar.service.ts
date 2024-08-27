import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private sidebarState = new BehaviorSubject<boolean>(true);

  getSidebarState() {
    return this.sidebarState.asObservable();
  }

  toggle() {
    this.sidebarState.next(!this.sidebarState.getValue());
  }
}
