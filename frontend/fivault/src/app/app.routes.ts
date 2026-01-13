import { Routes } from '@angular/router';
import { Login } from './pages/public/login/login';
import { Landing } from './pages/public/landing/landing';
import { Signup } from './pages/public/signup/signup';
import { authGuard } from './guards/auth-guard/auth-guard';
import { loginGuard } from './guards/login-guard/login-guard';
import { PublicLayout } from './pages/public/public-layout/public-layout';
import { AppLayout } from './pages/app/app-layout/app-layout';
export const routes: Routes = [
  {
    path: '',
    component: PublicLayout,
    children: [
      { path: '', redirectTo: 'landing', pathMatch: 'full' },
      { path: 'landing', component: Landing },
      {
        path: 'login',
        canActivate: [loginGuard],
        component: Login
      },
      {
        path: 'signup',
        canActivate: [loginGuard],
        component: Signup
      }
    ]
  },
  {
    path: 'app',
    component: AppLayout,
    canActivate: [authGuard],
    loadChildren: () => import('./pages/app/app-in.routes').then(m => m.routes)
  },
  { path: '**', redirectTo: 'landing' }
];
