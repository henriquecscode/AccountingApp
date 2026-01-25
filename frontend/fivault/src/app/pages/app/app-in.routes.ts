import { Routes } from "@angular/router";
import { HomeComponent } from "./home/home";
import { AppLayout } from "./app-layout/app-layout";

export const routes: Routes = [
    {
        path: '',
        component: AppLayout,
        children: [
            { path: '', redirectTo: 'home', pathMatch: 'full' },
            { path: 'home', component: HomeComponent },
            {
                path: 'domain',
                loadChildren: () => import('./domain/domain.routes').then(m => m.routes)
            },
            { path: '**', redirectTo: 'home' }
        ]
    }
]