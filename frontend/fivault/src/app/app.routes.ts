import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Landing } from './pages/landing/landing';
import { Signup } from './pages/signup/signup';
import { authGuard } from './guards/auth-guard/auth-guard';
export const routes: Routes = [

  { path: '', redirectTo: 'landing', pathMatch: 'full' },

  { path: 'landing', component: Landing },
  { path: 'login', component: Login },
  { path: 'signup', component: Signup },
  {
    path: 'app',
    canActivate: [authGuard],
    loadChildren: () => import('./pages/app/app-in.routes').then(m => m.routes)
  },
  { path: '**', redirectTo: 'landing' }
];
