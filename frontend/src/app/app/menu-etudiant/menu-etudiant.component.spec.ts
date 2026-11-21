import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuEtudiantComponent } from './menu-etudiant.component';

describe('MenuEtudiantComponent', () => {
  let component: MenuEtudiantComponent;
  let fixture: ComponentFixture<MenuEtudiantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MenuEtudiantComponent]
    });
    fixture = TestBed.createComponent(MenuEtudiantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
