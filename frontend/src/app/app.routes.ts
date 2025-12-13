import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { MainComponent } from './components/main/main.component';
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import { AuthGuard } from './guards/auth.guard';

/**
 * Конфигурация маршрутов приложения.
 *
 * Маршруты:
 * - / → редирект на /login
 * - /login → страница входа/регистрации (публичная)
 * - /main → основная страница с графиком (защищена AuthGuard)
 * - /403 → страница ошибки доступа (публичная)
 * - /** → редирект на /login для несуществующих путей
 */
export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'main', component: MainComponent, canActivate: [AuthGuard] },
    { path: '403', component: ForbiddenComponent },
    { path: '**', redirectTo: '/login' }
];