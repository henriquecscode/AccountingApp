import { Routes } from "@angular/router";
import { DomainList } from "./list/domain-list";
import { DomainCreate } from "./create/domain-create";
import { DomainDetail } from "./detail/domain-detail";
import { redirectToDetailGuard } from "../../../guards/domain-guard/domain-guard-guard";
import { DomainLayout } from "./domain-layout/domain-layout/domain-layout";
import { AppLayout } from "../app-layout/app-layout";
import { EventTagList } from "./eventTag/list/event-tag-list";

export const routes: Routes = [
    {
        path: '',
        component: AppLayout,
        children: [
            { path: '', component: DomainList },
            { path: 'create', component: DomainCreate },
        ]
    },
    {
        path: ':owner/:domainSlug',
        component: DomainLayout,
        children: [
            { path: '', component: DomainDetail, pathMatch: 'full' },
            {
                path: 'platform',
                loadChildren: () => import('../platform/platform.routes').then(m => m.routes)
            },
            {
                path: 'event',
                loadChildren: () => import('./event/event.routes').then(m => m.routes)
            },
            {
                path: 'eventTags',
                component: EventTagList
            },
            {
                path: '**',
                canActivate: [redirectToDetailGuard],
                children: []
            }
        ]
    },
    { path: '**', redirectTo: '' }
];