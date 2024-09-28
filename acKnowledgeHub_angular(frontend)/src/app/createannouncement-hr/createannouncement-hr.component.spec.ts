import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateannouncementHrComponent } from './createannouncement-hr.component';

describe('CreateannouncementHrComponent', () => {
  let component: CreateannouncementHrComponent;
  let fixture: ComponentFixture<CreateannouncementHrComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateannouncementHrComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateannouncementHrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
