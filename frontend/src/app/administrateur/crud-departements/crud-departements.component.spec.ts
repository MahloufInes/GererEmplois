import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudDepartementsComponent } from './crud-departements.component';

describe('CrudDepartementsComponent', () => {
  let component: CrudDepartementsComponent;
  let fixture: ComponentFixture<CrudDepartementsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CrudDepartementsComponent]
    });
    fixture = TestBed.createComponent(CrudDepartementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
