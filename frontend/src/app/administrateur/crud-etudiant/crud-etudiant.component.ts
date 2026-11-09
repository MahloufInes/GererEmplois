import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

interface Etudiant {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  filiere: string;
  niveau: string;
}

@Component({
  selector: 'app-crud-etudiant',
  templateUrl: './crud-etudiant.component.html',
  styleUrls: ['./crud-etudiant.component.css'],
})
export class CrudEtudiantComponent {
  etudiants: Etudiant[] = [
    { id: 1, nom: 'Ben Ali', prenom: 'Sara', email: 'sara.benali@universite.fr', filiere: 'Informatique', niveau: 'L3' },
    { id: 2, nom: 'Trabelsi', prenom: 'Omar', email: 'omar.trabelsi@universite.fr', filiere: 'Gestion', niveau: 'M1' },
    { id: 3, nom: 'Haddad', prenom: 'Yasmine', email: 'yasmine.haddad@universite.fr', filiere: 'Droit', niveau: 'L2' },
  ];
  nextId = 4;

  search = '';
  filtreNiveau = '';
  showForm = false;
  editingId: number | null = null;

  niveaux = ['L1', 'L2', 'L3', 'M1', 'M2'];
  private couleurs = ['#6352c9', '#e8a97e', '#7fa36b', '#d9738f', '#5b8fd9', '#b58ae0'];

  form = this.fb.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    filiere: ['', Validators.required],
    niveau: ['', Validators.required],
  });

  constructor(private fb: FormBuilder) {}

  // Liste filtrée par recherche + niveau
  get filtered(): Etudiant[] {
    const q = this.search.trim().toLowerCase();
    return this.etudiants.filter(e =>
      (!this.filtreNiveau || e.niveau === this.filtreNiveau) &&
      (!q || `${e.nom} ${e.prenom} ${e.email} ${e.filiere}`.toLowerCase().includes(q)));
  }

  // Statistiques
  get nbFilieres(): number {
    return new Set(this.etudiants.map(e => e.filiere.trim().toLowerCase())).size;
  }

  get nbMasters(): number {
    return this.etudiants.filter(e => e.niveau.startsWith('M')).length;
  }

  // Affichage
  initiales(e: Etudiant): string {
    return (e.prenom.charAt(0) + e.nom.charAt(0)).toUpperCase();
  }

  couleur(e: Etudiant): string {
    return this.couleurs[e.id % this.couleurs.length];
  }

  setNiveau(n: string): void {
    this.filtreNiveau = this.filtreNiveau === n ? '' : n;
  }

  // CRUD
  openAdd(): void {
    this.editingId = null;
    this.form.reset({ nom: '', prenom: '', email: '', filiere: '', niveau: '' });
    this.showForm = true;
  }

  openEdit(e: Etudiant): void {
    this.editingId = e.id;
    this.form.setValue({
      nom: e.nom, prenom: e.prenom, email: e.email, filiere: e.filiere, niveau: e.niveau,
    });
    this.showForm = true;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue() as Omit<Etudiant, 'id'>;
    if (this.editingId === null) {
      this.etudiants = [...this.etudiants, { id: this.nextId++, ...v }];
    } else {
      this.etudiants = this.etudiants.map(x =>
        x.id === this.editingId ? { id: x.id, ...v } : x);
    }
    this.close();
  }

  remove(e: Etudiant): void {
    if (confirm(`Supprimer ${e.prenom} ${e.nom} ?`)) {
      this.etudiants = this.etudiants.filter(x => x.id !== e.id);
    }
  }

  close(): void {
    this.showForm = false;
  }
}