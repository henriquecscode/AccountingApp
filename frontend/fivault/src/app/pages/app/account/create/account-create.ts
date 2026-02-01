import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SlugGenerator } from '../../../../util/slug';

import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../util/error-localization';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from '../../../../services/account.service';

@Component({
  selector: 'app-account-create',
  imports: [ReactiveFormsModule],
  templateUrl: './account-create.html',
  styleUrl: './account-create.scss',
})
export class AccountCreate {
  MAX_INPUT_LENGTH: number = 255;
  owner: string;
  domainSlug: string;
  platformSlug: string;
  accountCreateForm: FormGroup;
  submitted = false;
  backendError = '';

  private errorHandler = new BackendErrorLocalizationHandler(
    [

    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@account-create-backend-error-unknown:Account creation failed with error ${error}. Please try again`
    )
  );

  constructor(
    private accountService: AccountService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef

  ) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug')!;
    this.platformSlug = this.route.snapshot.paramMap.get('platformSlug')!;
    this.accountCreateForm = this.fb.group({
      accountName: ['', Validators.required, Validators.maxLength(this.MAX_INPUT_LENGTH)],
      accountSlug: ['', Validators.required],
      description: ['', Validators.maxLength(this.MAX_INPUT_LENGTH)]
    })

    this.accountCreateForm.get('accountName')?.valueChanges.subscribe(value => {
      const slug = SlugGenerator.generateSlug(value);
      const slugControl = this.accountCreateForm.get('accountSlug');
      slugControl?.setValue(slug);
      slugControl?.markAsTouched();
    });
  }


  get accountName() {
    return this.accountCreateForm.get("accountName");
  }

  get accountSlug() {
    return this.accountCreateForm.get("accountSlug");
  }

  get description() {
    return this.accountCreateForm.get("description");
  }

  onSubmit(): void {
    this.submitted = true;
    this.backendError = '';

    if (this.accountCreateForm.invalid) {
      this.accountCreateForm.markAllAsTouched();
      return;
    }

    const { accountName, _, description } = this.accountCreateForm.value;

    this.accountService.create(this.owner, this.domainSlug, this.platformSlug, accountName, description).subscribe({
      next: (response) => {
        console.log('Account create success', response);
        const slug: string = response.accountSlug
        this.router.navigate(['../', slug], { relativeTo: this.route });
      },
      error: (err) => {
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        const paramsString = params ? JSON.stringify(params, null, 2) : '';
        this.backendError = this.errorHandler.localize(errorCode, paramsString);

        this.cdr.detectChanges();
      }
    })
  }

}