import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudFilieresComponent } from './crud-filieres.component';

describe('CrudFilieresComponent', () => {
  let component: CrudFilieresComponent;
  let fixture: ComponentFixture<CrudFilieresComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CrudFilieresComponent]
    });
    fixture = TestBed.createComponent(CrudFilieresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
