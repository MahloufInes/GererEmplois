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

const routes: Routes = [  
  {path:'',component:LoginComponent},
  { path: 'crudEtud', component: CrudEtudiantComponent },
  { path: 'crud_Adm', component: CrudSousAdminComponent },
  { path: 'crudGroupes', component: CrudGroupesComponent },
  { path: 'crudFilieres', component: CrudFilieresComponent },
  { path: 'crudDepartements', component: CrudDepartementsComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'enseignants', component: EnseignantsComponent },
  {path: 'salles', component: SallesComponent},
  {path: 'creneaux-horaires', component: CreneauxHorairesComponent},
  {path: 'emploi', component: GenererEmploiComponent}
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
