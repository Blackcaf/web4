import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ResultService } from '../../services/result.service';
import { ThemeService } from '../../services/theme.service';
import { PointRequest, Result } from '../../models/result.model';

/**
 * Компонент основной страницы приложения.
 *
 * Основные возможности:
 * - Интерактивный Canvas график с тремя геометрическими областями
 * - Форма ввода координат (x, y, r) с валидацией
 * - Клик по графику для быстрой проверки точки
 * - Таблица истории всех проверок пользователя
 * - Очистка истории результатов
 * - Адаптивный дизайн с поддержкой темной/светлой темы
 * - Модальное окно с математическими формулами областей
 */
@Component({
    selector: 'app-main',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit, AfterViewInit {
    @ViewChild('canvas', { static: false }) canvasRef!: ElementRef<HTMLCanvasElement>;

    username: string = '';

    xInput: string = '';
    yInput: string = '';
    rInput: string = '';

    isXInvalid: boolean = false;
    isYInvalid: boolean = false;
    isRInvalid: boolean = false;
    isRNegative: boolean = false;

    errorMessage: string = '';
    showMathModal: boolean = false;
    isDarkTheme: boolean = false;

    results: Result[] = [];
    currentR: number | null = null;

    private readonly canvasSize = 300;
    private readonly scaleFactor = 2;

    private clampedX: number = 0;
    private clampedY: number = 0;
    private isMouseOverCanvas: boolean = false;

    constructor(
        private authService: AuthService,
        private resultService: ResultService,
        private themeService: ThemeService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.username = this.authService.getUsername() || 'Гость';
        this.loadResults();

        this.themeService.theme$.subscribe(theme => {
            this.isDarkTheme = theme === 'dark';
            this.drawCanvas();
        });
    }

    ngAfterViewInit(): void {
        setTimeout(() => this.drawCanvas(), 100);
    }

    scrollTo(elementId: string): void {
        const element = document.getElementById(elementId);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth' });
        }
    }


    private isNumber(val: string): boolean {
        if (val.trim() === '' || val.trim() === '-') return true;
        return !isNaN(Number(val)) && !isNaN(parseFloat(val));
    }

    private updateGlobalError(): void {
        if (!this.isXInvalid && !this.isYInvalid && !this.isRInvalid &&
            this.xInput && this.yInput && this.rInput && !this.isRNegative) {
            this.errorMessage = '';
        }
    }

    onXChange(event: any): void {
        let val = event.target.value.replace(',', '.');

        if (!this.isNumber(val)) {
            this.isXInvalid = true;
        } else {
            this.isXInvalid = false;
            const num = parseFloat(val);
            if (!isNaN(num)) {
                if (num > 3) { this.xInput = '3'; event.target.value = '3'; }
                else if (num < -5) { this.xInput = '-5'; event.target.value = '-5'; }
                else { this.xInput = val; }
            } else {
                this.xInput = val;
            }
        }
        this.updateGlobalError();
    }

    onYChange(event: any): void {
        let val = event.target.value.replace(',', '.');

        if (!this.isNumber(val)) {
            this.isYInvalid = true;
        } else {
            this.isYInvalid = false;
            const num = parseFloat(val);
            if (!isNaN(num)) {
                if (num > 5) { this.yInput = '5'; event.target.value = '5'; }
                else if (num < -3) { this.yInput = '-3'; event.target.value = '-3'; }
                else { this.yInput = val; }
            } else {
                this.yInput = val;
            }
        }
        this.updateGlobalError();
    }

    onRChange(event: any): void {
        let val = event.target.value.replace(',', '.');

        if (!this.isNumber(val)) {
            this.isRInvalid = true;
            this.currentR = null;
            this.isRNegative = false;
        } else {
            this.isRInvalid = false;
            const num = parseFloat(val);
            if (!isNaN(num)) {
                if (num > 3) { this.rInput = '3'; val = '3'; event.target.value = '3'; }
                else if (num < -5) { this.rInput = '-5'; val = '-5'; event.target.value = '-5'; }
                else { this.rInput = val; }

                const finalR = parseFloat(val);
                this.currentR = finalR;
                this.isRNegative = finalR < 0;
            } else {
                this.currentR = null;
                this.isRNegative = false;
            }
        }
        this.updateGlobalError();
        this.drawCanvas();
    }


    onCanvasMouseMove(event: MouseEvent): void {
        if (this.isRNegative || !this.currentR || this.currentR <= 0) return;

        this.isMouseOverCanvas = true;
        const canvas = this.canvasRef.nativeElement;
        const rect = canvas.getBoundingClientRect();

        const mousePxX = event.clientX - rect.left;
        const mousePxY = event.clientY - rect.top;

        const width = this.canvasSize;
        const height = this.canvasSize;
        const centerX = width / 2;
        const centerY = height / 2;
        const step = (width - 40) / 10;

        let rawX = (mousePxX - centerX) / step;
        let rawY = (centerY - mousePxY) / step;

        if (rawX > 3) rawX = 3;
        if (rawX < -5) rawX = -5;

        if (rawY > 5) rawY = 5;
        if (rawY < -3) rawY = -3;

        this.clampedX = Math.round(rawX * 100) / 100;
        this.clampedY = Math.round(rawY * 100) / 100;

        this.drawCanvas();
    }

    onCanvasMouseLeave(): void {
        this.isMouseOverCanvas = false;
        this.drawCanvas();
    }


    checkPoint(): void {
        if (this.isRNegative) { this.showMathModal = true; return; }

        const x = parseFloat(this.xInput.replace(',', '.'));
        const y = parseFloat(this.yInput.replace(',', '.'));
        const r = parseFloat(this.rInput.replace(',', '.'));

        if (this.isXInvalid || this.isYInvalid || this.isRInvalid ||
            isNaN(x) || isNaN(y) || isNaN(r)) {
            this.errorMessage = 'Заполните все поля корректными данными';
            return;
        }

        this.sendRequest(x, y, r);
    }

    onCanvasClick(): void {
        if (this.isRNegative) { this.showMathModal = true; return; }

        if (!this.currentR || this.currentR <= 0) {
            this.errorMessage = 'Введите положительный R для работы с графиком';
            return;
        }

        this.xInput = this.clampedX.toString();
        this.yInput = this.clampedY.toString();
        this.errorMessage = '';
        this.isXInvalid = false;
        this.isYInvalid = false;

        this.sendRequest(this.clampedX, this.clampedY, this.currentR);
    }

    private sendRequest(x: number, y: number, r: number): void {
        const request: PointRequest = { x, y, r };
        this.resultService.checkPoint(request).subscribe({
            next: (result) => {
                this.results.unshift(result);
                this.drawCanvas();
            },
            error: (err) => this.errorMessage = err.error?.message || 'Ошибка сервера'
        });
    }

    clearResults(): void {
        this.resultService.clearResults().subscribe({
            next: () => {
                this.results = [];
                this.drawCanvas();
            }
        });
    }

    loadResults(): void {
        this.resultService.getUserResults().subscribe({
            next: (results) => {
                this.results = results;
                this.drawCanvas();
            },
            error: (error) => console.error('Error loading results:', error)
        });
    }

    closeMathModal(): void { this.showMathModal = false; }
    toggleTheme(): void { this.themeService.toggleTheme(); }
    logout(): void {
        this.authService.logout().subscribe({
            next: () => this.router.navigate(['/login']),
            error: () => this.router.navigate(['/login'])
        });
    }


    drawCanvas(): void {
        const canvas = this.canvasRef?.nativeElement;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const colorGrid = this.isDarkTheme ? '#334155' : '#e2e8f0';
        const colorAxis = this.isDarkTheme ? '#94a3b8' : '#64748b';
        const colorText = this.isDarkTheme ? '#f1f5f9' : '#1e293b';
        const colorShape = 'rgba(99, 102, 241, 0.3)';
        const colorGhost = '#06b6d4';

        canvas.width = this.canvasSize * this.scaleFactor;
        canvas.height = this.canvasSize * this.scaleFactor;
        ctx.scale(this.scaleFactor, this.scaleFactor);

        const width = this.canvasSize;
        const height = this.canvasSize;
        const centerX = width / 2;
        const centerY = height / 2;
        const step = (width - 40) / 10;

        ctx.clearRect(0, 0, width, height);

        const rVal = (this.currentR && this.currentR > 0 && !this.isRNegative) ? this.currentR : 0;

        if (rVal > 0) {
            ctx.fillStyle = colorShape;
            const rPx = rVal * step;
            const halfRPx = (rVal / 2) * step;

            ctx.beginPath();
            ctx.rect(centerX-rPx, centerY - halfRPx, rPx, halfRPx);
            ctx.moveTo(centerX, centerY);
            ctx.lineTo(centerX-rPx, centerY);
            ctx.lineTo(centerX, centerY + halfRPx);
            ctx.arc(centerX, centerY, halfRPx, 3*Math.PI/2 , 0);
            ctx.fill();
        }

        ctx.strokeStyle = colorGrid;
        ctx.lineWidth = 1;
        ctx.beginPath();
        for (let i = -5; i <= 5; i++) {
            const pos = centerX + i * step;
            ctx.moveTo(pos, 0); ctx.lineTo(pos, height);
            ctx.moveTo(0, pos); ctx.lineTo(width, pos);
        }
        ctx.stroke();

        ctx.strokeStyle = colorAxis;
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(0, centerY); ctx.lineTo(width, centerY);
        ctx.moveTo(centerX, 0); ctx.lineTo(centerX, height);
        ctx.stroke();

        ctx.fillStyle = colorText;
        ctx.font = 'bold 12px Inter';
        ctx.fillText('X', width - 20, centerY - 15);
        ctx.fillText('Y', centerX + 15, 20);

        if (rVal > 0) {
            this.results.forEach(point => {
                if (Math.abs(point.r - rVal) < 0.001) {
                    const px = centerX + point.x * step;
                    const py = centerY - point.y * step;
                    ctx.beginPath();
                    ctx.arc(px, py, 4, 0, 2 * Math.PI);
                    ctx.fillStyle = point.hit ? '#10b981' : '#ef4444';
                    ctx.fill();
                    ctx.strokeStyle = this.isDarkTheme ? '#0f172a' : '#ffffff';
                    ctx.lineWidth = 2;
                    ctx.stroke();
                }
            });
        }

        if (rVal > 0 && this.isMouseOverCanvas) {
            const px = centerX + this.clampedX * step;
            const py = centerY - this.clampedY * step;

            ctx.beginPath();
            ctx.arc(px, py, 5, 0, 2 * Math.PI);
            ctx.fillStyle = colorGhost;
            ctx.fill();

            ctx.shadowColor = colorGhost;
            ctx.shadowBlur = 10;
            ctx.stroke();
            ctx.shadowBlur = 0;

            ctx.setLineDash([4, 4]);
            ctx.strokeStyle = colorGhost;
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(px, py); ctx.lineTo(px, centerY);
            ctx.moveTo(px, py); ctx.lineTo(centerX, py);
            ctx.stroke();
            ctx.setLineDash([]);

            ctx.fillStyle = colorText;
            ctx.font = '10px JetBrains Mono';
            ctx.fillText(`(${this.clampedX}, ${this.clampedY})`, px + 8, py - 8);
        }
    }
}