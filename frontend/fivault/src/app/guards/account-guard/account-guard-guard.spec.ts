import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';
import { redirectToCreateGuard } from './account-guard-guard';



describe('accountGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => redirectToCreateGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
