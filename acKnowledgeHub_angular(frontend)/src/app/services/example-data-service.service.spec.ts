import { TestBed } from '@angular/core/testing';

import { ExampleDataServiceService } from './example-data-service.service';

describe('ExampleDataServiceService', () => {
  let service: ExampleDataServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExampleDataServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
