import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudprofesseurComponent } from './crudprofesseur.component';

describe('CrudprofesseurComponent', () => {
  let component: CrudprofesseurComponent;
  let fixture: ComponentFixture<CrudprofesseurComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CrudprofesseurComponent]
    });
    fixture = TestBed.createComponent(CrudprofesseurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
