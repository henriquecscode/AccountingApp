// signup.component.ts
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  templateUrl: './signup.html',
  styleUrl: './signup.scss',
  standalone: true
})
export class Signup {
  signupForm: FormGroup;
  submitted = false;
  backendError = '';
  showPassword = false;
  showConfirmPassword = false;
  MIN_LENGTH = 8;

  constructor(
    private authservice: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.signupForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(this.MIN_LENGTH),
        this.passwordStrengthValidator
      ]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator  // Form-level validator
    });
  }

  // Custom validator for password strength
  passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;

    const hasNumber = /[0-9]/.test(value);
    const hasUpper = /[A-Z]/.test(value);
    const hasLower = /[a-z]/.test(value);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);

    const valid = hasNumber && hasUpper && hasLower && hasSpecial;

    if (!valid) {
      return {
        passwordStrength: {
          hasNumber,
          hasUpper,
          hasLower,
          hasSpecial
        }
      };
    }

    return null;
  }

  // Custom validator for password match
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) return null;

    if (confirmPassword.value === '') return null;

    return password.value === confirmPassword.value
      ? null
      : { passwordMismatch: true };
  }

  // Getters for easy access in template
  get username() {
    return this.signupForm.get('username');
  }

  get email() {
    return this.signupForm.get('email');
  }

  get password() {
    return this.signupForm.get('password');
  }

  get confirmPassword() {
    return this.signupForm.get('confirmPassword');
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit() {
    this.submitted = true;
    this.backendError = '';

    if (this.signupForm.invalid) {
      // Mark all fields as touched to show errors
      // Object.keys(this.signupForm.controls).forEach(key => {
      //   this.signupForm.get(key)?.markAsTouched();
      // });
      this.signupForm.markAllAsTouched();
      return;
    }

    const { username, email, password } = this.signupForm.value;

    this.authservice.signup(username, email, password).subscribe({
      next: (response) => {
        console.log("Signup success", response);
        this.router.navigate(['/app/home']);
      },
      error: (err) => {
        console.log("Signup error", err);

        // Handle backend errors
        if (err.status === 409) {
          this.backendError = 'This username is already registered';
        } else if (err.error?.message) {
          this.backendError = err.error.message;
        } else {
          this.backendError = 'Signup failed. Please try again.';
        }

        this.cdr.detectChanges();
      }
    });
  }
}