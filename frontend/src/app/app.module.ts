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

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    CrudEtudiantComponent,
    CrudprofesseurComponent,
    CrudSousAdminComponent,
    MenuComponent
  ],
  imports: [BrowserModule,AppRoutingModule,BrowserModule,FormsModule,ReactiveFormsModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
