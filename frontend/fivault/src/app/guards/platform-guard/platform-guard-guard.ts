import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';

export const redirectToCreateGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    
    const owner = route.parent?.paramMap.get('owner');
    const slug = route.parent?.paramMap.get('slug');
    
    if (!owner || !slug) {
        return router.createUrlTree(['/app/domain']);
    }
    
    return router.createUrlTree(['/app/domain', owner, slug, 'platform', 'create']);
};