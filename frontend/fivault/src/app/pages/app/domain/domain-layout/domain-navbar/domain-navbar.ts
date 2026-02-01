import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Observable, of } from 'rxjs';


export interface DomainNavbarViewmodel {

}
@Component({
  selector: 'domain-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './domain-navbar.html',
  styleUrl: './domain-navbar.scss',
})
export class DomainNavbar implements OnInit {
  viewModel$!: Observable<DomainNavbarViewmodel>;
  owner: string;
  domainSlug: string;
  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug')!;
  }


  ngOnInit(): void {
    if (!this.owner || !this.domainSlug) {
      this.viewModel$ = of({});
      return;
    }
    this.loadDomainNavbar();
  }

  loadDomainNavbar(): void {
    this.viewModel$ = of({});
  }

  leaveDomain() {
    this.router.navigate(['/app/domain']);
  }
}
