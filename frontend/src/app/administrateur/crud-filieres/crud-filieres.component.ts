
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';
interface Filiere {
  id: number;
  nom: string;
  departementId: number;
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
  console.log('Filière sélectionnée :', filiere);

  this.editingId = filiere.id;

  this.form = {
    nom: filiere.nom,
    departementId: filiere.departementId
  };

  console.log('Formulaire :', this.form);

  this.erreur = '';
  this.showForm = true;
}

  save(): void {
  if (!this.form.nom.trim() || this.form.departementId <= 0) {
    Swal.fire({
      icon: 'warning',
      title: 'Champs obligatoires',
      text: 'Veuillez remplir tous les champs.',
      confirmButtonColor: '#5142a5'
    });
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

        Swal.fire({
          icon: 'success',
          title: 'Filière ajoutée !',
          text: 'La filière a été ajoutée avec succès.',
          confirmButtonColor: '#5142a5'
        });
      },
      error: err => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: err.error?.message || 'Erreur lors de l’ajout.',
          confirmButtonColor: '#5142a5'
        });
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

        Swal.fire({
          icon: 'success',
          title: 'Modification réussie !',
          text: 'La filière a été modifiée avec succès.',
          confirmButtonColor: '#5142a5'
        });
      },
      error: err => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: err.error?.message || 'Erreur lors de la modification.',
          confirmButtonColor: '#5142a5'
        });
      }
    });
  }
}

  supprimer(filiere: Filiere): void {
  Swal.fire({
    title: 'Êtes-vous sûre ?',
    text: `Voulez-vous supprimer la filière ${filiere.nom} ?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Oui, supprimer',
    cancelButtonText: 'Annuler'
  }).then(result => {
    if (result.isConfirmed) {
      this.http.delete(`${this.apiFilieres}/${filiere.id}`).subscribe({
        next: () => {
          this.chargerFilieres();

          Swal.fire({
            icon: 'success',
            title: 'Supprimée !',
            text: 'La filière a été supprimée.',
            confirmButtonColor: '#5142a5'
          });
        },
        error: err => {
          Swal.fire({
            icon: 'error',
            title: 'Impossible de supprimer',
            text: err.error?.message ||
              'Cette filière ne peut pas être supprimée.',
            confirmButtonColor: '#5142a5'
          });
        }
      });
    }
  });
}

  close(): void {
    this.showForm = false;
    this.editingId = null;
  }
}
