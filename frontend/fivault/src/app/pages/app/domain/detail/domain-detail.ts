import { Component, OnInit } from '@angular/core';
import { DomainService } from '../../../../services/domain.service';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, map, Observable, of, shareReplay, startWith } from 'rxjs';
import { AppUserDomainRole, Domain } from '../domain.models';
import { CommonModule } from '@angular/common';
import { Platform } from '../../platform/platform.models';


export interface DomainDetailViewModel {
  domain: Domain | null;
  domainAppUsers: AppUserDomainRole[];
  platforms: Platform[];
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
    private route: ActivatedRoute,
    private router: Router
  ) { }


  ngOnInit(): void {
    const owner = this.route.snapshot.paramMap.get('owner');
    const slug = this.route.snapshot.paramMap.get('slug');

    if (!owner || !slug) {
      this.viewModel$ = of({
        domain: null,
        domainAppUsers: [],
        platforms: [],
        error: 'Invalid route parameters',
        isLoading: false
      });
      return;
    }

    this.viewModel$ = this.domainService.getDetail(owner, slug).pipe(
      map(result => ({
        domain: result.domain,
        domainAppUsers: result.userRoles,
        platforms: result.platforms,
        error: null,
        isLoading: false
      })),
      catchError(err => {
        console.error('Error loading domain detail:', err);
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        return of({
          domain: null,
          domainAppUsers: [],
          platforms: [],
          error: errorCode,
          isLoading: false
        });
      }),
      startWith({
        domain: null,
        domainAppUsers: [],
        platforms: [],
        error: null,
        isLoading: true
      }),
      shareReplay(1)
    );
  }
  createPlatform(): void {
    console.log("create Platform");
    this.router.navigate(['platform', 'create'], { relativeTo: this.route })
  }

  viewPlatform(owner: string, slug: string, platformSlug: string) {
    this.router.navigate(['/app/domain', owner, slug, 'platform', platformSlug]);
  }

  editPlatform(event: Event, owner: string, slug: string, platformSlug: string) {
    event.stopPropagation(); // Prevent card click
    // Navigate to edit page or open modal
    this.router.navigate(['/app/domain', owner, slug, 'platform', platformSlug, 'edit']);
  }

}
