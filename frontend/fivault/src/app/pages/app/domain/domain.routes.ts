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
        path: ':owner/:slug', component: DomainDetail
    },
    {
        path: '**', redirectTo: ''
    }
];
