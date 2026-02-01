import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterOutlet } from '@angular/router';
import { DomainNavbar } from '../domain-navbar/domain-navbar';

@Component({
  selector: 'app-domain-layout',
  imports: [DomainNavbar, RouterOutlet, RouterLink],
  templateUrl: './domain-layout.html',
  styleUrl: './domain-layout.scss',
  standalone: true
})
export class DomainLayout {
  owner: string;
  domainSlug: string;

  constructor(private route: ActivatedRoute) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug')!;
  }
}
