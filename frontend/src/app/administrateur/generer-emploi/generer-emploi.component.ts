
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

interface Filiere {
  id: number;
  nom: string;
}

interface GenerationResponse {
  emploiDuTempsId: number;
  semestre: string;
  nombreSeances: number;
  message: string;
}

@Component({
  selector: 'app-generer-emploi',
  templateUrl: './generer-emploi.component.html',
  styleUrls: ['./generer-emploi.component.css']
})
export class GenererEmploiComponent implements OnInit {

  private apiFilieres = 'http://localhost:8081/api/filieres';
  private apiGeneration = 'http://localhost:8081/api/generation/generer';

  filieres: Filiere[] = [];

  filiereId: number | null = null;
  semestre = 'S1';

  chargementFilieres = false;
  generationEnCours = false;

  erreur = '';
  succes = '';

  resultat: GenerationResponse | null = null;

  // À remplacer par l'identifiant réel de l'administrateur connecté
  administrateurId = 1;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerFilieres();
  }

  chargerFilieres(): void {
    this.chargementFilieres = true;
    this.erreur = '';

    this.http.get<Filiere[]>(this.apiFilieres).subscribe({
      next: data => {
        this.filieres = data;
        this.chargementFilieres = false;
      },
      error: err => {
        console.error('Erreur chargement filières :', err);
        this.erreur = 'Impossible de charger les filières.';
        this.chargementFilieres = false;
      }
    });
  }

  generer(): void {

    if (this.filiereId === null) {
      this.erreur = 'Veuillez sélectionner une filière.';
      return;
    }

    this.erreur = '';
    this.succes = '';
    this.resultat = null;
    this.generationEnCours = true;

    const params = new HttpParams()
      .set('filiereId', this.filiereId.toString())
      .set('semestre', this.semestre)
      .set('administrateurId', this.administrateurId.toString());

    this.http.post<GenerationResponse>(
      this.apiGeneration,
      null,
      { params }
    ).subscribe({
      next: response => {
        this.resultat = response;
        this.succes = response.message;
        this.generationEnCours = false;
      },
      error: err => {
        console.error('Erreur génération :', err);

        this.erreur =
          err.error?.message ||
          err.error ||
          'Une erreur est survenue pendant la génération de l’emploi du temps.';

        this.generationEnCours = false;
      }
    });
  }
}
