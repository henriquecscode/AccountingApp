import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventCategoryListNode } from './event-category-list-node';

describe('EventCategoryListNode', () => {
  let component: EventCategoryListNode;
  let fixture: ComponentFixture<EventCategoryListNode>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventCategoryListNode]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventCategoryListNode);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
