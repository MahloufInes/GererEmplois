
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PreferenceMatiere {
  id: number;
  professeur: {
    id: number;
    nom: string;
    prenom: string;
  };
  matiere: {
    id: number;
    nom: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class PreferenceMatiereService {

  private apiUrl = 'http://localhost:8081/api/preferences-matieres';

  constructor(private http: HttpClient) {}

  ajouterPreference(
    professeurId: number,
    matiereId: number
  ): Observable<PreferenceMatiere> {

    const params = new HttpParams()
      .set('professeurId', professeurId)
      .set('matiereId', matiereId);

    return this.http.post<PreferenceMatiere>(
      this.apiUrl,
      null,
      { params }
    );
  }

  getByProfesseur(
    professeurId: number
  ): Observable<PreferenceMatiere[]> {

    return this.http.get<PreferenceMatiere[]>(
      `${this.apiUrl}/professeur/${professeurId}`
    );
  }

  getByMatiere(
    matiereId: number
  ): Observable<PreferenceMatiere[]> {

    return this.http.get<PreferenceMatiere[]>(
      `${this.apiUrl}/matiere/${matiereId}`
    );
  }

  supprimerPreference(
    professeurId: number,
    matiereId: number
  ): Observable<void> {

    const params = new HttpParams()
      .set('professeurId', professeurId)
      .set('matiereId', matiereId);

    return this.http.delete<void>(
      this.apiUrl,
      { params }
    );
  }
}
