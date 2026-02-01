import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DomainNavbar } from '../domain-navbar/domain-navbar';

@Component({
  selector: 'app-domain-layout',
  imports: [DomainNavbar, RouterOutlet],
  templateUrl: './domain-layout.html',
  styleUrl: './domain-layout.scss',
  standalone: true
})
export class DomainLayout {

}
