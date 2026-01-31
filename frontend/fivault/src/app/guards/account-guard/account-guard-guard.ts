import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';

export const redirectToCreateGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);

  const owner = route.parent?.paramMap.get('owner');
  const domainSlug = route.parent?.paramMap.get('domainSlug');
  const platformSlug = route.parent?.paramMap.get('platformSlug');

  if (!owner || !domainSlug) {
    return router.createUrlTree(['/app/domain']);
  }

  if (!platformSlug) {
    return router.createUrlTree([`/app/domain/${owner}/${domainSlug}`])
  }



  return router.createUrlTree(['/app/domain', owner, domainSlug, 'platform', platformSlug, 'account', 'create']);
};

export const redirectToDetailGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);

  const owner = route.parent?.paramMap.get('owner');
  const domainSlug = route.parent?.paramMap.get('domainSlug');
  const platformSlug = route.parent?.paramMap.get('platformSlug');
  const accountSlug = route.parent?.paramMap.get('accountSlug');

  if (!owner || !domainSlug) {
    return router.createUrlTree(['/app/domain']);
  }

  if (!platformSlug) {
    return router.createUrlTree([`/app/domain/${owner}/${domainSlug}`])
  }

  if (!accountSlug) {
    return router.createUrlTree(['/app/domain', owner, domainSlug, 'platform', platformSlug]);
  }

  return router.createUrlTree(['/app/domain', owner, domainSlug, 'platform', platformSlug, 'account', accountSlug]);
};
