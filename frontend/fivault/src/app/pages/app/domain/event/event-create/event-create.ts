import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../../util/error-localization';
import { EventService } from '../../../../../services/event.service';

@Component({
  selector: 'app-event-create',
  imports: [ReactiveFormsModule],
  templateUrl: './event-create.html',
  styleUrl: './event-create.scss',
})
export class EventCreate {
  MAX_INPUT_LENGTH: number = 500;
  owner: string;
  domainSlug: string;
  eventCreateForm: FormGroup;
  submitted = false;
  backendError = '';

  private errorHandler = new BackendErrorLocalizationHandler(
    [

    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@event-create-backend-error-unknown:Event creation failed with error ${error}. Please try again`
    )
  );

  constructor(
    private eventService: EventService,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug')!;
    this.eventCreateForm = this.fb.group({
      title: ['', Validators.required, Validators.maxLength(this.MAX_INPUT_LENGTH)],
      description: [''],
      startTimestamp: ['', Validators.required],
      endTimestamp: ['']
    });
  }

  get title() {
    return this.eventCreateForm.get('title');
  }

  get description() {
    return this.eventCreateForm.get('description');
  }

  get startTimestamp() {
    return this.eventCreateForm.get('startTimestamp');
  }

  get endTimestamp() {
    return this.eventCreateForm.get('endTimestamp');
  }

  onSubmit(): void {
    this.submitted = true;
    this.backendError = '';

    if (this.eventCreateForm.invalid) {
      this.eventCreateForm.markAllAsTouched();
      return;
    }

    const { title, description, startTimestamp, endTimestamp } = this.eventCreateForm.value;

    this.eventService.create(this.owner, this.domainSlug, {
      title,
      description,
      startTimestamp: startTimestamp || null,
      endTimestamp: endTimestamp || null
    }).subscribe({
      next: (response) => {
        console.log('Event create success', response);
        this.router.navigate(['/app/domain', this.owner, this.domainSlug]);
      },
      error: (err) => {
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        const paramsString = params ? JSON.stringify(params, null, 2) : '';
        this.backendError = this.errorHandler.localize(errorCode, paramsString);
        this.cdr.detectChanges();
      }
    });
  }
}