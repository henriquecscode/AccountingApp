import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventCategoryList } from './event-category-list';

describe('EventCategoryList', () => {
  let component: EventCategoryList;
  let fixture: ComponentFixture<EventCategoryList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventCategoryList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventCategoryList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
