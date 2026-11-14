
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';
interface Groupe {
  id: number;
  nom: string;
  effectif: number;
  filiereId: number;
  filiere: string;
}

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
  selector: 'app-crud-etudiant',
  templateUrl: './crud-etudiant.component.html',
  styleUrls: ['./crud-etudiant.component.css'],
})
export class CrudEtudiantComponent implements OnInit {

  private apiEtudiants = 'http://localhost:8081/api/etudiants';
  private apiGroupes = 'http://localhost:8081/api/groupes';

  etudiants: Etudiant[] = [];
  groupes: Groupe[] = [];

  search = '';
  showForm = false;
  editingId: number | null = null;

  loading = false;
  erreur = '';

  private couleurs = [
    '#6352c9', '#e8a97e', '#7fa36b',
    '#d9738f', '#5b8fd9', '#b58ae0'
  ];

  form = this.fb.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    matricule: ['', Validators.required],
    motDePasse: ['', Validators.required],
    groupeId: [null as number | null, Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.chargerGroupes();
    this.chargerEtudiants();
  }

  chargerGroupes(): void {
    this.http.get<Groupe[]>(this.apiGroupes).subscribe({
      next: (data) => {
        this.groupes = data;
      },
      error: (err) => {
        console.error('Erreur chargement groupes :', err);
        this.erreur = 'Impossible de charger les groupes.';
      }
    });
  }

  chargerEtudiants(): void {
    this.loading = true;

    this.http.get<Etudiant[]>(this.apiEtudiants).subscribe({
      next: (data) => {
        this.etudiants = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement étudiants :', err);
        this.erreur = 'Impossible de charger les étudiants.';
        this.loading = false;
      }
    });
  }

  get filtered(): Etudiant[] {
    const q = this.search.trim().toLowerCase();

    return this.etudiants.filter(e =>
      `${e.nom} ${e.prenom} ${e.email} ${e.filiere} ${e.groupe} ${e.matricule}`
        .toLowerCase()
        .includes(q)
    );
  }

  get nbFilieres(): number {
    return new Set(
      this.etudiants.map(e => e.filiere.trim().toLowerCase())
    ).size;
  }

  get nbGroupes(): number {
    return new Set(this.etudiants.map(e => e.groupeId)).size;
  }

  initiales(e: Etudiant): string {
    return (e.prenom.charAt(0) + e.nom.charAt(0)).toUpperCase();
  }

  couleur(e: Etudiant): string {
    return this.couleurs[e.id % this.couleurs.length];
  }

 openAdd(): void {
  this.editingId = null;

  this.form.reset({
    nom: '',
    prenom: '',
    email: '',
    matricule: '',
    motDePasse: '',
    groupeId: null
  });

  // Le mot de passe est obligatoire lors de l'ajout
  this.form.get('motDePasse')?.setValidators(Validators.required);
  this.form.get('motDePasse')?.updateValueAndValidity();

  this.erreur = '';
  this.showForm = true;
}

save(): void {
  if (this.form.invalid) {
    this.form.markAllAsTouched();

    Swal.fire({
      icon: 'warning',
      title: 'Champs obligatoires',
      text: 'Veuillez remplir tous les champs obligatoires.',
      confirmButtonColor: '#5142a5'
    });

    return;
  }

  const v = this.form.getRawValue();

  const request: any = {
    nom: v.nom!,
    prenom: v.prenom!,
    email: v.email!,
    matricule: v.matricule!,
    groupeId: v.groupeId!
  };

  // Ajouter le mot de passe seulement s'il est renseigné
  if (v.motDePasse?.trim()) {
    request.motDePasse = v.motDePasse;
  }

  if (this.editingId === null) {

    this.http.post(this.apiEtudiants, {
      ...request,
      motDePasse: v.motDePasse
    }).subscribe({
      next: () => {
        this.close();
        this.chargerEtudiants();

        Swal.fire({
          icon: 'success',
          title: 'Étudiant ajouté !',
          text: 'L’étudiant a été ajouté avec succès.',
          confirmButtonColor: '#5142a5'
        });
      },
      error: err => {
        console.error(err);

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
      `${this.apiEtudiants}/${this.editingId}`,
      request
    ).subscribe({
      next: () => {
        this.close();
        this.chargerEtudiants();

        Swal.fire({
          icon: 'success',
          title: 'Modification réussie !',
          text: 'Les informations ont été modifiées.',
          confirmButtonColor: '#5142a5'
        });
      },
      error: err => {
        console.error(err);

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
 openEdit(e: Etudiant): void {
  this.editingId = e.id;

  this.form.reset({
    nom: e.nom,
    prenom: e.prenom,
    email: e.email,
    matricule: e.matricule,
    motDePasse: '',
    groupeId: e.groupeId
  });

  this.form.get('motDePasse')?.clearValidators();
  this.form.get('motDePasse')?.updateValueAndValidity();

  this.erreur = '';
  this.showForm = true;
}
supprimer(e: Etudiant): void {

  Swal.fire({
    title: 'Êtes-vous sûr ?',
    text: `Voulez-vous supprimer ${e.prenom} ${e.nom} ?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Oui, supprimer',
    cancelButtonText: 'Annuler'
  }).then(result => {

    if (result.isConfirmed) {

      this.http.delete(`${this.apiEtudiants}/${e.id}`).subscribe({
        next: () => {
          this.chargerEtudiants();

          Swal.fire({
            icon: 'success',
            title: 'Supprimé !',
            text: 'L’étudiant a été supprimé.',
            confirmButtonColor: '#5142a5'
          });
        },
        error: err => {
          console.error(err);

          Swal.fire({
            icon: 'error',
            title: 'Impossible de supprimer',
            text: err.error?.message || 'Erreur lors de la suppression.',
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

  // Réactiver le mot de passe obligatoire pour l'ajout
  this.form.get('motDePasse')?.setValidators(Validators.required);
  this.form.get('motDePasse')?.updateValueAndValidity();
}
}
