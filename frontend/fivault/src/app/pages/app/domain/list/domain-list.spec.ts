import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DomainList } from '../list/domain-list'

describe('Domain', () => {
  let component: DomainList;
  let fixture: ComponentFixture<DomainList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainList]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DomainList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
