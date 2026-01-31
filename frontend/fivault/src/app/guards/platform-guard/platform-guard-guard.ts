import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';

export const redirectToCreateGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);

    const owner = route.parent?.paramMap.get('owner');
    const slug = route.parent?.paramMap.get('domainSlug');

    if (!owner || !slug) {
        return router.createUrlTree(['/app/domain']);
    }

    return router.createUrlTree(['/app/domain', owner, slug, 'platform', 'create']);
};

export const redirectToDetailGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
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

    return router.createUrlTree(['/app/domain', owner, domainSlug, 'platform', platformSlug]);
};