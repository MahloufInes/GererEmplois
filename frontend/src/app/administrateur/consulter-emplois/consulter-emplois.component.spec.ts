import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsulterEmploisComponent } from './consulter-emplois.component';

describe('ConsulterEmploisComponent', () => {
  let component: ConsulterEmploisComponent;
  let fixture: ComponentFixture<ConsulterEmploisComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConsulterEmploisComponent]
    });
    fixture = TestBed.createComponent(ConsulterEmploisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
