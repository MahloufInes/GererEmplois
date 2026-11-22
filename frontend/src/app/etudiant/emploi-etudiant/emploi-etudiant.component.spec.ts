import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmploiEtudiantComponent } from './emploi-etudiant.component';

describe('EmploiEtudiantComponent', () => {
  let component: EmploiEtudiantComponent;
  let fixture: ComponentFixture<EmploiEtudiantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EmploiEtudiantComponent]
    });
    fixture = TestBed.createComponent(EmploiEtudiantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
