
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Professeur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  specialite: string;
  departementId: number;
  nomDepartement: string;
}

export interface ProfesseurRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  specialite: string;
  departementId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProfesseurService {

  private apiUrl = 'http://localhost:8081/api/professeurs';

  constructor(private http: HttpClient) {}

  afficherTous(): Observable<Professeur[]> {
    return this.http.get<Professeur[]>(this.apiUrl);
  }

  compterProfesseurs(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`);
  }

  ajouter(professeur: ProfesseurRequest): Observable<Professeur> {
    return this.http.post<Professeur>(this.apiUrl, professeur);
  }

  modifier(id: number, professeur: ProfesseurRequest): Observable<Professeur> {
    return this.http.put<Professeur>(`${this.apiUrl}/${id}`, professeur);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
