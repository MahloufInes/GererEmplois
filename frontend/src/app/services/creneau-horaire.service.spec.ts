import { TestBed } from '@angular/core/testing';

import { CreneauHoraireService } from './creneau-horaire.service';

describe('CreneauHoraireService', () => {
  let service: CreneauHoraireService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CreneauHoraireService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
