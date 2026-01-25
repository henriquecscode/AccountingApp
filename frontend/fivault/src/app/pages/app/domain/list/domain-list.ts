import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Domain, VisibleDomain } from '../domain.models';
import { DomainListResult, DomainService } from '../../../../services/domain.service';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../util/error-localization';
import { catchError, map, Observable, of } from 'rxjs';
import { CommonModule } from '@angular/common';
interface ViewModel {
  myDomains: VisibleDomain[];
  otherDomains: VisibleDomain[];
  error: string | null;
  isLoading: boolean;
}

@Component({
  selector: 'app-domain',
  imports: [CommonModule],
  templateUrl: './domain-list.html',
  styleUrl: './domain-list.scss',
  standalone: true
})
export class DomainList implements OnInit {
  viewModel$!: Observable<ViewModel>;

  private errorHandler = new BackendErrorLocalizationHandler(
    [

    ],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@domain-list-backend-error-unknown:Domain list failed with error ${error}. Please try again`
    )
  );
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private domainService: DomainService
  ) { }

  ngOnInit(): void {
    this.loadDomains();
  }

  private loadDomains(): void {
    this.viewModel$ = this.domainService.list().pipe(
      map(result => ({
        myDomains: result.myDomains,
        otherDomains: result.otherDomains,
        error: null as string | null,
        isLoading: false
      })),
      catchError(err => {
        console.error('Error loading domains:', err);
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        const paramsString = params ? JSON.stringify(params, null, 2) : '';
        return of({
          myDomains: [] as VisibleDomain[],
          otherDomains: [] as VisibleDomain[],
          error: this.errorHandler.localize(errorCode, paramsString),
          isLoading: false
        });
      })
    );
  }

  retry() {
    this.loadDomains();
  }
  createDomain(): void {
    console.log("create domain");
    this.router.navigate(['./create'], { relativeTo: this.route })
  }

  viewDomain(owner: String, slug: String): void {
    console.log("view domain", owner, "/", slug);
    this.router.navigate([owner, slug], { relativeTo: this.route })
  }

  editDomain(event: Event, owner: String, slug: String): void {
    event.stopPropagation();
    console.log("edit domain", owner, "/", slug);
  }
}
