import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardEtudiantComponent } from './dashboard-etudiant.component';

describe('DashboardEtudiantComponent', () => {
  let component: DashboardEtudiantComponent;
  let fixture: ComponentFixture<DashboardEtudiantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DashboardEtudiantComponent]
    });
    fixture = TestBed.createComponent(DashboardEtudiantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
