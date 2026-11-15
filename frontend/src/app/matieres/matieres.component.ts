import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Matiere, MatiereService } from '../services/matiere.service';

interface Departement {
  id: number;
  nom: string;
}

@Component({
  selector: 'app-matieres',
  templateUrl: './matieres.component.html',
  styleUrls: ['./matieres.component.css']
})
export class MatieresComponent implements OnInit {

  matieres: Matiere[] = [];
  departements: Departement[] = [];

  search = '';
  chargement = false;
  erreur = '';

  showForm = false;
  editingId: number | null = null;
  form: { nom: string; departementId: number } = { nom: '', departementId: 0 };

  constructor(
    private matiereService: MatiereService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.chargerDepartements();
    this.charger();
  }

  chargerDepartements(): void {
    // Adapt the URL if your departments endpoint is different
    this.http.get<Departement[]>('http://localhost:8081/api/departements').subscribe({
      next: d => (this.departements = d),
      error: () => (this.erreur = 'Impossible de charger les départements')
    });
  }

  charger(): void {
    this.chargement = true;
    this.matiereService.getAll().subscribe({
      next: m => {
        this.matieres = m;
        this.chargement = false;
      },
      error: err => {
        this.erreur = err.error?.message || 'Erreur de chargement des matières';
        this.chargement = false;
      }
    });
  }

  get filtered(): Matiere[] {
    const q = this.search.trim().toLowerCase();
    return this.matieres.filter(m => !q || m.nom.toLowerCase().includes(q));
  }

  openAdd(): void {
    this.editingId = null;
    this.form = { nom: '', departementId: 0 };
    this.erreur = '';
    this.showForm = true;
  }

  openEdit(m: Matiere): void {
    this.editingId = m.id;
    this.form = { nom: m.nom, departementId: m.departementId ?? 0 };
    this.erreur = '';
    this.showForm = true;
  }

  close(): void {
    this.showForm = false;
    this.erreur = '';
  }

  save(): void {
    if (!this.form.nom.trim()) {
      this.erreur = 'Le nom de la matière est obligatoire';
      return;
    }
    if (!this.form.departementId) {
      this.erreur = 'Veuillez choisir un département';
      return;
    }

    const body = { nom: this.form.nom.trim(), departementId: this.form.departementId };

    const requete = this.editingId === null
      ? this.matiereService.ajouter(body)
      : this.matiereService.modifier(this.editingId, body);

    requete.subscribe({
      next: () => {
        this.close();
        this.charger();
      },
      error: err => {
        this.erreur = err.error?.message || 'Erreur lors de l’enregistrement';
      }
    });
  }

  supprimer(m: Matiere): void {
    if (!confirm(`Supprimer la matière « ${m.nom} » ?`)) return;

    this.matiereService.supprimer(m.id).subscribe({
      next: () => this.charger(),
      error: err => {
        this.erreur = err.error?.message || 'Impossible de supprimer cette matière';
      }
    });
  }
}