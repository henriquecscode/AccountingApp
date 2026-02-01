import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
})
export class Footer implements AfterViewInit {
 @ViewChild('footer') footer!: ElementRef;

  ngAfterViewInit() {
    const height = this.footer.nativeElement.offsetHeight;
    document.documentElement.style.setProperty('--footer-height', `${height}px`);
  }
}
