
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Salles {
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

  afficherToutes(): Observable<Salles[]> {
    return this.http.get<Salles[]>(this.apiUrl);
  }

  compterSalles(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`);
  }

  ajouter(salle: SalleRequest): Observable<Salles> {
    return this.http.post<Salles>(this.apiUrl, salle);
  }

  modifier(id: number, salle: SalleRequest): Observable<Salles> {
    return this.http.put<Salles>(`${this.apiUrl}/${id}`, salle);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
