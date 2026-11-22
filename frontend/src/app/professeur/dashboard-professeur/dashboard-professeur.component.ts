
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
  selector: 'app-dashboard-professeur',
  templateUrl: './dashboard-professeur.component.html',
  styleUrls: ['./dashboard-professeur.component.css']
})
export class DashboardProfesseurComponent implements OnInit {

  seances: Seance[] = [];
  chargement = false;
  messageErreur = '';
  nomProfesseur = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerEmploiDuTemps();
  }

  get nombreGroupes(): number {
    return new Set(this.seances.map(s => s.nomGroupe).filter(Boolean)).size;
  }

  get nombreMatieres(): number {
    return new Set(this.seances.map(s => s.nomMatiere).filter(Boolean)).size;
  }

  get prochainesSeances(): Seance[] {
    const maintenant = new Date();

    return [...this.seances]
      .filter(s => {
        if (!s.date) return true;

        const dateSeance = new Date(`${s.date}T${s.heureDebut || '00:00'}:00`);
        return !isNaN(dateSeance.getTime()) && dateSeance >= maintenant;
      })
      .sort((a, b) => {
        const dateA = new Date(`${a.date}T${a.heureDebut || '00:00'}:00`).getTime();
        const dateB = new Date(`${b.date}T${b.heureDebut || '00:00'}:00`).getTime();
        return dateA - dateB;
      })
      .slice(0, 5);
  }

  get prochaineSeance(): Seance | null {
    return this.prochainesSeances.length > 0
      ? this.prochainesSeances[0]
      : null;
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

      this.nomProfesseur =
        utilisateur.nom && utilisateur.prenom
          ? `${utilisateur.prenom} ${utilisateur.nom}`
          : utilisateur.nom || utilisateur.prenom || 'Professeur';

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
          this.seances = Array.isArray(data) ? data : [];
          this.chargement = false;
        },
        error: (error: any) => {
          console.error('Erreur tableau de bord :', error);
          this.messageErreur = 'Impossible de charger vos informations.';
          this.chargement = false;
        }
      });

    } catch (error) {
      console.error(error);
      this.messageErreur = 'Informations utilisateur invalides.';
    }
  }
}
