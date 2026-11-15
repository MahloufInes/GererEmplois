
import { Component, OnInit } from '@angular/core';
import {
  Professeur,
  ProfesseurService,
  ProfesseurRequest
} from '../../services/professeur.service';

import {
  Departement,
  DepartementService
} from '../../services/departement.service';

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

  nouveauProfesseur: ProfesseurRequest = {
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    specialite: '',
    departementId: 0
  };

  constructor(
    private professeurService: ProfesseurService,
    private departementService: DepartementService
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
      motDePasse: '',
      specialite: '',
      departementId: 0
    };
  }

  ajouterProfesseur(): void {
    this.messageErreur = '';
    this.messageSucces = '';

    if (
      !this.nouveauProfesseur.nom.trim() ||
      !this.nouveauProfesseur.prenom.trim() ||
      !this.nouveauProfesseur.email.trim() ||
      !this.nouveauProfesseur.motDePasse.trim() ||
      !this.nouveauProfesseur.specialite.trim() ||
      this.nouveauProfesseur.departementId <= 0
    ) {
      this.messageErreur = 'Veuillez remplir tous les champs.';
      return;
    }

    const emailValide = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
      this.nouveauProfesseur.email
    );

    if (!emailValide) {
      this.messageErreur = 'Veuillez saisir une adresse email valide.';
      return;
    }

    this.enregistrement = true;

    this.professeurService.ajouter(this.nouveauProfesseur).subscribe({
      next: () => {
        this.messageSucces = 'Enseignant ajouté avec succès !';
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
          this.messageErreur = error.error?.message ||
            'Erreur lors de l’ajout de l’enseignant.';
        }
      }
    });
  }
}
