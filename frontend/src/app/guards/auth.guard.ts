import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private authService: AuthService,
        private router: Router
    ) {}

    /**
     * Проверяет, может ли пользователь перейти на main.
     *
     * @returns true если пользователь аутентифицирован, false и редирект на /403 в противном случае
     */
    canActivate(): boolean {
        if (this.authService.isAuthenticated()) {
            return true;
        }

        this.router.navigate(['/403']);
        return false;
    }
}