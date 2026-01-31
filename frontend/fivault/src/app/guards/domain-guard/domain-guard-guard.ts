import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

export const redirectToDetailGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);

  const owner = route.parent?.paramMap.get('owner');
  const domainSlug = route.parent?.paramMap.get('domainSlug');

  if (!owner || !domainSlug) {
    return router.createUrlTree(['/app/domain']);
  }

  return router.createUrlTree(['/app/domain', owner, domainSlug]);
};
