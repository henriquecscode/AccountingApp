import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventCategoryListContainer } from './event-category-list-container';

describe('EventCategoryListContainer', () => {
  let component: EventCategoryListContainer;
  let fixture: ComponentFixture<EventCategoryListContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventCategoryListContainer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventCategoryListContainer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
