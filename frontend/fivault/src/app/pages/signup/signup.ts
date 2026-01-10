import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signup.html',
  styleUrl: './signup.scss',
  standalone: true
})
export class Signup {
  signupForm: FormGroup;

  constructor(
    private authservice: AuthService,
    private router: Router,
    private fb: FormBuilder) {
    this.signupForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    })
  }

  onSubmit() {
    if (this.signupForm.invalid) {
      return;
    }

    const { email, password, confirmPassword } = this.signupForm.value;

    this.authservice.signup(email, password).subscribe({
      next: (response) => {
        console.log("Signup success", response);
        this.router.navigate(['/app/home']);
      },
      error: (err) => {
        console.log("Signup error", err);
      }
    })
  }
}
