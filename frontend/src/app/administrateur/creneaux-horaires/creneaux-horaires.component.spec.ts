import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreneauxHorairesComponent } from './creneaux-horaires.component';

describe('CreneauxHorairesComponent', () => {
  let component: CreneauxHorairesComponent;
  let fixture: ComponentFixture<CreneauxHorairesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreneauxHorairesComponent]
    });
    fixture = TestBed.createComponent(CreneauxHorairesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
