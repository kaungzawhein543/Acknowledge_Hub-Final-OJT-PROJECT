import { TestBed } from '@angular/core/testing';

import { RefreshDebounceService } from './refresh-debounce.service';

describe('RefreshDebounceService', () => {
  let service: RefreshDebounceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RefreshDebounceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
