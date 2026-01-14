import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
  standalone: true
})
export class Landing {

  constructor(private router: Router) { }
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  navigateToSignup(): void {
    this.router.navigate(['/signup']);
  }
}
