
import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  email: string = '';
  motDePasse: string = '';
  errorMessage: string = '';
  afficherMotDePasse = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {

    this.errorMessage = '';

    this.authService.login(this.email, this.motDePasse).subscribe({

      next: (response) => {

        console.log('Login successful!');
        console.log('User:', response);

        // Sauvegarder les informations de connexion
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response));

        // Redirection selon le rôle
       switch (response.role) {

  case 'ADMINISTRATEUR':
  case 'SOUS_ADMINISTRATEUR':
    this.router.navigate(['/dashboard']);
    break;

  case 'PROFESSEUR':
    this.router.navigate(['/dashboard-professeur']);
    break;

  case 'ETUDIANT':
    this.router.navigate(['/dashboard-etudiant']);
    break;

  default:
    this.errorMessage = 'Rôle utilisateur non reconnu.';
    this.authService.logout();
    break;
}

      },

      error: (error: HttpErrorResponse) => {

        console.error('Login error:', error);

        this.errorMessage =
          typeof error.error === 'string'
            ? error.error
            : 'Email ou mot de passe incorrect.';

      }

    });

  }

}
