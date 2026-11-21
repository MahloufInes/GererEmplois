
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { LoginComponent } from './login/login.component';
import { AuthInterceptor } from './auth.interceptor';

import { CrudEtudiantComponent } from './administrateur/crud-etudiant/crud-etudiant.component';
import { CrudprofesseurComponent } from './administrateur/crudprofesseur/crudprofesseur.component';
import { CrudSousAdminComponent } from './administrateur/crud-sous-admin/crud-sous-admin.component';
import { MenuComponent } from './administrateur/menu/menu.component';
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
import { DashboardEtudiantComponent } from './etudiant/dashboard-etudiant/dashboard-etudiant.component';
import { MenuEtudiantComponent } from './app/menu-etudiant/menu-etudiant.component';
import { MenuProfesseurComponent } from './app/menu-professeur/menu-professeur.component';

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
    CreneauxHorairesComponent,
    GenererEmploiComponent,
    MatieresComponent,
    DashboardProfesseurComponent,
    DashboardEtudiantComponent,
    MenuEtudiantComponent,
    MenuProfesseurComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
