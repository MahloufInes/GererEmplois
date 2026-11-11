
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Departement {
  id: number;
  code: string;
  nom: string;
}

@Component({
  selector: 'app-crud-departements',
  templateUrl: './crud-departements.component.html',
  styleUrls: ['./crud-departements.component.css']
})
export class CrudDepartementsComponent implements OnInit {

  private api = 'http://localhost:8081/api/departements';

  departements: Departement[] = [];

  showForm = false;
  editingId: number | null = null;

  search = '';
  erreur = '';
  chargement = false;

  form = {
    code: '',
    nom: ''
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.chargerDepartements();
  }

  chargerDepartements(): void {
    this.chargement = true;

    this.http.get<Departement[]>(this.api).subscribe({
      next: (data) => {
        this.departements = data;
        this.chargement = false;
      },
      error: (err) => {
        console.error(err);
        this.erreur = 'Erreur lors du chargement des départements.';
        this.chargement = false;
      }
    });
  }

  get filtered(): Departement[] {
    return this.departements.filter(d =>
      d.code.toLowerCase().includes(this.search.toLowerCase()) ||
      d.nom.toLowerCase().includes(this.search.toLowerCase())
    );
  }

  openAdd(): void {
    this.editingId = null;
    this.form = { code: '', nom: '' };
    this.erreur = '';
    this.showForm = true;
  }

  openEdit(d: Departement): void {
    this.editingId = d.id;
    this.form = {
      code: d.code,
      nom: d.nom
    };
    this.erreur = '';
    this.showForm = true;
  }

  save(): void {
    if (!this.form.code.trim() || !this.form.nom.trim()) {
      this.erreur = 'Veuillez remplir tous les champs.';
      return;
    }

    const request = {
      code: this.form.code.trim(),
      nom: this.form.nom.trim()
    };

    if (this.editingId === null) {
      this.http.post<Departement>(this.api, request).subscribe({
        next: () => {
          this.close();
          this.chargerDepartements();
        },
        error: (err) => {
          console.error(err);
          this.erreur = err.error?.message || 'Erreur lors de l’ajout.';
        }
      });
    } else {
      this.http.put<Departement>(
        `${this.api}/${this.editingId}`, request
      ).subscribe({
        next: () => {
          this.close();
          this.chargerDepartements();
        },
        error: (err) => {
          console.error(err);
          this.erreur = err.error?.message || 'Erreur lors de la modification.';
        }
      });
    }
  }

  supprimer(d: Departement): void {
    if (!confirm(`Voulez-vous supprimer le département ${d.nom} ?`)) {
      return;
    }

    this.http.delete(`${this.api}/${d.id}`).subscribe({
      next: () => this.chargerDepartements(),
      error: (err) => {
        console.error(err);
        alert(err.error?.message || 'Impossible de supprimer ce département.');
      }
    });
  }

  close(): void {
    this.showForm = false;
    this.editingId = null;
    this.erreur = '';
  }
}
