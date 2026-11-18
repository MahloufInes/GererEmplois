import { Component, OnInit } from '@angular/core';
import { EtudiantService } from '../../app/services/etudiant.service';

interface Etudiant {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  matricule: string;
  groupeId: number;
  groupe: string;
  filiere: string;
}

@Component({
  selector: 'app-dashboard-etudiant',
  templateUrl: './dashboard-etudiant.component.html',
  styleUrls: ['./dashboard-etudiant.component.css']
})
export class DashboardEtudiantComponent implements OnInit {

  etudiants: Etudiant[] = [];
  chargement = false;
  messageErreur = '';

  constructor(private etudiantService: EtudiantService) {}

  ngOnInit(): void {
    this.chargement = true;

    this.etudiantService.afficherTous().subscribe({
      next: (data: Etudiant[]) => {
        this.etudiants = data;
        this.chargement = false;
      },
      error: (err: any) => {
        console.error(err);
        this.messageErreur = 'Impossible de charger les étudiants.';
        this.chargement = false;
      }
    });
  }
}