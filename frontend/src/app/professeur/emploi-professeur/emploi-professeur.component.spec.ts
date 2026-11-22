import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmploiProfesseurComponent } from './emploi-professeur.component';

describe('EmploiProfesseurComponent', () => {
  let component: EmploiProfesseurComponent;
  let fixture: ComponentFixture<EmploiProfesseurComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EmploiProfesseurComponent]
    });
    fixture = TestBed.createComponent(EmploiProfesseurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
