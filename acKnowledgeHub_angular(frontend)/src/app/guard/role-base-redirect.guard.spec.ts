import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { roleBaseRedirectGuard } from './role-base-redirect.guard';

describe('roleBaseRedirectGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => roleBaseRedirectGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
