import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SlugGenerator } from '../../../../util/slug';
import { DomainService } from '../../../../services/domain.service';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../util/error-localization';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-domain-create',
  imports: [ReactiveFormsModule],
  templateUrl: './domain-create.html',
  styleUrl: './domain-create.scss',
})
export class DomainCreate {

  domainCreateForm: FormGroup;
  submitted = false;
  backendError = '';

  private errorHandler = new BackendErrorLocalizationHandler(
    [

    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@domain-create-backend-error-unknown:Domain creation failed with error ${error}. Please try again`
    )
  );

  constructor(
    private domainService: DomainService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef

  ) {
    this.domainCreateForm = this.fb.group({
      domainName: ['', Validators.required],
      domainSlug: ['', Validators.required],
      description: ['']
    })

    this.domainCreateForm.get('domainName')?.valueChanges.subscribe(value => {
      const slug = SlugGenerator.generateSlug(value);
      const slugControl = this.domainCreateForm.get('domainSlug');
      slugControl?.setValue(slug);
      slugControl?.markAsTouched();
    });
  }


  get domainName() {
    return this.domainCreateForm.get("domainName");
  }

  get domainSlug() {
    return this.domainCreateForm.get("domainSlug");
  }

  get description(){
    return this.domainCreateForm.get("description");
  }

  onSubmit(): void {
    this.submitted = true;
    this.backendError = '';

    if (this.domainCreateForm.invalid) {
      this.domainCreateForm.markAllAsTouched();
      return;
    }

    const { domainName, _ , description} = this.domainCreateForm.value;

    this.domainService.create(domainName, description).subscribe({
      next: (response) => {
        console.log('Domain create success', response);
        this.router.navigate(['../'], { relativeTo: this.route });
      },
      error: (err) => {
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        this.backendError = this.errorHandler.localize(errorCode, params);

        this.cdr.detectChanges();
      }
    })
  }

}
