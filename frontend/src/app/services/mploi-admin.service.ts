import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SeanceAdmin {
  id: number;
  emploiId: number;
  semestre: string;
  jour: string;
  heureDebut: string;
  heureFin: string;
  type: string;       // COURS ou TP
  statut: string;
  matiere: string;
  professeur: string;
  salle: string;
  groupe: string;
}

@Injectable({ providedIn: 'root' })
export class EmploiAdminService {

  private apiUrl = 'http://localhost:8081/api/emplois-du-temps/seances';

  constructor(private http: HttpClient) {}

  afficherToutes(): Observable<SeanceAdmin[]> {
    return this.http.get<SeanceAdmin[]>(this.apiUrl);
  }
}