
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CreneauHoraire {
  id: number;
  jour: string;
  heureDebut: string;
  heureFin: string;
}

export interface CreneauHoraireRequest {
  jour: string;
  heureDebut: string;
  heureFin: string;
}

@Injectable({
  providedIn: 'root'
})
export class CreneauHoraireService {

  private apiUrl = 'http://localhost:8081/api/creneaux-horaires';

  constructor(private http: HttpClient) {}

  afficherTous(): Observable<CreneauHoraire[]> {
    return this.http.get<CreneauHoraire[]>(this.apiUrl);
  }

  ajouter(creneau: CreneauHoraireRequest): Observable<CreneauHoraire> {
    return this.http.post<CreneauHoraire>(this.apiUrl, creneau);
  }

  modifier(id: number, creneau: CreneauHoraireRequest): Observable<CreneauHoraire> {
    return this.http.put<CreneauHoraire>(`${this.apiUrl}/${id}`, creneau);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
