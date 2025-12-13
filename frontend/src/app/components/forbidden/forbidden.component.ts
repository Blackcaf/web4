import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

/**
 * Компонент страницы 403 Forbidden.
 * Отображается при попытке доступа к защищенным маршрутам без аутентификации.
 */
@Component({
    selector: 'app-forbidden',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './forbidden.component.html',
    styleUrls: ['./forbidden.component.css']
})
export class ForbiddenComponent {
    constructor(private router: Router) {}

    goHome() {
        this.router.navigate(['/login']);
    }
}