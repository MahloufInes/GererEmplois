import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenererEmploiComponent } from './generer-emploi.component';

describe('GenererEmploiComponent', () => {
  let component: GenererEmploiComponent;
  let fixture: ComponentFixture<GenererEmploiComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenererEmploiComponent]
    });
    fixture = TestBed.createComponent(GenererEmploiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
