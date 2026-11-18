import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuProfesseurComponent } from './menu-professeur.component';

describe('MenuProfesseurComponent', () => {
  let component: MenuProfesseurComponent;
  let fixture: ComponentFixture<MenuProfesseurComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MenuProfesseurComponent]
    });
    fixture = TestBed.createComponent(MenuProfesseurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
