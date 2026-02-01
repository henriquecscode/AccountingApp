import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainNavbar } from './domain-navbar';

describe('DomainNavbar', () => {
  let component: DomainNavbar;
  let fixture: ComponentFixture<DomainNavbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainNavbar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DomainNavbar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
