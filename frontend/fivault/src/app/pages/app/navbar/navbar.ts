import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  constructor(
    private authService: AuthService,
    private router: Router
  ) { }
  logout() {
    this.authService.logout().pipe(
      finalize(() => {
        this.router.navigate(['/']);
      })
    ).subscribe({
      next: (response) => {
        console.log('Logout success', response);
      },
      error: (err) => {
        console.error('Logout failed', err);
      }
    })
  }
}
