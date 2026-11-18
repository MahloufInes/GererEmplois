import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-professeur',
  templateUrl: './menu-professeur.component.html',
  styleUrls: ['./menu-professeur.component.css']
})
export class MenuProfesseurComponent {

  constructor(private router: Router) {}

  deconnecter(): void {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigateByUrl('/', { replaceUrl: true });
  }
}