import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CookieService } from '../utils/cookie.service';

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    private themeSubject = new BehaviorSubject<'light' | 'dark'>('light');
    public theme$ = this.themeSubject.asObservable();

    constructor() {
        const savedTheme = CookieService.get('theme') as 'light' | 'dark';
        if (savedTheme) {
            this.setTheme(savedTheme);
        } else {
            const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            this.setTheme(prefersDark ? 'dark' : 'light');
        }
    }

    /**
     * Переключает тему между светлой и темной.
     */
    toggleTheme(): void {
        const newTheme = this.themeSubject.value === 'light' ? 'dark' : 'light';
        this.setTheme(newTheme);
    }


    private setTheme(theme: 'light' | 'dark'): void {
        this.themeSubject.next(theme);
        CookieService.set('theme', theme, 365);
        document.documentElement.setAttribute('data-theme', theme);
    }
}