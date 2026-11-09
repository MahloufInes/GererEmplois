import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrudSousAdminComponent } from './crud-sous-admin.component';

describe('CrudSousAdminComponent', () => {
  let component: CrudSousAdminComponent;
  let fixture: ComponentFixture<CrudSousAdminComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CrudSousAdminComponent]
    });
    fixture = TestBed.createComponent(CrudSousAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
