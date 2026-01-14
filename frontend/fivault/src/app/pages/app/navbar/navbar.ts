import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { finalize } from 'rxjs';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
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
