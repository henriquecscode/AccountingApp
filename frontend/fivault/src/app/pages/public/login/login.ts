import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { lastValueFrom, Observable } from 'rxjs';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../util/error-localization';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  standalone: true
})
export class Login {

  loginForm: FormGroup;
  submitted = false;
  backendError = '';
  showPassword = false;

  private errorHandler = new BackendErrorLocalizationHandler(
    [
      new ErrorMessage('AUTH_002', (params) =>
        $localize`:invalid loging @@login-backend-error-invalid-credentials:The username or password are incorrect.`
      )
    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@login-backend-error-unknown:Login failed with error ${error}. Please try again`
    )
  );
  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,  // To get returnUrl query param
    private cdr: ChangeDetectorRef


  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    this.submitted = true;
    this.backendError = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
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
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        this.backendError = this.errorHandler.localize(errorCode, params);

        this.cdr.detectChanges();
      }
    });
  }


}
