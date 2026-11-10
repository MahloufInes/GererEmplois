
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

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

    this.erreur = '';
    this.showForm = true;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.getRawValue();

    const request = {
      nom: v.nom!,
      prenom: v.prenom!,
      email: v.email!,
      matricule: v.matricule!,
      motDePasse: v.motDePasse!,
      groupeId: v.groupeId!
    };

    this.http.post<Etudiant>(this.apiEtudiants, request).subscribe({
      next: () => {
        this.chargerEtudiants();
        this.close();
      },
      error: (err) => {
        console.error('Erreur ajout étudiant :', err);
        this.erreur =
          err.error?.message ||
          err.error ||
          'Erreur lors de l’ajout de l’étudiant.';
      }
    });
  }

  close(): void {
    this.showForm = false;
  }
}
