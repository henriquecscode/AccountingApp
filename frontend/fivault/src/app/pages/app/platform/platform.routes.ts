import { Routes } from "@angular/router";
import { PlatformCreate } from "./create/platform-create";
import { redirectToCreateGuard, redirectToDetailGuard } from "../../../guards/platform-guard/platform-guard-guard";
import { PlatformDetail } from "./detail/platform-detail";
export const routes: Routes = [
    {
        // TODO: Go to platform list?
        path: '',
        canActivate: [redirectToCreateGuard],
        children: [] // Dummy component (won't be rendered)
    },
    {
        path: 'create', component: PlatformCreate
    },
    {
        path: ':platformSlug',
        children: [
            { path: '', component: PlatformDetail, pathMatch: 'full' },
            {
                path: 'account',
                loadChildren: () => import('../account/account.routes').then(m => m.routes)
            },
            {
                path: '**',
                canActivate: [redirectToDetailGuard],
                children: []
            }
        ]
    },

    {
        path: '**', redirectTo: ''
    }
];
