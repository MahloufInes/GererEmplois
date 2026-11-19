
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

// ========================
// INTERFACES
// ========================

interface Departement {
  id: number;
  code: string;
  nom: string;
}

interface Filiere {
  id: number;
  nom: string;
  departementId: number;
}

interface Groupe {
  id: number;
  nom: string;
  effectif: number;
  filiereId: number;
  filiere: string;
  semestres: string[];
}

interface Seance {
  id: number;
  date: string | null;
  jour: string;
  heureDebut: string;
  heureFin: string;
  type: string;
  statut: string;
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
  selector: 'app-consulter-emplois',
  templateUrl: './consulter-emplois.component.html',
  styleUrls: ['./consulter-emplois.component.css']
})
export class ConsulterEmploisComponent implements OnInit {

  // URLs API
  private apiDepartements = 'http://localhost:8081/api/departements';
  private apiFilieres = 'http://localhost:8081/api/filieres';
  private apiGroupes = 'http://localhost:8081/api/groupes';
  private apiConsultation = 'http://localhost:8081/api/consultation';

  // Données
  departements: Departement[] = [];
  filieres: Filiere[] = [];
  groupes: Groupe[] = [];
  seances: Seance[] = [];

  // Sélections
  departementSelectionne: number | null = null;
  filiereSelectionnee: number | null = null;
  groupeSelectionne: number | null = null;
  semestreSelectionne: string | null = null;

  // États de chargement
  chargementDepartements = false;
  chargementFilieres = false;
  chargementGroupes = false;
  chargementSeances = false;

  erreur = '';

  // Jours
  jours: string[] = [
    'LUNDI',
    'MARDI',
    'MERCREDI',
    'JEUDI',
    'VENDREDI',
    'SAMEDI'
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerDepartements();
    this.chargerFilieres();
    this.chargerGroupes();
  }

  // ========================
  // DEPARTEMENTS
  // ========================

  chargerDepartements(): void {
    this.chargementDepartements = true;

    this.http.get<Departement[]>(this.apiDepartements).subscribe({
      next: (data) => {
        this.departements = data;
        this.chargementDepartements = false;
      },
      error: (err) => {
        console.error(err);
        this.erreur = 'Impossible de charger les départements.';
        this.chargementDepartements = false;
      }
    });
  }

  // ========================
  // FILIERES
  // ========================

  chargerFilieres(): void {
    this.chargementFilieres = true;

    this.http.get<Filiere[]>(this.apiFilieres).subscribe({
      next: (data) => {
        this.filieres = data;
        this.chargementFilieres = false;
      },
      error: (err) => {
        console.error(err);
        this.erreur = 'Impossible de charger les filières.';
        this.chargementFilieres = false;
      }
    });
  }

  get filieresFiltrees(): Filiere[] {
    if (this.departementSelectionne === null) {
      return [];
    }

    return this.filieres.filter(
      f => f.departementId === this.departementSelectionne
    );
  }

  // ========================
  // GROUPES
  // ========================

  chargerGroupes(): void {
    this.chargementGroupes = true;

    this.http.get<Groupe[]>(this.apiGroupes).subscribe({
      next: (data) => {
        this.groupes = data;
        this.chargementGroupes = false;
      },
      error: (err) => {
        console.error(err);
        this.erreur = 'Impossible de charger les groupes.';
        this.chargementGroupes = false;
      }
    });
  }

  get groupesFiltres(): Groupe[] {
    if (this.filiereSelectionnee === null) {
      return [];
    }

    return this.groupes.filter(
      g => g.filiereId === this.filiereSelectionnee
    );
  }

  // Nom du groupe sélectionné
  get groupeNomSelectionne(): string {
    const groupe = this.groupes.find(
      g => g.id === this.groupeSelectionne
    );

    return groupe ? groupe.nom : '';
  }

  // ========================
  // SELECTIONS
  // ========================

  selectionnerDepartement(id: number): void {
    this.departementSelectionne = id;
    this.filiereSelectionnee = null;
    this.groupeSelectionne = null;
    this.semestreSelectionne = null;
    this.seances = [];
    this.erreur = '';
  }

  selectionnerFiliere(id: number): void {
    this.filiereSelectionnee = id;
    this.groupeSelectionne = null;
    this.semestreSelectionne = null;
    this.seances = [];
    this.erreur = '';
  }

  selectionnerGroupe(id: number): void {
    this.groupeSelectionne = id;
    this.semestreSelectionne = null;
    this.seances = [];
    this.erreur = '';
  }

  // ========================
  // SEMESTRES
  // ========================

  get semestresDisponibles(): string[] {
    const groupe = this.groupes.find(
      g => g.id === this.groupeSelectionne
    );

    return groupe ? groupe.semestres : [];
  }

  // ========================
  // CONSULTER EMPLOI
  // ========================

  consulterEmploiDuTemps(semestre: string): void {

    if (this.groupeSelectionne === null) {
      return;
    }

    this.semestreSelectionne = semestre;
    this.seances = [];
    this.erreur = '';
    this.chargementSeances = true;

    const url =
      `${this.apiConsultation}/groupe/${this.groupeSelectionne}/semestre/${semestre}`;

    this.http.get<Seance[]>(url).subscribe({
      next: (data) => {
        this.seances = data;
        this.chargementSeances = false;
      },
      error: (err) => {
        console.error(err);
        this.erreur = 'Impossible de charger cet emploi du temps.';
        this.chargementSeances = false;
      }
    });
  }

  // ========================
  // HORAIRES
  // ========================

  get horaires(): Horaire[] {
    const map = new Map<string, string>();

    for (const seance of this.seances) {
      map.set(seance.heureDebut, seance.heureFin);
    }

    return Array.from(map.entries())
      .map(([debut, fin]) => ({ debut, fin }))
      .sort((a, b) => a.debut.localeCompare(b.debut));
  }

  // ========================
  // NORMALISER JOUR
  // ========================

  normaliserJour(jour: string): string {
    return (jour || '')
      .toUpperCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .trim();
  }

  // ========================
  // TROUVER SEANCE
  // ========================

  trouverSeance(jour: string, horaire: Horaire): Seance | undefined {
    return this.seances.find(seance =>
      this.normaliserJour(seance.jour) === this.normaliserJour(jour) &&
      seance.heureDebut === horaire.debut
    );
  }

  // ========================
  // TYPE DE SEANCE
  // ========================

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
}
