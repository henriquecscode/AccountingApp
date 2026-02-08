import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTagDetail } from './event-tag-detail';

describe('EventTagDetail', () => {
  let component: EventTagDetail;
  let fixture: ComponentFixture<EventTagDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventTagDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventTagDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
