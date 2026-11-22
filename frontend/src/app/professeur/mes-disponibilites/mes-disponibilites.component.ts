
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface Creneau {
  id: number;
  jour: string;
  heureDebut: string;
  heureFin: string;
}

interface Disponibilite {
  id: number;
  creneauId: number;
  jour: string;
  heureDebut: string;
  heureFin: string;
}

@Component({
  selector: 'app-mes-disponibilites',
  templateUrl: './mes-disponibilites.component.html',
  styleUrls: ['./mes-disponibilites.component.css']
})
export class MesDisponibilitesComponent implements OnInit {

  private apiDisponibilites =
    'http://localhost:8081/api/professeurs/mes-disponibilites';

  private apiCreneaux =
    'http://localhost:8081/api/creneaux-horaires';

  disponibilites: Disponibilite[] = [];
  creneaux: Creneau[] = [];

  creneauId: number | null = null;
  disponibiliteId: number | null = null;

  chargement = false;
  messageSucces = '';
  messageErreur = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerCreneaux();
    this.chargerDisponibilites();
  }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  chargerCreneaux(): void {
    this.http.get<Creneau[]>(this.apiCreneaux, {
      headers: this.getHeaders()
    }).subscribe({
      next: data => {
        this.creneaux = data;
      },
      error: err => {
        console.error('Erreur chargement créneaux :', err);
        this.messageErreur = 'Impossible de charger les créneaux.';
      }
    });
  }

  chargerDisponibilites(): void {
    this.chargement = true;

    this.http.get<Disponibilite[]>(this.apiDisponibilites, {
      headers: this.getHeaders()
    }).subscribe({
      next: data => {
        this.disponibilites = data;
        this.chargement = false;
      },
      error: err => {
        console.error('Erreur chargement disponibilités :', err);
        this.chargement = false;
        this.messageErreur = 'Impossible de charger les disponibilités.';
      }
    });
  }

  enregistrer(): void {

    if (this.creneauId === null) {
      this.messageErreur = 'Veuillez sélectionner un créneau.';
      return;
    }

    this.messageErreur = '';
    this.messageSucces = '';

    const donnees = {
      creneauId: this.creneauId
    };

    if (this.disponibiliteId !== null) {

      // MODIFICATION
      this.http.put(
        `${this.apiDisponibilites}/${this.disponibiliteId}`,
        donnees,
        { headers: this.getHeaders() }
      ).subscribe({
        next: () => {
          this.messageSucces = 'Disponibilité modifiée avec succès.';
          this.annuler();
          this.chargerDisponibilites();
        },
        error: err => {
          console.error('Erreur modification :', err);

          this.messageErreur = err.status === 409
            ? 'Ce créneau est déjà utilisé.'
            : 'Erreur lors de la modification.';
        }
      });

    } else {

      // AJOUT
      this.http.post(
        this.apiDisponibilites,
        donnees,
        { headers: this.getHeaders() }
      ).subscribe({
        next: () => {
          this.messageSucces = 'Disponibilité ajoutée avec succès.';
          this.annuler();
          this.chargerDisponibilites();
        },
        error: err => {
          console.error('Erreur ajout :', err);

          this.messageErreur = err.status === 409
            ? 'Ce créneau est déjà ajouté.'
            : 'Erreur lors de l’ajout.';
        }
      });
    }
  }

  modifier(d: Disponibilite): void {
    this.disponibiliteId = d.id;
    this.creneauId = d.creneauId;

    this.messageErreur = '';
    this.messageSucces = '';
  }

  supprimer(id: number): void {

    if (!confirm('Voulez-vous supprimer cette disponibilité ?')) {
      return;
    }

    this.http.delete(`${this.apiDisponibilites}/${id}`, {
      headers: this.getHeaders()
    }).subscribe({
      next: () => {
        this.messageSucces = 'Disponibilité supprimée avec succès.';
        this.messageErreur = '';
        this.chargerDisponibilites();
      },
      error: err => {
        console.error('Erreur suppression :', err);
        this.messageErreur = 'Erreur lors de la suppression.';
      }
    });
  }

  annuler(): void {
    this.creneauId = null;
    this.disponibiliteId = null;
  }
}
