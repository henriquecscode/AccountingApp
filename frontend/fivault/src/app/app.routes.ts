import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { Signin } from './pages/signin/signin';
export const routes: Routes = [
  { path: 'signin', component: Signin },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/home' }
];
