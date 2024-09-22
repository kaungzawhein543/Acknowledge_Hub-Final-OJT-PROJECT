import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotedComponent } from './noted.component';

describe('NotedComponent', () => {
  let component: NotedComponent;
  let fixture: ComponentFixture<NotedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
