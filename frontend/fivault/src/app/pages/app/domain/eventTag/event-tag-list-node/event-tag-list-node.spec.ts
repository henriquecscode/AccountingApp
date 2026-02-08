import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTagListNode } from './event-tag-list-node';

describe('EventTagListNode', () => {
  let component: EventTagListNode;
  let fixture: ComponentFixture<EventTagListNode>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventTagListNode]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventTagListNode);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
