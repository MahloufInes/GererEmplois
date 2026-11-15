
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

interface Seance {
  id?: number;
  date?: string | null;
  jour: string;
  heureDebut: string;
  heureFin: string;
  type?: string;
  statut?: string;
  matiereNom: string;
  professeurNom: string;
  salleNom: string;
  groupeNom: string;
}

interface Horaire {
  debut: string;
  fin: string;
}

@Component({
  selector: 'app-generer-emploi',
  templateUrl: './generer-emploi.component.html',
  styleUrls: ['./generer-emploi.component.css']
})
export class GenererEmploiComponent implements OnInit {

  private apiFilieres = 'http://localhost:8081/api/filieres';
  private apiGeneration = 'http://localhost:8081/api/generation/generer';
  private apiSeances = 'http://localhost:8081/api/generation/emploi';

  filieres: Filiere[] = [];
  seances: Seance[] = [];

  filiereId: number | null = null;
  semestre = 'S1';

  chargementFilieres = false;
  chargementSeances = false;
  generationEnCours = false;

  erreur = '';
  succes = '';

  resultat: GenerationResponse | null = null;

  administrateurId = 1;

  // Ordre des jours dans le tableau
  jours = [
    'LUNDI',
    'MARDI',
    'MERCREDI',
    'JEUDI',
    'VENDREDI',
    'SAMEDI'
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerFilieres();
  }

  // Regrouper les séances par groupe
  get seancesParGroupe(): [string, Seance[]][] {
    const map: { [groupe: string]: Seance[] } = {};

    for (const s of this.seances) {
      const cle = s.groupeNom || 'Sans groupe';
      (map[cle] ||= []).push(s);
    }

    return Object.entries(map).sort(
      (a, b) => a[0].localeCompare(b[0])
    );
  }

  // Récupérer les horaires disponibles
  get horaires(): Horaire[] {
    const map = new Map<string, string>();

    for (const s of this.seances) {
      map.set(s.heureDebut, s.heureFin);
    }

    return Array.from(map.entries())
      .map(([debut, fin]) => ({ debut, fin }))
      .sort((a, b) => a.debut.localeCompare(b.debut));
  }

  // Normaliser les noms des jours
  normaliserJour(jour: string): string {
    return (jour || '')
      .toUpperCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .trim();
  }

  // Vérifier si une séance correspond à une cellule
  trouverSeance(
    groupeSeances: Seance[],
    jour: string,
    horaire: Horaire
  ): Seance | undefined {

    return groupeSeances.find(s =>
      this.normaliserJour(s.jour) === jour &&
      s.heureDebut === horaire.debut
    );
  }

  // Afficher le type de séance
  afficherType(type?: string): string {
    const valeur = (type || '').toUpperCase();

    if (valeur.includes('TP')) {
      return 'TP';
    }

    if (valeur.includes('COURS')) {
      return 'COURS';
    }

    return valeur || 'SÉANCE';
  }

  // Chargement des filières
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

  // Génération de l'emploi du temps
  generer(): void {
    if (this.filiereId === null) {
      this.erreur = 'Veuillez sélectionner une filière.';
      return;
    }

    this.erreur = '';
    this.succes = '';
    this.resultat = null;
    this.seances = [];
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
        this.chargerSeances(response.emploiDuTempsId);
      },
      error: err => {
        console.error('Erreur génération :', err);
        this.erreur = this.extraireErreur(err);
        this.generationEnCours = false;
      }
    });
  }

  // Chargement des séances générées
  chargerSeances(emploiId: number): void {
    this.chargementSeances = true;

    this.http.get<Seance[]>(
      `${this.apiSeances}/${emploiId}/seances`
    ).subscribe({
      next: data => {
        this.seances = data;
        this.chargementSeances = false;
      },
      error: err => {
        console.error('Erreur chargement séances :', err);
        this.erreur =
          'L’emploi du temps est généré, mais impossible de charger les séances.';
        this.chargementSeances = false;
      }
    });
  }

  private extraireErreur(err: any): string {
    const backendError = err.error;

    if (typeof backendError === 'string') {
      return backendError;
    }

    if (backendError && typeof backendError === 'object') {
      return backendError.message
        || backendError.error
        || JSON.stringify(backendError);
    }

    return err.message
      || 'Une erreur est survenue pendant la génération de l’emploi du temps.';
  }
}
