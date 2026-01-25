import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformDetail } from './platform-detail';

describe('PlatformDetail', () => {
  let component: PlatformDetail;
  let fixture: ComponentFixture<PlatformDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlatformDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlatformDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
