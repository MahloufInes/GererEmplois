
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { CrudEtudiantComponent } from './administrateur/crud-etudiant/crud-etudiant.component';
import { CrudprofesseurComponent } from './administrateur/crudprofesseur/crudprofesseur.component';
import { CrudSousAdminComponent } from './administrateur/crud-sous-admin/crud-sous-admin.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MenuComponent } from './administrateur/menu/menu.component';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { CrudGroupesComponent } from './administrateur/crud-groupes/crud-groupes.component';
import { CrudFilieresComponent } from './administrateur/crud-filieres/crud-filieres.component';
import { CrudDepartementsComponent } from './administrateur/crud-departements/crud-departements.component';
import { DashboardComponent } from './administrateur/dashboard/dashboard.component';
import { EnseignantsComponent } from './administrateur/enseignants/enseignants.component';
import { SallesComponent } from './administrateur/salles/salles.component';
import { CreneauxHorairesComponent } from './administrateur/creneaux-horaires/creneaux-horaires.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    CrudEtudiantComponent,
    CrudprofesseurComponent,
    CrudSousAdminComponent,
    MenuComponent,
    CrudGroupesComponent,
    CrudFilieresComponent,
    CrudDepartementsComponent,
    DashboardComponent,
    EnseignantsComponent,
    SallesComponent,
    CreneauxHorairesComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
