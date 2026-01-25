import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SlugGenerator } from '../../../../util/slug';

import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../util/error-localization';
import { ActivatedRoute, Router } from '@angular/router';
import { PlatformService } from '../../../../services/platform.service';

@Component({
  selector: 'app-platform-create',
  imports: [ReactiveFormsModule],
  templateUrl: './platform-create.html',
  styleUrl: './platform-create.scss',
})
export class PlatformCreate {

  owner: string;
  domainSlug: string;
  platformCreateForm: FormGroup;
  submitted = false;
  backendError = '';

  private errorHandler = new BackendErrorLocalizationHandler(
    [

    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@platform-create-backend-error-unknown:Platform creation failed with error ${error}. Please try again`
    )
  );

  constructor(
    private platformService: PlatformService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef

  ) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('slug')!;
    this.platformCreateForm = this.fb.group({
      platformName: ['', Validators.required],
      platformSlug: ['', Validators.required],
      description: ['']
    })

    this.platformCreateForm.get('platformName')?.valueChanges.subscribe(value => {
      const slug = SlugGenerator.generateSlug(value);
      const slugControl = this.platformCreateForm.get('platformSlug');
      slugControl?.setValue(slug);
      slugControl?.markAsTouched();
    });
  }


  get platformName() {
    return this.platformCreateForm.get("platformName");
  }

  get platformSlug() {
    return this.platformCreateForm.get("platformSlug");
  }

  get description() {
    return this.platformCreateForm.get("description");
  }

  onSubmit(): void {
    this.submitted = true;
    this.backendError = '';

    if (this.platformCreateForm.invalid) {
      this.platformCreateForm.markAllAsTouched();
      return;
    }

    const { platformName, _, description } = this.platformCreateForm.value;

    this.platformService.create(this.owner, this.domainSlug, platformName, description).subscribe({
      next: (response) => {
        console.log('Platform create success', response);
        const slug: string = response.platformSlug
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
