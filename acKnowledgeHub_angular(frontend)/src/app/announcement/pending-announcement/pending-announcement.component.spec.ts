import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingAnnouncementComponent } from './pending-announcement.component';

describe('PendingAnnouncementComponent', () => {
  let component: PendingAnnouncementComponent;
  let fixture: ComponentFixture<PendingAnnouncementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PendingAnnouncementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PendingAnnouncementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
