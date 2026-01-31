import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { domainGuardGuard } from './domain-guard-guard';

describe('domainGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => domainGuardGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
