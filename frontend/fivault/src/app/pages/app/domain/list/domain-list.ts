import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Domain } from '../domain.models';
import { DomainListResult, DomainService } from '../../../../services/domain.service';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../util/error-localization';


@Component({
  selector: 'app-domain',
  imports: [],
  templateUrl: './domain-list.html',
  styleUrl: './domain-list.scss',
  standalone: true
})
export class DomainList {
  myDomains: Domain[] = [];
  otherDomains: Domain[] = [];
  backendError = '';
  private errorHandler = new BackendErrorLocalizationHandler(
    [

    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@domain-list-backend-error-unknown:Domain list failed with error ${error}. Please try again`
    )
  );
  /*
  // Mock data - replace with actual service call
  myDomains: Domain[] = [
    { id: 1, name: 'My Business Domain', description: 'Main business operations', owner: 'me' },
    { id: 2, name: 'Personal Projects', description: 'Side projects and experiments', owner: 'me' },
    { id: 3, name: 'Finance Tracking', description: 'Personal finance management', owner: 'me' }
  ];

  otherDomains: Domain[] = [
    { id: 4, name: 'Company Domain', description: 'Shared company resources', owner: 'John Doe' },
    { id: 5, name: 'Team Project', description: 'Collaborative team workspace', owner: 'Jane Smith' },
    { id: 6, name: 'Marketing Assets', description: 'Marketing materials and campaigns', owner: 'Mike Johnson' }
  ];
  */

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private domainService: DomainService,
    private cdr: ChangeDetectorRef
  ) {

  }

  ngOnInit(): void {
    this.domainService.list().subscribe({
      next: (response: DomainListResult) => {
        console.log('Data received:', response);
        console.log('myDomains:', response.myDomains);
        console.log('otherDomains:', response.otherDomains);

        this.myDomains = response.myDomains;
        this.otherDomains = response.otherDomains;
        this.backendError = '';
        this.cdr.detectChanges(); // Force change detection

        console.log('After assignment - myDomains:', this.myDomains);
        console.log('After assignment - otherDomains:', this.otherDomains);
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

  createDomain(): void {
    console.log("create domain");
    this.router.navigate(['./create'], { relativeTo: this.route })
  }

  viewDomain(owner: String, slug: String): void {
    console.log("view domain", owner, "/", slug);
  }

  editDomain(event: Event, owner: String, slug: String): void {
    event.stopPropagation();
    console.log("edit domain", owner, "/", slug);
  }
}
