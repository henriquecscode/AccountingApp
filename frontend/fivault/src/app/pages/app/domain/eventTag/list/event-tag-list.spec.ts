import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTagList } from './event-tag-list';

describe('EventTagList', () => {
  let component: EventTagList;
  let fixture: ComponentFixture<EventTagList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventTagList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventTagList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
