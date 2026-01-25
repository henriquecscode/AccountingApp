import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainDetail } from './domain-detail';

describe('DomainDetail', () => {
  let component: DomainDetail;
  let fixture: ComponentFixture<DomainDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DomainDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
