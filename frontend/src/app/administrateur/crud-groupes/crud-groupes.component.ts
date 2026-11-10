
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Filiere {
  id: number;
  nom: string;
}

interface Groupe {
  id: number;
  nom: string;
  effectif: number;
  filiereId: number;
  filiere: string;
}

@Component({
  selector: 'app-crud-groupes',
  templateUrl: './crud-groupes.component.html',
  styleUrls: ['./crud-groupes.component.css']
})
export class CrudGroupesComponent implements OnInit {

  private apiGroupes = 'http://localhost:8081/api/groupes';
  private apiFilieres = 'http://localhost:8081/api/filieres';

  groupes: Groupe[] = [];
  filieres: Filiere[] = [];

  showForm = false;
  editingId: number | null = null;
  search = '';
  erreur = '';
  chargement = false;

  form = {
    nom: '',
    effectif: 1,
    filiereId: 0
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerGroupes();
    this.chargerFilieres();
  }

  chargerGroupes(): void {
    this.chargement = true;

    this.http.get<Groupe[]>(this.apiGroupes).subscribe({
      next: data => {
        this.groupes = data;
        this.chargement = false;
      },
      error: err => {
        console.error(err);
        this.erreur = 'Impossible de charger les groupes.';
        this.chargement = false;
      }
    });
  }

  chargerFilieres(): void {
    this.http.get<Filiere[]>(this.apiFilieres).subscribe({
      next: data => this.filieres = data,
      error: err => {
        console.error(err);
        this.erreur = 'Impossible de charger les filières.';
      }
    });
  }

  get filtered(): Groupe[] {
    const q = this.search.toLowerCase().trim();

    return this.groupes.filter(g =>
      g.nom.toLowerCase().includes(q) ||
      g.filiere.toLowerCase().includes(q)
    );
  }

  openAdd(): void {
    this.editingId = null;
    this.form = {
      nom: '',
      effectif: 1,
      filiereId: this.filieres.length ? this.filieres[0].id : 0
    };
    this.erreur = '';
    this.showForm = true;
  }

  openEdit(groupe: Groupe): void {
    this.editingId = groupe.id;

    this.form = {
      nom: groupe.nom,
      effectif: groupe.effectif,
      filiereId: groupe.filiereId
    };

    this.erreur = '';
    this.showForm = true;
  }

  save(): void {
    if (
      !this.form.nom.trim() ||
      this.form.effectif < 1 ||
      this.form.filiereId <= 0
    ) {
      this.erreur = 'Veuillez remplir correctement tous les champs.';
      return;
    }

    this.erreur = '';

    const request = {
      nom: this.form.nom.trim(),
      effectif: this.form.effectif,
      filiereId: this.form.filiereId
    };

    if (this.editingId === null) {
      this.http.post(this.apiGroupes, request).subscribe({
        next: () => {
          this.close();
          this.chargerGroupes();
        },
        error: err => {
          console.error(err);
          this.erreur = err.error?.message || 'Erreur lors de l’ajout.';
        }
      });
    } else {
      this.http.put(
        `${this.apiGroupes}/${this.editingId}`,
        request
      ).subscribe({
        next: () => {
          this.close();
          this.chargerGroupes();
        },
        error: err => {
          console.error(err);
          this.erreur = err.error?.message || 'Erreur lors de la modification.';
        }
      });
    }
  }

  supprimer(groupe: Groupe): void {
    const confirmation = confirm(
      `Voulez-vous supprimer le groupe ${groupe.nom} ?`
    );

    if (!confirmation) return;

    this.http.delete(`${this.apiGroupes}/${groupe.id}`).subscribe({
      next: () => this.chargerGroupes(),
      error: err => {
        console.error(err);
        this.erreur = err.error?.message ||
          'Impossible de supprimer ce groupe.';
      }
    });
  }

  close(): void {
    this.showForm = false;
    this.editingId = null;
  }
}
