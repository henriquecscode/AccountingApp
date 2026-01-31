import { Routes } from "@angular/router";
import { redirectToCreateGuard, redirectToDetailGuard } from "../../../guards/account-guard/account-guard-guard";
import { AccountDetail } from "./detail/account-detail";
import { AccountCreate } from "./create/account-create";
export const routes: Routes = [
    {
        path: '',
        canActivate: [redirectToCreateGuard],
        children: [] // Dummy component (won't be rendered)
    },
    {
        path: 'create', component: AccountCreate
    },
    {
        path: ':accountSlug',
        children: [
            { path: '', component: AccountDetail, pathMatch: 'full' },
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
