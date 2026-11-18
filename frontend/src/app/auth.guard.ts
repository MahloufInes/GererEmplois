
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {

    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');

    if (!token || !user) {
      this.router.navigate(['/']);
      return false;
    }

    const utilisateur = JSON.parse(user);
    const role = utilisateur.role;

    const rolesAutorises = route.data['roles'] as string[];

    if (rolesAutorises && !rolesAutorises.includes(role)) {
      this.router.navigate(['/']);
      return false;
    }

    return true;
  }
}
