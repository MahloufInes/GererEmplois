
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Salle {
  id: number;
  nom: string;
  capacite: number;
  type: string;
}

export interface SalleRequest {
  nom: string;
  capacite: number;
  type: string;
}

@Injectable({
  providedIn: 'root'
})
export class SalleService {

  private apiUrl = 'http://localhost:8081/api/salles';

  constructor(private http: HttpClient) {}

  afficherToutes(): Observable<Salle[]> {
    return this.http.get<Salle[]>(this.apiUrl);
  }

  compterSalles(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`);
  }

  ajouter(salle: SalleRequest): Observable<Salle> {
    return this.http.post<Salle>(this.apiUrl, salle);
  }

  modifier(id: number, salle: SalleRequest): Observable<Salle> {
    return this.http.put<Salle>(`${this.apiUrl}/${id}`, salle);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
