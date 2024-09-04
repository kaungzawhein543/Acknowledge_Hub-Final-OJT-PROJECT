import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserUnnotedComponent } from './user-unnoted.component';

describe('UserUnnotedComponent', () => {
  let component: UserUnnotedComponent;
  let fixture: ComponentFixture<UserUnnotedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserUnnotedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserUnnotedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
