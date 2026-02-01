import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainLayout } from './domain-layout';

describe('DomainLayout', () => {
  let component: DomainLayout;
  let fixture: ComponentFixture<DomainLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainLayout]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DomainLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
