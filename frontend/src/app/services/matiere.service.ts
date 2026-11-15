import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Matiere {
  id: number;
  nom: string;
  departementId: number | null;
  departementNom: string | null;
}

export interface MatiereRequest {
  nom: string;
  departementId: number;
}

@Injectable({ providedIn: 'root' })
export class MatiereService {

  private api = 'http://localhost:8081/api/matieres';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Matiere[]> {
    return this.http.get<Matiere[]>(this.api);
  }

  getByDepartement(departementId: number): Observable<Matiere[]> {
    return this.http.get<Matiere[]>(`${this.api}/departement/${departementId}`);
  }

  ajouter(body: MatiereRequest): Observable<Matiere> {
    return this.http.post<Matiere>(this.api, body);
  }

  modifier(id: number, body: MatiereRequest): Observable<Matiere> {
    return this.http.put<Matiere>(`${this.api}/${id}`, body);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}