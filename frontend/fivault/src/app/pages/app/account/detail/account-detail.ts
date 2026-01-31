import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from '../../../../services/account.service';
import { Account } from '../account.model';
import { catchError, map, Observable, of, shareReplay, startWith } from 'rxjs';
import { CommonModule } from '@angular/common';


export interface AccountDetailViewModel {
  account: Account | null;
  error: string | null;
  isLoading: boolean;
}
@Component({
  selector: 'app-account-detail',
  imports: [CommonModule],
  templateUrl: './account-detail.html',
  styleUrl: './account-detail.scss',
})
export class AccountDetail implements OnInit {
  viewModel$!: Observable<AccountDetailViewModel>;
  owner: string;
  domainSlug: string;
  platformSlug: string;
  accountSlug: string;

  constructor(
    private route: ActivatedRoute,
    private accountService: AccountService
  ) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug')!;
    this.platformSlug = this.route.snapshot.paramMap.get('platformSlug')!;
    this.accountSlug = this.route.snapshot.paramMap.get('accountSlug')!;
  }

  ngOnInit(): void {
    if (!this.owner || !this.domainSlug || !this.platformSlug || !this.accountSlug) {
      this.viewModel$ = of({
        account: null,
        error: 'Invalid route parameters',
        isLoading: false
      });
    }
    this.loadAccountDetail();
  }

  loadAccountDetail(): void {
    this.viewModel$ = this.accountService.getDetail(this.owner, this.domainSlug, this.platformSlug, this.accountSlug).pipe(
      map(result => ({
        account: result.account,
        error: null,
        isLoading: false
      })),
      catchError(err => {
        console.error('Error loading platform detail:', err);
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        return of({
          account: null,
          error: errorCode,
          isLoading: false
        });
      }),
      startWith({
        account: null,
        error: null,
        isLoading: true
      }),
      shareReplay(1)
    );


  }
}