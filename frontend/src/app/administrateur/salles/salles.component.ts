
import { Component, OnInit } from '@angular/core';
import { Salle, SalleService } from '../../services/salle.service';

@Component({
  selector: 'app-salles',
  templateUrl: './salles.component.html',
  styleUrls: ['./salles.component.css']
})
export class SallesComponent implements OnInit {

  salles: Salle[] = [];
  chargement = false;
  messageErreur = '';

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

  afficherFormulaire = false;
  modeModification = false;
  salleIdModifie: number | null = null;

  nouvelleSalle = {
    nom: '',
    capacite: 30,
    type: 'TD'
  };

  ouvrirFormulaire(): void {
    this.afficherFormulaire = true;
    this.modeModification = false;
    this.salleIdModifie = null;

    this.nouvelleSalle = {
      nom: '',
      capacite: 30,
      type: 'TD'
    };
  }

  fermerFormulaire(): void {
    this.afficherFormulaire = false;
  }

 
enregistrerSalle(): void {
  if (!this.nouvelleSalle.nom.trim() ||
      this.nouvelleSalle.capacite <= 0 ||
      !this.nouvelleSalle.type) {
    alert('Veuillez remplir correctement tous les champs.');
    return;
  }

  const operation = this.modeModification && this.salleIdModifie !== null
    ? this.salleService.modifier(this.salleIdModifie, this.nouvelleSalle)
    : this.salleService.ajouter(this.nouvelleSalle);

  operation.subscribe({
    next: () => {
      alert(this.modeModification
        ? 'Salle modifiée avec succès !'
        : 'Salle ajoutée avec succès !');

      this.fermerFormulaire();
      this.chargerSalles();
    },
    error: error => {
      console.error(error);
      alert('Erreur lors de l’enregistrement de la salle.');
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
  const confirmation = confirm('Voulez-vous vraiment supprimer cette salle ?');

  if (confirmation) {
    this.salleService.supprimer(id).subscribe({
      next: () => {
        alert('Salle supprimée avec succès !');
        this.chargerSalles();
      },
      error: error => {
        console.error(error);
        alert('Impossible de supprimer cette salle.');
      }
    });
  }
}


}
