import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAnnouncementListComponent } from './user-announcement-list.component';

describe('UserAnnouncementListComponent', () => {
  let component: UserAnnouncementListComponent;
  let fixture: ComponentFixture<UserAnnouncementListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAnnouncementListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserAnnouncementListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
