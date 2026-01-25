import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { platformGuardGuard } from './platform-guard-guard';

describe('platformGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => platformGuardGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
