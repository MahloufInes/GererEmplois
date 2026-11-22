
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
  selector: 'app-profil-etudiant',
  templateUrl: './profil-etudiant.component.html',
  styleUrls: ['./profil-etudiant.component.css']
})
export class ProfilEtudiantComponent implements OnInit {

  etudiant: Etudiant | null = null;
  chargement = true;
  messageErreur = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerProfil();
  }

  chargerProfil(): void {
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
        this.messageErreur = 'Impossible de charger votre profil.';
        this.chargement = false;
      }
    });
  }
}
