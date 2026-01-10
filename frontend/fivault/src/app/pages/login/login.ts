import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { lastValueFrom, Observable } from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  standalone: true
})
export class Login {

  loginForm: FormGroup;
  polling: Boolean = true;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute  // To get returnUrl query param

  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }

    const { username, password } = this.loginForm.value;

    this.authService.login(username, password).subscribe({
      next: (response) => {
        console.log('Login success', response);
        var returnUrl: string = this.route.snapshot.queryParams['returnUrl'] || '/app/home';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => {
        console.error('Login failed', err);
      }
    });
  }

  async startTryingAuthentication() {
    if (!this.polling) { return; }

    let response: String = await lastValueFrom(this.authService.tryAuthentication())
    console.log(response);



    await this.delayfunc(2000); // Function RETURNS here again

    this.startTryingAuthentication();
  }

  private delayfunc(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  ngOnDestroy() {
    this.polling = false;
  }

}
