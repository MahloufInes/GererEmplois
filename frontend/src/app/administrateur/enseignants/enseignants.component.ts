
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import {
  Professeur,
  ProfesseurService,
  ProfesseurRequest
} from '../../services/professeur.service';

import {
  Departement,
  DepartementService
} from '../../services/departement.service';

import {
  Matiere,
  MatiereService
} from '../../services/matiere.service';

interface PreferenceMatiere {
  id: number;
  professeur: {
    id: number;
  };
  matiere: {
    id: number;
    nom: string;
  };
}

@Component({
  selector: 'app-enseignants',
  templateUrl: './enseignants.component.html',
  styleUrls: ['./enseignants.component.css']
})
export class EnseignantsComponent implements OnInit {

  professeurs: Professeur[] = [];
  departements: Departement[] = [];

  chargement = false;
  messageErreur = '';
  messageSucces = '';

  afficherFormulaire = false;
  enregistrement = false;

  motDePasseTemporaire = '';

  nouveauProfesseur: ProfesseurRequest = {
    nom: '',
    prenom: '',
    email: '',
    specialite: '',
    departementId: 0
  };

  // Gestion des matières préférées
  afficherPreferences = false;
  professeurSelectionne: Professeur | null = null;

  matieres: Matiere[] = [];
  preferences: PreferenceMatiere[] = [];

  matiereSelectionnee = 0;
  chargementPreferences = false;
  enregistrementPreference = false;

  private apiPreferences =
    'http://localhost:8081/api/preferences-matieres';

  constructor(
    private professeurService: ProfesseurService,
    private departementService: DepartementService,
    private matiereService: MatiereService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.chargerProfesseurs();
    this.chargerDepartements();
  }

  chargerProfesseurs(): void {
    this.chargement = true;
    this.messageErreur = '';

    this.professeurService.afficherTous().subscribe({
      next: (data) => {
        this.professeurs = data;
        this.chargement = false;
      },
      error: (error) => {
        console.error('Erreur de chargement', error);
        this.messageErreur = 'Impossible de charger les professeurs.';
        this.chargement = false;
      }
    });
  }

  chargerDepartements(): void {
    this.departementService.afficherTous().subscribe({
      next: (data) => {
        this.departements = data;
      },
      error: (error) => {
        console.error('Erreur départements', error);
        this.messageErreur = 'Impossible de charger les départements.';
      }
    });
  }

  ouvrirFormulaire(): void {
    this.afficherFormulaire = true;
    this.messageErreur = '';
    this.messageSucces = '';
    this.motDePasseTemporaire = '';
  }

  annuler(): void {
    this.afficherFormulaire = false;
    this.reinitialiserFormulaire();
  }

  reinitialiserFormulaire(): void {
    this.nouveauProfesseur = {
      nom: '',
      prenom: '',
      email: '',
      specialite: '',
      departementId: 0
    };
  }

  ajouterProfesseur(): void {
    this.messageErreur = '';
    this.messageSucces = '';
    this.motDePasseTemporaire = '';

    if (
      !this.nouveauProfesseur.nom.trim() ||
      !this.nouveauProfesseur.prenom.trim() ||
      !this.nouveauProfesseur.email.trim() ||
      !this.nouveauProfesseur.specialite.trim() ||
      this.nouveauProfesseur.departementId <= 0
    ) {
      this.messageErreur = 'Veuillez remplir tous les champs.';
      return;
    }

    const emailValide =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
        this.nouveauProfesseur.email
      );

    if (!emailValide) {
      this.messageErreur = 'Veuillez saisir une adresse email valide.';
      return;
    }

    this.enregistrement = true;

