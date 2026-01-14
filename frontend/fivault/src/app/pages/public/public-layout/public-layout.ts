import { Component } from '@angular/core';
import { PublicNavbar } from '../navbar/navbar';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-public-layout',
  imports: [PublicNavbar, RouterOutlet],
  templateUrl: './public-layout.html',
  styleUrl: './public-layout.scss',
  standalone: true
})
export class PublicLayout {

}
