import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

/**
 * Точка входа Angular приложения.
 * Запускает корневой компонент AppComponent с конфигурацией appConfig.
 * Использует standalone API (Angular 17+) вместо NgModule.
 */
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));

