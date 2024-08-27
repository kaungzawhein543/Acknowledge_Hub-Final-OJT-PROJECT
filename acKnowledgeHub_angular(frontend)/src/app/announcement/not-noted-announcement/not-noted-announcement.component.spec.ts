import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotNotedAnnouncementComponent } from './not-noted-announcement.component';

describe('NotNotedAnnouncementComponent', () => {
  let component: NotNotedAnnouncementComponent;
  let fixture: ComponentFixture<NotNotedAnnouncementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotNotedAnnouncementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotNotedAnnouncementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
