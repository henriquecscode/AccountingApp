import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTagListContainer } from './event-tag-list-container';

describe('EventTagListContainer', () => {
  let component: EventTagListContainer;
  let fixture: ComponentFixture<EventTagListContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventTagListContainer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventTagListContainer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
