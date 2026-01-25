import { Component, OnInit } from '@angular/core';
import { DomainService } from '../../../../services/domain.service';
import { ActivatedRoute } from '@angular/router';
import { catchError, map, Observable, of, startWith } from 'rxjs';
import { AppUserDomainRole, Domain } from '../domain.models';
import { CommonModule } from '@angular/common';


export interface DomainDetailViewModel {
  domain: Domain | null;
  domainAppUsers: AppUserDomainRole[];
  error: string | null;
  isLoading: boolean;
}

@Component({
  selector: 'app-domain-detail',
  imports: [CommonModule],
  templateUrl: './domain-detail.html',
  styleUrl: './domain-detail.scss',
})
export class DomainDetail implements OnInit {
  viewModel$!: Observable<DomainDetailViewModel>;

  constructor(
    private domainService: DomainService,
    private route: ActivatedRoute
  ) { }


  ngOnInit(): void {
    const owner = this.route.snapshot.paramMap.get('owner');
    const slug = this.route.snapshot.paramMap.get('slug');

    if (!owner || !slug) {
      this.viewModel$ = of({
        domain: null,
        domainAppUsers: [],
        error: 'Invalid route parameters',
        isLoading: false
      });
      return;
    }

    this.viewModel$ = this.domainService.getDetail(owner, slug).pipe(
      map(result => ({
        domain: result.domain,
        domainAppUsers: result.userRoles,
        error: null,
        isLoading: false
      })),
      catchError(err => {
        console.error('Error loading domain detail:', err);
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        return of({
          domain: null,
          domainAppUsers: [],
          error: errorCode,
          isLoading: false
        });
      }),
      startWith({
        domain: null,
        domainAppUsers: [],
        error: null,
        isLoading: true
      })
    );
  }
}
