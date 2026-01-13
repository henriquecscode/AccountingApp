// signup.component.ts
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../util/error-localization';

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


  private errorHandler = new BackendErrorLocalizationHandler(
    [
      new ErrorMessage('AUTH_001', (params) =>
        $localize `:user exists @@signup-backend-error-user-exists:The username ${params?.username} is already registered.`
      ),
      new ErrorMessage('VAL_001', () =>
        $localize `:@@signup-backend-error-invalid-input:Invalid input`
      ),
    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize `:@@signup-backend-error-unknown:Signup failed with error ${error}. Please try again`
    )
  );

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

        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        this.backendError = this.errorHandler.localize(errorCode, params);

        this.cdr.detectChanges();
      }
    });
  }
}
