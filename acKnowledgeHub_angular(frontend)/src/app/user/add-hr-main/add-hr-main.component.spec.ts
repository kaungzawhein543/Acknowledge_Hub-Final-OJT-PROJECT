import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddHRMainComponent } from './add-hr-main.component';

describe('AddHRMainComponent', () => {
  let component: AddHRMainComponent;
  let fixture: ComponentFixture<AddHRMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddHRMainComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddHRMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
