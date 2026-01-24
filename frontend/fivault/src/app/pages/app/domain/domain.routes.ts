import { Routes } from "@angular/router";
import { DomainList } from "./list/domain-list";
import { DomainCreate } from "./create/domain-create";

export const routes: Routes = [

    {
        path: '', component: DomainList
    },
    {
        path: 'create', component: DomainCreate
    }
];
