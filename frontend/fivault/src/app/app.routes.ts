import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { Login } from './pages/login/login';
export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'storefront' },
  { path: 'login', component: Login },
  { path: '**', redirectTo: '/home' }
];
