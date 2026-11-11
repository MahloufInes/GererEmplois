
import { Component, OnInit } from '@angular/core';
import {
  CreneauHoraire,
  CreneauHoraireService
} from '../../services/creneau-horaire.service';

@Component({
  selector: 'app-creneaux-horaires',
  templateUrl: './creneaux-horaires.component.html',
  styleUrls: ['./creneaux-horaires.component.css']
})
export class CreneauxHorairesComponent implements OnInit {

  creneaux: CreneauHoraire[] = [];

  jours = [
    'LUNDI',
    'MARDI',
    'MERCREDI',
    'JEUDI',
    'VENDREDI',
    'SAMEDI'
  ];

  afficherFormulaire = false;
  modeModification = false;
  idModifie: number | null = null;

  nouveauCreneau = {
    jour: 'LUNDI',
    heureDebut: '08:00',
    heureFin: '09:30'
  };

  constructor(private service: CreneauHoraireService) {}

  ngOnInit(): void {
    this.chargerCreneaux();
  }

 
chargerCreneaux(): void {
  this.service.afficherTous().subscribe({
    next: data => {
      this.creneaux = data.sort((a, b) => {
        const jourA = this.jours.indexOf(a.jour);
        const jourB = this.jours.indexOf(b.jour);

        const ordreA = jourA === -1 ? 99 : jourA;
        const ordreB = jourB === -1 ? 99 : jourB;

        return ordreA - ordreB ||
          a.heureDebut.localeCompare(b.heureDebut);
      });
    },
    error: error => console.error('Erreur de chargement', error)
  });
}


  ouvrirFormulaire(): void {
    this.afficherFormulaire = true;
    this.modeModification = false;
    this.idModifie = null;

    this.nouveauCreneau = {
      jour: 'LUNDI',
      heureDebut: '08:00',
      heureFin: '09:30'
    };
  }

  fermerFormulaire(): void {
    this.afficherFormulaire = false;
  }

  enregistrerCreneau(): void {
    if (!this.nouveauCreneau.jour ||
        !this.nouveauCreneau.heureDebut ||
        !this.nouveauCreneau.heureFin) {
      alert('Veuillez remplir tous les champs.');
      return;
    }

    if (this.nouveauCreneau.heureDebut >= this.nouveauCreneau.heureFin) {
      alert("L'heure de début doit être avant l'heure de fin.");
      return;
    }

    const operation = this.modeModification && this.idModifie !== null
      ? this.service.modifier(this.idModifie, this.nouveauCreneau)
      : this.service.ajouter(this.nouveauCreneau);

    operation.subscribe({
      next: () => {
        alert('Créneau enregistré avec succès !');
        this.fermerFormulaire();
        this.chargerCreneaux();
      },
      error: error => {
        console.error(error);
        alert("Erreur lors de l'enregistrement du créneau.");
      }
    });
  }

  modifierCreneau(creneau: CreneauHoraire): void {
    this.afficherFormulaire = true;
    this.modeModification = true;
    this.idModifie = creneau.id;

    this.nouveauCreneau = {
      jour: creneau.jour,
      heureDebut: creneau.heureDebut.substring(0, 5),
      heureFin: creneau.heureFin.substring(0, 5)
    };
  }

  supprimerCreneau(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer ce créneau ?')) {
      this.service.supprimer(id).subscribe({
        next: () => {
          alert('Créneau supprimé avec succès !');
          this.chargerCreneaux();
        },
        error: error => {
          console.error(error);
          alert('Impossible de supprimer ce créneau.');
        }
      });
    }
  }

}
