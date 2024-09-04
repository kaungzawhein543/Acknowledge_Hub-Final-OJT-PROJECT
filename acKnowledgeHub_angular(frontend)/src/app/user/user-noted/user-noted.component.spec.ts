import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserNotedComponent } from './user-noted.component';

describe('UserNotedComponent', () => {
  let component: UserNotedComponent;
  let fixture: ComponentFixture<UserNotedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserNotedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserNotedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
