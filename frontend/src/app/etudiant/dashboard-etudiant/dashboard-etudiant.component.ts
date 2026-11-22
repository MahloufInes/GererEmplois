
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

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

  etudiant: Etudiant | null = null;
  chargement = true;
  messageErreur = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerMonProfil();
  }

  chargerMonProfil(): void {
    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<Etudiant>(
      'http://localhost:8081/api/etudiants/mon-profil',
      { headers }
    ).subscribe({
      next: data => {
        this.etudiant = data;
        this.chargement = false;
      },
      error: err => {
        console.error(err);
        this.messageErreur = 'Impossible de charger vos informations.';
        this.chargement = false;
      }
    });
  }
}
