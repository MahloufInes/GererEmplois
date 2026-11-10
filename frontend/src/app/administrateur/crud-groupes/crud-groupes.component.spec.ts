import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudGroupesComponent } from './crud-groupes.component';

describe('CrudGroupesComponent', () => {
  let component: CrudGroupesComponent;
  let fixture: ComponentFixture<CrudGroupesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CrudGroupesComponent]
    });
    fixture = TestBed.createComponent(CrudGroupesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
