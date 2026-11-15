
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

        // Save JWT token
        localStorage.setItem('token', response.token);

        // Save user information
        localStorage.setItem('user', JSON.stringify(response));

        console.log('JWT token saved successfully.');

        // Redirect to dashboard
        this.router.navigate(['/dashboard']);

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
