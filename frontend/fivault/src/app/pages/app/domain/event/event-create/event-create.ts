import { ChangeDetectorRef, Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../../util/error-localization';
import { EventService } from '../../../../../services/event.service';

@Component({
  selector: 'app-event-create',
  imports: [ReactiveFormsModule],
  templateUrl: './event-create.html',
  styleUrl: './event-create.scss',
  standalone: true
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
      title: ['', [Validators.required, Validators.maxLength(this.MAX_INPUT_LENGTH)]],
      description: [''],
      startTimestamp: ['', [Validators.required]],
      endTimestamp: ['']
    }, { validators: this.endAfterStartValidator });
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

  endAfterStartValidator(control: AbstractControl): ValidationErrors | null {
    const start = control.get('startTimestamp')?.value;
    const end = control.get('endTimestamp')?.value;

    if (!start || !end) {
      return null; // No validation needed if either is empty
    }

    return end < start ? { endBeforeStart: true } : null;
  };

  onSubmit(): void {
    this.submitted = true;
    this.backendError = '';

    if (this.eventCreateForm.invalid) {
      this.eventCreateForm.markAllAsTouched();
      return;
    }

    const { title, description, startTimestamp, endTimestamp } = this.eventCreateForm.value;

      // Convert local datetime-local values to ISO strings with UTC offset
  const startIso = startTimestamp ? new Date(startTimestamp).toISOString() : null;
  const endIso = endTimestamp ? new Date(endTimestamp).toISOString() : null;

    this.eventService.create(this.owner, this.domainSlug, {
      title,
      description,
      startTimestamp: startIso,
      endTimestamp: endIso
    }).subscribe({
      next: (response) => {
        console.log('Event create success', response);
        this.router.navigate(['/app/domain', this.owner, this.domainSlug, 'event', response.eventId]);
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