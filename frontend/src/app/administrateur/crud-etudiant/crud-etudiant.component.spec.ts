import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudEtudiantComponent } from './crud-etudiant.component';

describe('CrudEtudiantComponent', () => {
  let component: CrudEtudiantComponent;
  let fixture: ComponentFixture<CrudEtudiantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CrudEtudiantComponent]
    });
    fixture = TestBed.createComponent(CrudEtudiantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
