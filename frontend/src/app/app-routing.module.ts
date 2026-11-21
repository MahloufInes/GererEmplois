
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

import { DashboardProfesseurComponent } from './professeur/dashboard-professeur/dashboard-professeur.component';

import { AuthGuard } from './auth.guard';
import { DashboardEtudiantComponent } from './etudiant/dashboard-etudiant/dashboard-etudiant.component';

const routes: Routes = [

  { path: '', component: LoginComponent },

  // ESPACE ADMINISTRATEUR
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
