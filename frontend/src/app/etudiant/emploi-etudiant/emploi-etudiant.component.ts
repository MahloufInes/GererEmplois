
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
  matiere: string;
  professeur: string;
  salle: string;
  groupe: string;
}

@Component({
  selector: 'app-emploi-etudiant',
  templateUrl: './emploi-etudiant.component.html',
  styleUrls: ['./emploi-etudiant.component.css']
})
export class EmploiEtudiantComponent implements OnInit {

  seances: Seance[] = [];
  chargement = true;
  messageErreur = '';

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
    this.chargerEmploi();
  }

  chargerEmploi(): void {
    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<Seance[]>(
      'http://localhost:8081/api/etudiants/mon-emploi',
      { headers }
    ).subscribe({
      next: data => {
        this.seances = data;
        this.chargement = false;
      },
      error: err => {
        console.error(err);
        this.messageErreur =
          err.error?.message || 'Impossible de charger votre emploi du temps.';
        this.chargement = false;
      }
    });
  }

  getSeances(jour: string): Seance[] {
    return this.seances
      .filter(s => s.jour === jour)
      .sort((a, b) =>
        a.heureDebut.localeCompare(b.heureDebut)
      );
  }
}
