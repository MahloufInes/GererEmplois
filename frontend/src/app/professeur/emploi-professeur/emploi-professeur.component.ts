
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface Seance {
  id: number;
  date: string;
  jour: string;
  heureDebut: string;
  heureFin: string;
  type: string;
  statut: string;
  nomMatiere: string;
  nomProfesseur: string;
  nomSalle: string;
  nomGroupe: string;
}

@Component({
  selector: 'app-emploi-professeur',
  templateUrl: './emploi-professeur.component.html',
  styleUrls: ['./emploi-professeur.component.css']
})
export class EmploiProfesseurComponent implements OnInit {

  seances: Seance[] = [];
  chargement = false;
  messageErreur = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerEmploiDuTemps();
  }

  chargerEmploiDuTemps(): void {
    const user = localStorage.getItem('user');
    const token = localStorage.getItem('token');

    if (!user || !token) {
      this.messageErreur = 'Utilisateur non connecté.';
      return;
    }

    try {
      const utilisateur = JSON.parse(user);
      const professeurId = utilisateur.id;

      if (!professeurId || utilisateur.role !== 'PROFESSEUR') {
        this.messageErreur = 'Compte professeur invalide.';
        return;
      }

      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`
      });

      this.chargement = true;

      this.http.get<Seance[]>(
        `http://localhost:8081/api/generation/professeur/${professeurId}/seances`,
        { headers }
      ).subscribe({
        next: (data: Seance[]) => {
          this.seances = data;
          this.chargement = false;
        },
        error: (error: any) => {
          console.error('Erreur emploi du temps :', error);
          this.messageErreur = 'Impossible de charger votre emploi du temps.';
          this.chargement = false;
        }
      });

    } catch (error) {
      console.error(error);
      this.messageErreur = 'Informations utilisateur invalides.';
    }
  }
}
