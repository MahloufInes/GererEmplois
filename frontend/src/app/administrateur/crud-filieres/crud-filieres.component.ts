
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';

interface FiliereMatiere {
  id?: number;
  filiereId?: number;
  matiereId: number;
  matiereNom?: string;
  semestre: string;
  heuresCours: number;
  heuresTP: number;
}

interface Filiere {
  id: number;
  nom: string;
  departementId: number;
  matieres?: FiliereMatiere[];
}

interface Departement {
  id: number;
  nom: string;
}

interface Matiere {
  id: number;
  nom: string;
  departementId: number;
}

@Component({
  selector: 'app-crud-filieres',
  templateUrl: './crud-filieres.component.html',
  styleUrls: ['./crud-filieres.component.css']
})
export class CrudFilieresComponent implements OnInit {

  private apiFilieres = 'http://localhost:8081/api/filieres';
  private apiDepartements = 'http://localhost:8081/api/departements';
  private apiMatieres = 'http://localhost:8081/api/matieres';

  filieres: Filiere[] = [];
  departements: Departement[] = [];
  matieresDisponibles: Matiere[] = [];

  matieresSelectionnees: FiliereMatiere[] = [];

  semestresDisponibles: string[] = [
    'S1', 'S2', 'S3', 'S4', 'S5', 'S6'
  ];

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
    this.chargerMatieres();
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

  chargerMatieres(): void {
    this.http.get<Matiere[]>(this.apiMatieres).subscribe({
      next: data => {
        this.matieresDisponibles = data;
      },
      error: err => {
        console.error(err);
        this.erreur = 'Impossible de charger les matières.';
      }
    });
  }

  get filtered(): Filiere[] {
    const q = this.search.toLowerCase().trim();

    return this.filieres.filter(f =>
      f.nom.toLowerCase().includes(q)
    );
  }

  // Check whether a subject is already associated
  isMatiereSelected(matiereId: number): boolean {
    return this.matieresSelectionnees.some(
      m => m.matiereId === matiereId
    );
  }

  // Get all semester associations for a subject
  getAssociations(matiereId: number): FiliereMatiere[] {
    return this.matieresSelectionnees.filter(
      m => m.matiereId === matiereId
    );
  }

  // Add a subject association for a semester
  ajouterAssociation(matiereId: number): void {
    this.matieresSelectionnees.push({
      matiereId,
      semestre: 'S3',
      heuresCours: 0,
      heuresTP: 0
    });
  }

  // Remove one subject-semester association
  supprimerAssociation(association: FiliereMatiere): void {
    this.matieresSelectionnees =
      this.matieresSelectionnees.filter(
        m => m !== association
      );
  }

  // Update semester
  modifierSemestre(
    association: FiliereMatiere,
    semestre: string
  ): void {
    association.semestre = semestre;
  }

  // Update weekly hours
  modifierHeures(
    association: FiliereMatiere,
    type: 'heuresCours' | 'heuresTP',
    valeur: number
  ): void {
    association[type] = Number(valeur);
  }

  // Open add form
  openAdd(): void {
    this.editingId = null;

    this.form = {
      nom: '',
      departementId: this.departements.length
        ? this.departements[0].id
        : 0
    };

    this.matieresSelectionnees = [];
    this.erreur = '';
    this.showForm = true;
  }

  // Open edit form
  openEdit(filiere: Filiere): void {
    this.editingId = filiere.id;

    this.form = {
      nom: filiere.nom,
      departementId: filiere.departementId
    };

    this.matieresSelectionnees =
      (filiere.matieres || []).map(m => ({
        matiereId: m.matiereId,
        matiereNom: m.matiereNom,
        semestre: m.semestre,
        heuresCours: m.heuresCours,
        heuresTP: m.heuresTP
      }));

    this.erreur = '';
    this.showForm = true;
  }

  // Save filière
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

    if (this.matieresSelectionnees.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Matières obligatoires',
        text: 'Veuillez ajouter au moins une matière.',
        confirmButtonColor: '#5142a5'
      });
      return;
    }

    const associationInvalide = this.matieresSelectionnees.some(m =>
      !m.semestre ||
      !this.semestresDisponibles.includes(m.semestre) ||
      m.heuresCours < 0 ||
      m.heuresTP < 0 ||
      !Number.isFinite(m.heuresCours) ||
      !Number.isFinite(m.heuresTP)
    );

    if (associationInvalide) {
      Swal.fire({
        icon: 'warning',
        title: 'Données invalides',
        text: 'Vérifiez les semestres et les heures des matières.',
        confirmButtonColor: '#5142a5'
      });
      return;
    }

    // Prevent duplicate subject + semester associations
    const associations = this.matieresSelectionnees.map(
      m => `${m.matiereId}-${m.semestre}`
    );

    if (new Set(associations).size !== associations.length) {
      Swal.fire({
        icon: 'warning',
        title: 'Association en double',
        text: 'Une matière ne peut pas être associée deux fois au même semestre.',
        confirmButtonColor: '#5142a5'
      });
      return;
    }

    const request = {
      nom: this.form.nom.trim(),
      departementId: this.form.departementId,
      matieres: this.matieresSelectionnees.map(m => ({
        matiereId: m.matiereId,
        semestre: m.semestre,
        heuresCours: m.heuresCours,
        heuresTP: m.heuresTP
      }))
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
          console.error(err);

          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: err.error?.message || 'Erreur lors de l’ajout de la filière.',
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

  // Delete filière
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
        this.http.delete(
          `${this.apiFilieres}/${filiere.id}`
        ).subscribe({
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
            console.error(err);

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
    this.matieresSelectionnees = [];
    this.erreur = '';
  }
}
