import { Routes } from "@angular/router";
import { PlatformCreate } from "./create/platform-create";
import { DomainDetail } from "../domain/detail/domain-detail";
import { redirectToCreateGuard } from "../../../guards/platform-guard/platform-guard-guard";
import { PlatformDetail } from "./detail/platform-detail";
export const routes: Routes = [
    {
        path: '',
        canActivate: [redirectToCreateGuard],
        children: [] // Dummy component (won't be rendered)
    },
    {
        path: 'create', component: PlatformCreate
    },
    {
        path: ':platformSlug',
        component: PlatformDetail
    },
    {
        path: '**', redirectTo: ''
    }
];
