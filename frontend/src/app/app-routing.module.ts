
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './login/login.component';

import { CrudEtudiantComponent } from './administrateur/crud-etudiant/crud-etudiant.component';
import { CrudSousAdminComponent } from './administrateur/crud-sous-admin/crud-sous-admin.component';
import { CrudGroupesComponent } from './administrateur/crud-groupes/crud-groupes.component';
import { CrudFilieresComponent } from './administrateur/crud-filieres/crud-filieres.component';
import { CrudDepartementsComponent } from './administrateur/crud-departements/crud-departements.component';
import { DashboardComponent } from './administrateur/dashboard/dashboard.component';
import { EnseignantsComponent } from './administrateur/enseignants/enseignants.component';
import { SallesComponent } from './administrateur/salles/salles.component';
import { CreneauxHorairesComponent } from './administrateur/creneaux-horaires/creneaux-horaires.component';
import { GenererEmploiComponent } from './administrateur/generer-emploi/generer-emploi.component';
import { MatieresComponent } from './matieres/matieres.component';
import { EmploiEtudiantComponent } from './etudiant/emploi-etudiant/emploi-etudiant.component';
import { DashboardProfesseurComponent } from './professeur/dashboard-professeur/dashboard-professeur.component';
import { ProfilEtudiantComponent } from './etudiant/profil-etudiant/profil-etudiant.component';
import { AuthGuard } from './auth.guard';
import { DashboardEtudiantComponent } from './etudiant/dashboard-etudiant/dashboard-etudiant.component';
import { EmploiProfesseurComponent } from './professeur/emploi-professeur/emploi-professeur.component';
import { ProfilProfesseurComponent } from './professeur/profil-professeur/profil-professeur.component';
import { MesDisponibilitesComponent } from './professeur/mes-disponibilites/mes-disponibilites.component';
const routes: Routes = [

  { path: '', component: LoginComponent },

  // ESPACE ADMINISTRATEUR
  {
  path: 'profil-etudiant',
  component: ProfilEtudiantComponent,
  canActivate: [AuthGuard],
  data: { roles: ['ETUDIANT'] }
},
{
  path: 'emploi-etudiant',
  component: EmploiEtudiantComponent,
  canActivate: [AuthGuard],
  data: { roles: ['ETUDIANT'] }
},
  {
  path: 'mes-disponibilites',
  component: MesDisponibilitesComponent,
  canActivate: [AuthGuard],
  data: { roles: ['PROFESSEUR'] }
},
  {
  path: 'profil-professeur',
  component: ProfilProfesseurComponent,
  canActivate: [AuthGuard],
  data: { roles: ['PROFESSEUR'] }
},
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
    path: 'crudEtud',
    component: CrudEtudiantComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
    path: 'crud_Adm',
    component: CrudSousAdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR'] }
  },
  {
    path: 'crudGroupes',
    component: CrudGroupesComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
    path: 'crudFilieres',
    component: CrudFilieresComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
    path: 'crudMatiers',
    component: MatieresComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
  path: 'emploi-professeur',
  component: EmploiProfesseurComponent,
  canActivate: [AuthGuard],
  data: { roles: ['PROFESSEUR'] }
},
  {
    path: 'crudDepartements',
    component: CrudDepartementsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR'] }
  },
  {
    path: 'enseignants',
    component: EnseignantsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
    path: 'salles',
    component: SallesComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
    path: 'creneaux-horaires',
    component: CreneauxHorairesComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },
  {
    path: 'emploi',
    component: GenererEmploiComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMINISTRATEUR', 'SOUS_ADMINISTRATEUR'] }
  },

  // ESPACE PROFESSEUR
  {
    path: 'dashboard-professeur',
    component: DashboardProfesseurComponent,
    canActivate: [AuthGuard],
    data: { roles: ['PROFESSEUR'] }
  },
  {
  path: 'dashboard-etudiant',
  component: DashboardEtudiantComponent
},
  { path: '**', redirectTo: '' }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
