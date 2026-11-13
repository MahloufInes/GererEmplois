import { Component, OnInit } from '@angular/core';
import { Professeur, ProfesseurService } from '../../services/professeur.service';

@Component({
  selector: 'app-enseignants',
  templateUrl: './enseignants.component.html',
  styleUrls: ['./enseignants.component.css']
})
export class EnseignantsComponent implements OnInit {

  professeurs: Professeur[] = [];
  chargement = false;
  messageErreur = '';

  constructor(private professeurService: ProfesseurService) {}

  ngOnInit(): void {
    this.chargerProfesseurs();
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
}
