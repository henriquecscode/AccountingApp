import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformCreate } from './platform-create';

describe('PlatformCreate', () => {
  let component: PlatformCreate;
  let fixture: ComponentFixture<PlatformCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlatformCreate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlatformCreate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
