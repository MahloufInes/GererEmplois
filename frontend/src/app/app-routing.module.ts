import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { CrudEtudiantComponent } from './administrateur/crud-etudiant/crud-etudiant.component';
import { CrudSousAdminComponent } from './administrateur/crud-sous-admin/crud-sous-admin.component';
import { CrudGroupesComponent } from './administrateur/crud-groupes/crud-groupes.component';
import { CrudFilieresComponent } from './administrateur/crud-filieres/crud-filieres.component';

const routes: Routes = [  
  {path:'',component:LoginComponent},
  { path: 'crudEtud', component: CrudEtudiantComponent },
  { path: 'crud_Adm', component: CrudSousAdminComponent },
  { path: 'crudGroupes', component: CrudGroupesComponent },
  { path: 'crudFilieres', component: CrudFilieresComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
