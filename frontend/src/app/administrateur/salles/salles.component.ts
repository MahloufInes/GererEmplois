
import { Component, OnInit } from '@angular/core';
import { Salle,
  SalleService,
  SalleRequest, } from '../../services/salle.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-salles',
  templateUrl: './salles.component.html',
  styleUrls: ['./salles.component.css']
})
export class SallesComponent implements OnInit {

  salles: Salle[] = [];
  chargement = false;
  messageErreur = '';

  afficherFormulaire = false;
  modeModification = false;
  salleIdModifie: number | null = null;

  nouvelleSalle: SalleRequest = {
  nom: '',
  capacite: 30,
  type: 'COURS'
};

  constructor(private salleService: SalleService) {}

  ngOnInit(): void {
    this.chargerSalles();
  }

  chargerSalles(): void {
    this.chargement = true;
    this.messageErreur = '';

    this.salleService.afficherToutes().subscribe({
      next: data => {
        this.salles = data;
        this.chargement = false;
      },
      error: error => {
        console.error('Erreur de chargement', error);
        this.messageErreur = 'Impossible de charger les salles.';
        this.chargement = false;
      }
    });
  }

  ouvrirFormulaire(): void {
    this.afficherFormulaire = true;
    this.modeModification = false;
    this.salleIdModifie = null;

    this.nouvelleSalle = {
      nom: '',
      capacite: 30,
      type: 'COURS'
    };
  }

  fermerFormulaire(): void {
    this.afficherFormulaire = false;
  }

 
enregistrerSalle(): void {

  if (!this.nouvelleSalle.nom.trim() ||
      this.nouvelleSalle.capacite <= 0 ||
      !this.nouvelleSalle.type) {

    Swal.fire({
      icon: 'warning',
      title: 'Attention !',
      text: 'Veuillez remplir correctement tous les champs.',
      confirmButtonColor: '#5142a5'
    });

    return;
  }

  const operation = this.modeModification && this.salleIdModifie !== null
    ? this.salleService.modifier(this.salleIdModifie, this.nouvelleSalle)
    : this.salleService.ajouter(this.nouvelleSalle);

  operation.subscribe({
    next: () => {

      Swal.fire({
        icon: 'success',
        title: this.modeModification
          ? 'Salle modifiée !'
          : 'Salle ajoutée !',
        text: this.modeModification
          ? 'Les modifications ont été enregistrées avec succès.'
          : 'La nouvelle salle a été ajoutée avec succès.',
        confirmButtonText: 'Parfait !',
        confirmButtonColor: '#5142a5',
        timer: 2500,
        timerProgressBar: true
      });

      this.fermerFormulaire();
      this.chargerSalles();
    },

    error: error => {
      console.error(error);

      Swal.fire({
        icon: 'error',
        title: 'Erreur !',
        text: 'Impossible d’enregistrer la salle.',
        confirmButtonColor: '#5142a5'
      });
    }
  });
}


  modifierSalle(salle: Salle): void {

    this.afficherFormulaire = true;
    this.modeModification = true;
    this.salleIdModifie = salle.id;

    this.nouvelleSalle = {
      nom: salle.nom,
      capacite: salle.capacite,
      type: salle.type
    };
  }

  
supprimerSalle(id: number): void {

  Swal.fire({
    title: 'Supprimer cette salle ?',
    text: 'Cette action est irréversible.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc3545',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Oui, supprimer',
    cancelButtonText: 'Annuler'
  }).then(result => {

    if (result.isConfirmed) {

      this.salleService.supprimer(id).subscribe({
        next: () => {

          Swal.fire({
            icon: 'success',
            title: 'Salle supprimée !',
            text: 'La salle a été supprimée avec succès.',
            confirmButtonColor: '#5142a5'
          });

          this.chargerSalles();
        },

        error: error => {
          console.error(error);

          Swal.fire({
            icon: 'error',
            title: 'Erreur !',
            text: 'Impossible de supprimer cette salle.',
            confirmButtonColor: '#5142a5'
          });
        }
      });

    }
  });
}

}
