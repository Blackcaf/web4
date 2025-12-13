import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/**
 * Корневой компонент приложения.
 * Содержит только router-outlet для отображения компонентов по маршрутам.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet></router-outlet>'
})
export class AppComponent {}

