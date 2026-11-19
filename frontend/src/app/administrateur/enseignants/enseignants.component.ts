import { Component, OnInit } from '@angular/core';
import {
  Professeur,
  ProfesseurRequest,
  Departement,
  ProfesseurService
} from '../../services/professeur.service';

@Component({
  selector: 'app-enseignants',
  templateUrl: './enseignants.component.html',
  styleUrls: ['./enseignants.component.css']
})
export class EnseignantsComponent implements OnInit {

  professeurs: Professeur[] = [];
  departements: Departement[] = [];
  chargement = false;
  messageErreur = '';   // erreur de chargement de la liste
  erreurAction = '';    // erreur de suppression

  // Formulaire ajout / modification
  showForm = false;
  editingId: number | null = null;
  erreurForm = '';
  form: ProfesseurRequest = this.formVide();

  constructor(private professeurService: ProfesseurService) {}

  ngOnInit(): void {
    this.chargerProfesseurs();
    this.chargerDepartements();
  }

  chargerProfesseurs(): void {
    this.chargement = true;
    this.messageErreur = '';

    this.professeurService.afficherTous().subscribe({
      next: (data) => {
        this.professeurs = data;
        this.chargement = false;
      },
      error: (error) => {
        console.error('Erreur de chargement', error);
        this.messageErreur = 'Impossible de charger les professeurs.';
        this.chargement = false;
      }
    });
  }

  chargerDepartements(): void {
    this.professeurService.afficherDepartements().subscribe({
      next: (data) => this.departements = data,
      error: (error) => console.error('Erreur départements', error)
    });
  }

  private formVide(): ProfesseurRequest {
    return {
      nom: '',
      prenom: '',
      email: '',
      motDePasse: '',
      specialite: '',
      departementId: 0
    };
  }

  // ---------- Ajout ----------
  openAdd(): void {
    this.editingId = null;
    this.form = this.formVide();
    this.form.departementId = this.departements.length ? this.departements[0].id : 0;
    this.erreurForm = '';
    this.erreurAction = '';
    this.showForm = true;
  }

  // ---------- Modification ----------
  openEdit(p: Professeur): void {
    this.editingId = p.id;
    this.form = {
      nom: p.nom,
      prenom: p.prenom,
      email: p.email,
      motDePasse: '',               // jamais renvoyé par le serveur
      specialite: p.specialite,
      departementId: p.departementId
    };
    this.erreurForm = '';
    this.erreurAction = '';
    this.showForm = true;
  }

  close(): void {
    this.showForm = false;
    this.editingId = null;
  }

  save(): void {
    const f = this.form;
    const modeAjout = this.editingId === null;

    // Le mot de passe n'est obligatoire qu'à l'ajout
    if (!f.nom.trim() || !f.prenom.trim() || !f.email.trim()
        || !f.specialite.trim() || f.departementId <= 0
        || (modeAjout && !f.motDePasse.trim())) {
      this.erreurForm = 'Veuillez remplir tous les champs.';
      return;
    }

    this.erreurForm = '';

    const request: ProfesseurRequest = {
      nom: f.nom.trim(),
      prenom: f.prenom.trim(),
      email: f.email.trim(),
      motDePasse: f.motDePasse,
      specialite: f.specialite.trim(),
      departementId: f.departementId
    };

    const appel = modeAjout
      ? this.professeurService.ajouter(request)
      : this.professeurService.modifier(this.editingId as number, request);

    appel.subscribe({
      next: () => {
        this.close();
        this.chargerProfesseurs();
      },
      error: (error) => {
        console.error(error);
        this.erreurForm = error.error?.message ||
          (modeAjout ? 'Erreur lors de l’ajout.' : 'Erreur lors de la modification.');
      }
    });
  }

  // ---------- Suppression ----------
  supprimer(p: Professeur): void {
    const confirmation = confirm(
      `Voulez-vous supprimer l'enseignant ${p.prenom} ${p.nom} ?`
    );
    if (!confirmation) return;

    this.erreurAction = '';

    this.professeurService.supprimer(p.id).subscribe({
      next: () => this.chargerProfesseurs(),
      error: (error) => {
        console.error(error);
        this.erreurAction = error.error?.message ||
          'Impossible de supprimer cet enseignant (il est peut-être lié à des séances).';
      }
    });
  }
}