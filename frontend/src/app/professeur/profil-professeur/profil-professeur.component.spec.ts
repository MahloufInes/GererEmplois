import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfilProfesseurComponent } from './profil-professeur.component';

describe('ProfilProfesseurComponent', () => {
  let component: ProfilProfesseurComponent;
  let fixture: ComponentFixture<ProfilProfesseurComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfilProfesseurComponent]
    });
    fixture = TestBed.createComponent(ProfilProfesseurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
