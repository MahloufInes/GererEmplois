
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Filiere {
  id: number;
  nom: string;
}

interface Departement {
  id: number;
  nom: string;
}

@Component({
  selector: 'app-crud-filieres',
  templateUrl: './crud-filieres.component.html',
  styleUrls: ['./crud-filieres.component.css']
})
export class CrudFilieresComponent implements OnInit {

  private apiFilieres = 'http://localhost:8081/api/filieres';
  private apiDepartements = 'http://localhost:8081/api/departements';

  filieres: Filiere[] = [];
  departements: Departement[] = [];

  showForm = false;
  editingId: number | null = null;
  search = '';
  erreur = '';
  chargement = false;

  form = {
    nom: '',
    departementId: 0
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerFilieres();
    this.chargerDepartements();
  }

  chargerFilieres(): void {
    this.chargement = true;

    this.http.get<Filiere[]>(this.apiFilieres).subscribe({
      next: data => {
        this.filieres = data;
        this.chargement = false;
      },
      error: err => {
        console.error(err);
        this.erreur = 'Impossible de charger les filières.';
        this.chargement = false;
      }
    });
  }

  chargerDepartements(): void {
    this.http.get<Departement[]>(this.apiDepartements).subscribe({
      next: data => this.departements = data,
      error: err => {
        console.error(err);
        this.erreur = 'Impossible de charger les départements.';
      }
    });
  }

  get filtered(): Filiere[] {
    const q = this.search.toLowerCase().trim();

    return this.filieres.filter(f =>
      f.nom.toLowerCase().includes(q)
    );
  }

  openAdd(): void {
    this.editingId = null;
    this.form = {
      nom: '',
      departementId: this.departements.length
        ? this.departements[0].id : 0
    };
    this.erreur = '';
    this.showForm = true;
  }

  openEdit(filiere: Filiere): void {
    this.editingId = filiere.id;
    this.form = {
      nom: filiere.nom,
      departementId: 0
    };
    this.erreur = '';
    this.showForm = true;
  }

  save(): void {
    if (!this.form.nom.trim() || this.form.departementId <= 0) {
      this.erreur = 'Veuillez remplir tous les champs.';
      return;
    }

    const request = {
      nom: this.form.nom.trim(),
      departementId: this.form.departementId
    };

    if (this.editingId === null) {
      this.http.post(this.apiFilieres, request).subscribe({
        next: () => {
          this.close();
          this.chargerFilieres();
        },
        error: err => {
          console.error(err);
          this.erreur = err.error?.message || 'Erreur lors de l’ajout.';
        }
      });
    } else {
      this.http.put(
        `${this.apiFilieres}/${this.editingId}`,
        request
      ).subscribe({
        next: () => {
          this.close();
          this.chargerFilieres();
        },
        error: err => {
          console.error(err);
          this.erreur = err.error?.message || 'Erreur lors de la modification.';
        }
      });
    }
  }

  supprimer(filiere: Filiere): void {
    if (!confirm(`Voulez-vous supprimer la filière ${filiere.nom} ?`)) {
      return;
    }

    this.http.delete(`${this.apiFilieres}/${filiere.id}`).subscribe({
      next: () => this.chargerFilieres(),
      error: err => {
        console.error(err);
        this.erreur = err.error?.message ||
          'Impossible de supprimer cette filière.';
      }
    });
  }

  close(): void {
    this.showForm = false;
    this.editingId = null;
  }
}
