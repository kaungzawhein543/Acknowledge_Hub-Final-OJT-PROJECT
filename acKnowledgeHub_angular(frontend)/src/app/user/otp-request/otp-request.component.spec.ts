import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtpRequestComponent } from './otp-request.component';

describe('OtpRequestComponent', () => {
  let component: OtpRequestComponent;
  let fixture: ComponentFixture<OtpRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OtpRequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OtpRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
