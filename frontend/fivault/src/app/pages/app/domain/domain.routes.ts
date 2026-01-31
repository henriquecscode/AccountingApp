import { Routes } from "@angular/router";
import { DomainList } from "./list/domain-list";
import { DomainCreate } from "./create/domain-create";
import { DomainDetail } from "./detail/domain-detail";
import { redirectToDetailGuard } from "../../../guards/domain-guard/domain-guard-guard";

export const routes: Routes = [

    {
        path: '', component: DomainList
    },
    {
        path: 'create', component: DomainCreate
    },
    {
        path: ':owner/:domainSlug',
        children: [
            { path: '', component: DomainDetail, pathMatch: 'full' },
            {
                path: 'platform',
                loadChildren: () => import('../platform/platform.routes').then(m => m.routes)
            },
            {
                path: '**',
                canActivate: [redirectToDetailGuard],
                children: []
            } // Catch-all redirects to domain detail

        ]
    },
    {
        path: '**', redirectTo: ''
    }
];