    this.professeurService.ajouter(this.nouveauProfesseur).subscribe({
      next: (response) => {
        this.messageSucces = 'Enseignant ajouté avec succès !';

        this.motDePasseTemporaire =
          response.motDePasseTemporaire;

        this.enregistrement = false;
        this.afficherFormulaire = false;

        this.reinitialiserFormulaire();
        this.chargerProfesseurs();
      },

      error: (error) => {
        console.error('Erreur ajout professeur', error);

        this.enregistrement = false;

        if (error.status === 409) {
          this.messageErreur = 'Cette adresse email existe déjà.';
        } else if (error.status === 404) {
          this.messageErreur = 'Département introuvable.';
        } else {
          this.messageErreur =
            error.error?.message ||
            'Erreur lors de l’ajout de l’enseignant.';
        }
      }
    });
  }

  copierMotDePasse(): void {
    if (!this.motDePasseTemporaire) {
      return;
    }

    navigator.clipboard.writeText(this.motDePasseTemporaire)
      .then(() => {
        this.messageSucces = 'Mot de passe copié avec succès !';
      })
      .catch(() => {
        this.messageErreur = 'Impossible de copier le mot de passe.';
      });
  }

  // Ouvrir la gestion des préférences d'un professeur
  ouvrirPreferences(professeur: Professeur): void {
    this.professeurSelectionne = professeur;
    this.afficherPreferences = true;

    this.messageErreur = '';
    this.messageSucces = '';

    this.matiereSelectionnee = 0;

    this.chargerMatieres();
    this.chargerPreferences();
  }

  fermerPreferences(): void {
    this.afficherPreferences = false;
    this.professeurSelectionne = null;
    this.preferences = [];
    this.matiereSelectionnee = 0;
  }

  // Charger toutes les matières
  chargerMatieres(): void {
    this.matiereService.getAll().subscribe({
      next: (data) => {
        this.matieres = data;
      },
      error: (error) => {
        console.error('Erreur chargement matières', error);
        this.messageErreur = 'Impossible de charger les matières.';
      }
    });
  }

  // Charger les préférences du professeur sélectionné
  chargerPreferences(): void {
    if (!this.professeurSelectionne) {
      return;
    }

    this.chargementPreferences = true;

    const professeurId = this.professeurSelectionne.id;

    this.http.get<PreferenceMatiere[]>(
      `${this.apiPreferences}/professeur/${professeurId}`
    ).subscribe({
      next: (data) => {
        this.preferences = data;
        this.chargementPreferences = false;
      },
      error: (error) => {
        console.error('Erreur chargement préférences', error);
        this.messageErreur =
          'Impossible de charger les matières préférées.';
        this.chargementPreferences = false;
      }
    });
  }

  // Vérifier si une matière est déjà préférée
  estMatiereSelectionnee(matiereId: number): boolean {
    return this.preferences.some(
      preference => preference.matiere.id === matiereId
    );
  }

  // Ajouter une préférence
  ajouterPreference(): void {
    if (
      !this.professeurSelectionne ||
      this.matiereSelectionnee <= 0
    ) {
      this.messageErreur = 'Veuillez choisir une matière.';
      return;
    }

    if (this.estMatiereSelectionnee(this.matiereSelectionnee)) {
      this.messageErreur = 'Cette matière est déjà sélectionnée.';
      return;
    }

    this.enregistrementPreference = true;
    this.messageErreur = '';

    const params = new HttpParams()
      .set('professeurId', this.professeurSelectionne.id)
      .set('matiereId', this.matiereSelectionnee);

    this.http.post<PreferenceMatiere>(
      this.apiPreferences,
      null,
      { params }
    ).subscribe({
      next: () => {
        this.messageSucces = 'Matière ajoutée aux préférences !';
        this.matiereSelectionnee = 0;
        this.enregistrementPreference = false;
        this.chargerPreferences();
      },
      error: (error) => {
        console.error('Erreur ajout préférence', error);
        this.messageErreur =
          error.error?.message ||
          'Erreur lors de l’ajout de la préférence.';
        this.enregistrementPreference = false;
      }
    });
  }

  // Supprimer une préférence
  supprimerPreference(matiereId: number): void {
    if (!this.professeurSelectionne) {
      return;
    }

    const params = new HttpParams()
      .set('professeurId', this.professeurSelectionne.id)
      .set('matiereId', matiereId);

    this.http.delete<void>(
      this.apiPreferences,
      { params }
    ).subscribe({
      next: () => {
        this.messageSucces = 'Préférence supprimée avec succès !';
        this.chargerPreferences();
      },
      error: (error) => {
        console.error('Erreur suppression préférence', error);
        this.messageErreur =
          error.error?.message ||
          'Erreur lors de la suppression.';
      }
    });
  }

  // Matières qui ne sont pas encore sélectionnées
  get matieresDisponibles(): Matiere[] {
    return this.matieres.filter(
      matiere => !this.estMatiereSelectionnee(matiere.id)
    );
  }
}
