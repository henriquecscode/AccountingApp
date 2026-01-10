import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent {

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
