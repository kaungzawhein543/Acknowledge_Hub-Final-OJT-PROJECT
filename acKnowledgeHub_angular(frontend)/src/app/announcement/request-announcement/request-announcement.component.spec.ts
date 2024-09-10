import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestAnnouncementComponent } from './request-announcement.component';

describe('RequestAnnouncementComponent', () => {
  let component: RequestAnnouncementComponent;
  let fixture: ComponentFixture<RequestAnnouncementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RequestAnnouncementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RequestAnnouncementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
