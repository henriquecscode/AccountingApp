import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { RouterLink } from '@angular/router';
import { lastValueFrom, Observable } from 'rxjs';

@Component({
  selector: 'app-signin',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signin.html',
  styleUrl: './signin.scss',
  standalone: true
})
export class Signin {

  loginForm: FormGroup;
  polling: Boolean = true;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder) {
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

    this.authService.signin(username, password).subscribe({
      next: (response) => {
        console.log('Login success', response);
        this.startTryingAuthentication();

        // navigate, store token, etc.
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

  ngOnDestroy(){
    this.polling = false;
  }

}
