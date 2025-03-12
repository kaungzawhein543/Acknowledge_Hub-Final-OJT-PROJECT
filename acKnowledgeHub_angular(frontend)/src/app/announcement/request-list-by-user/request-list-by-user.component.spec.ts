import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestListByUserComponent } from './request-list-by-user.component';

describe('RequestListByUserComponent', () => {
  let component: RequestListByUserComponent;
  let fixture: ComponentFixture<RequestListByUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RequestListByUserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RequestListByUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
