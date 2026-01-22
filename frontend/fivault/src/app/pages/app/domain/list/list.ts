import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';


interface Domain {
  id: number;
  name: string;
  description: string;
  owner: string;
}


@Component({
  selector: 'app-domain',
  imports: [],
  templateUrl: './list.html',
  styleUrl: './list.scss',
  standalone: true
})
export class DomainList {
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

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {

  }

  createDomain(): void {
    console.log("create domain");
    this.router.navigate(['./create'], {relativeTo: this.route})
  }

  viewDomain(domainId: number): void {
    console.log("view domain", domainId);
  }
  
  editDomain(event: Event, domainId: number): void {
    event.stopPropagation();
    console.log("edit domain", domainId);
  }
}
