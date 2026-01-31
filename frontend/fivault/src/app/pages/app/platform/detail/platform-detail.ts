import { Component, OnInit } from '@angular/core';
import { PlatformService } from '../../../../services/platform.service';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, map, Observable, of, shareReplay, startWith } from 'rxjs';
import { Platform } from '../platform.models';
import { CommonModule } from '@angular/common';
import { Account } from '../../account/account.model';


export interface PlatformDetailViewModel {
  platform: Platform | null;
  accounts: Account[];
  error: string | null;
  isLoading: boolean;
}

@Component({
  selector: 'app-platform-detail',
  imports: [CommonModule],
  templateUrl: './platform-detail.html',
  styleUrl: './platform-detail.scss',
})
export class PlatformDetail implements OnInit {
  viewModel$!: Observable<PlatformDetailViewModel>;
  owner!: string;
  domainSlug!: string;
  platformSlug!: string;
  constructor(
    private platformService: PlatformService,
    private route: ActivatedRoute,
    private router: Router
  ) { }


  ngOnInit(): void {
    this.owner = this.route.snapshot.paramMap.get('owner') || '';
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug') || '';
    this.platformSlug = this.route.snapshot.paramMap.get('platformSlug') || '';

    if (!this.owner || !this.domainSlug || !this.platformSlug) {
      this.viewModel$ = of({
        platform: null,
        accounts: [],
        error: 'Invalid route parameters',
        isLoading: false
      });
      return;
    }

    this.viewModel$ = this.platformService.getDetail(this.owner, this.domainSlug, this.platformSlug).pipe(
      map(result => ({
        platform: result.platform,
        accounts: result.accounts,
        error: null,
        isLoading: false
      })),
      catchError(err => {
        console.error('Error loading platform detail:', err);
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        return of({
          platform: null,
          accounts: [],
          error: errorCode,
          isLoading: false
        });
      }),
      startWith({
        platform: null,
        accounts: [],
        error: null,
        isLoading: true
      }),
      shareReplay(1)
    );
  }

  createAccount(): void {
    console.log("create Account");
    this.router.navigate(['account', 'create'], { relativeTo: this.route })
  }

  viewAccount(accountSlug: string) {
    this.router.navigate(['/app/domain', this.owner, this.domainSlug, 'platform', this.platformSlug, 'account', accountSlug]);
  }

  editAccount(event: Event, accountSlug: string) {
    event.stopPropagation(); // Prevent card click
    this.router.navigate(['/app/domain', this.owner, this.domainSlug, 'platform', this.platformSlug, 'account', accountSlug, 'edit']);
  }

}