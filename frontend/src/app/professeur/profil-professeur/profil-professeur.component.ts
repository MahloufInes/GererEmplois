
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface Professeur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  specialite: string;
  departementId: number;
  nomDepartement: string;
}

@Component({
  selector: 'app-profil-professeur',
  templateUrl: './profil-professeur.component.html',
  styleUrls: ['./profil-professeur.component.css']
})
export class ProfilProfesseurComponent implements OnInit {

  professeur: Professeur | null = null;

  modeModification = false;
  chargement = false;
  enregistrement = false;

  messageSucces = '';
  messageErreur = '';

  formulaire = {
    nom: '',
    prenom: '',
    email: '',
    specialite: '',
    motDePasse: ''
  };

  private apiUrl = 'http://localhost:8081/api/professeurs/mon-profil';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerProfil();
  }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  chargerProfil(): void {
    this.chargement = true;
    this.messageErreur = '';

    this.http.get<Professeur>(this.apiUrl, {
      headers: this.getHeaders()
    }).subscribe({
      next: (data) => {
        this.professeur = data;

        this.formulaire = {
          nom: data.nom,
          prenom: data.prenom,
          email: data.email,
          specialite: data.specialite,
          motDePasse: ''
        };

        this.chargement = false;
      },
      error: (error) => {
        console.error(error);
        this.messageErreur = 'Impossible de charger votre profil.';
        this.chargement = false;
      }
    });
  }

  activerModification(): void {
    if (!this.professeur) return;

    this.formulaire = {
      nom: this.professeur.nom,
      prenom: this.professeur.prenom,
      email: this.professeur.email,
      specialite: this.professeur.specialite,
      motDePasse: ''
    };

    this.modeModification = true;
    this.messageErreur = '';
    this.messageSucces = '';
  }

  annulerModification(): void {
    this.modeModification = false;
    this.messageErreur = '';
    this.chargerProfil();
  }

  enregistrerModifications(): void {
    if (!this.professeur) return;

    this.messageErreur = '';
    this.messageSucces = '';

    const donnees = {
      nom: this.formulaire.nom,
      prenom: this.formulaire.prenom,
      email: this.formulaire.email,
      specialite: this.formulaire.specialite,
      motDePasse: this.formulaire.motDePasse
    };

    this.enregistrement = true;

    this.http.put<Professeur>(this.apiUrl, donnees, {
      headers: this.getHeaders()
    }).subscribe({
      next: (data) => {
        this.professeur = data;

        this.formulaire.motDePasse = '';
        this.modeModification = false;
        this.enregistrement = false;
        this.messageSucces = 'Votre profil a été modifié avec succès !';

        // Actualiser les informations enregistrées localement
        const user = localStorage.getItem('user');

        if (user) {
          const utilisateur = JSON.parse(user);

          utilisateur.nom = data.nom;
          utilisateur.prenom = data.prenom;
          utilisateur.email = data.email;

          localStorage.setItem('user', JSON.stringify(utilisateur));
        }
      },
      error: (error) => {
        console.error(error);

        this.messageErreur =
          error.status === 409
            ? 'Cet email est déjà utilisé.'
            : 'Erreur lors de la modification du profil.';

        this.enregistrement = false;
      }
    });
  }
}
