import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MesDisponibilitesComponent } from './mes-disponibilites.component';

describe('MesDisponibilitesComponent', () => {
  let component: MesDisponibilitesComponent;
  let fixture: ComponentFixture<MesDisponibilitesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MesDisponibilitesComponent]
    });
    fixture = TestBed.createComponent(MesDisponibilitesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
