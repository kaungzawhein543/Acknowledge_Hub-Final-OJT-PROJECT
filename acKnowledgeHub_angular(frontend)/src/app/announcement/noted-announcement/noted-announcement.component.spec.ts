import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotedAnnouncementComponent } from './noted-announcement.component';

describe('NotedAnnouncementComponent', () => {
  let component: NotedAnnouncementComponent;
  let fixture: ComponentFixture<NotedAnnouncementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotedAnnouncementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotedAnnouncementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
