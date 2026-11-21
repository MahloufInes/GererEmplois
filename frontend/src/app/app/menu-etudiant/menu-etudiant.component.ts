
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-etudiant',
  templateUrl: './menu-etudiant.component.html',
  styleUrls: ['./menu-etudiant.component.css']
})
export class MenuEtudiantComponent {

  constructor(private router: Router) {}

  deconnecter(): void {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigateByUrl('/', { replaceUrl: true });
  }
}
