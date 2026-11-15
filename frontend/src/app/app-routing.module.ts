
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

import { AuthGuard } from './auth.guard';

const routes: Routes = [
  { path: '', component: LoginComponent },

  { path: 'crudEtud', component: CrudEtudiantComponent, canActivate: [AuthGuard] },
  { path: 'crud_Adm', component: CrudSousAdminComponent, canActivate: [AuthGuard] },
  { path: 'crudGroupes', component: CrudGroupesComponent, canActivate: [AuthGuard] },
  { path: 'crudFilieres', component: CrudFilieresComponent, canActivate: [AuthGuard] },
  { path: 'crudMatiers', component: MatieresComponent, canActivate: [AuthGuard] },
  { path: 'crudDepartements', component: CrudDepartementsComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'enseignants', component: EnseignantsComponent, canActivate: [AuthGuard] },
  { path: 'salles', component: SallesComponent, canActivate: [AuthGuard] },
  { path: 'creneaux-horaires', component: CreneauxHorairesComponent, canActivate: [AuthGuard] },
  { path: 'emploi', component: GenererEmploiComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
