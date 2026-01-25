import { Routes } from "@angular/router";
import { DomainList } from "./list/domain-list";
import { DomainCreate } from "./create/domain-create";
import { DomainDetail } from "./detail/domain-detail";

export const routes: Routes = [

    {
        path: '', component: DomainList
    },
    {
        path: 'create', component: DomainCreate
    },
    {
        path: ':owner/:slug',
        children: [
            { path: '', component: DomainDetail, pathMatch: 'full' },
            {
                path: 'platform',
                loadChildren: () => import('../platform/platform.routes').then(m => m.routes)
            },
            { path: '**', component: DomainDetail } // Catch-all redirects to domain detail

        ]
    },
    {
        path: '**', redirectTo: ''
    }
];
