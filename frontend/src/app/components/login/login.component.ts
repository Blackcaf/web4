import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { LoginRequest } from '../../models/auth.model';
import { environment } from '../../../environments/environment';
import { MonsterAnimationManager, MonsterState } from '../../utils/monster-animation.manager';
import { CaptchaManager } from '../../utils/captcha.manager';
import { PasswordStrengthIndicator } from '../../utils/password-validator';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
    username: string = '';
    password: string = '';
    errorMessage: string = '';

    isLoading: boolean = false;
    showPassword: boolean = false;
    isRegisterMode: boolean = false;
    isDarkTheme: boolean = false;

    failedAttempts: number = 0;

    private monsterManager = new MonsterAnimationManager();
    private captchaManager = new CaptchaManager(environment.recaptcha.siteKey);
    private passwordStrengthIndicator = new PasswordStrengthIndicator();

    passwordStrength: number = 0;
    passwordStrengthMessage: string = '';
    passwordStrengthColor: string = '#e5e7eb';

    constructor(
        private authService: AuthService,
        private themeService: ThemeService,
        private router: Router,
        private route: ActivatedRoute
    ) {}

    get monsterState(): MonsterState {
        return this.monsterManager.getState();
    }

    get isBlinking(): boolean {
        return this.monsterManager.isInBlinkingState();
    }

    get showCaptcha(): boolean {
        return this.captchaManager.shouldShowCaptcha();
    }

    get captchaSiteKey(): string {
        return environment.recaptcha.siteKey;
    }

    ngOnInit(): void {
        this.themeService.theme$.subscribe(theme => this.isDarkTheme = theme === 'dark');
        this.route.queryParams.subscribe(params => {
            if (params['code']) this.handleSocialCallback(params['code'], params['state']);
        });
        this.monsterManager.startBlinkingLoop();
    }

    ngOnDestroy(): void {
        this.monsterManager.stopBlinkingLoop();
    }

    @HostListener('document:mousemove', ['$event'])
    onMouseMove(e: MouseEvent): void {
        this.monsterManager.updateMousePosition(
            e.clientX,
            e.clientY,
            window.innerWidth,
            window.innerHeight
        );
    }

    getLookTransform(multiplier: number): string {
        return this.monsterManager.getLookTransform(multiplier);
    }

    getYellowFaceTransform(): string {
        return this.monsterManager.getYellowFaceTransform();
    }

    onEmailFocus(): void {
        this.monsterManager.onEmailFocus();
    }

    onPasswordFocus(): void {
        this.monsterManager.onPasswordFocus(this.showPassword);
    }

    onBlur(): void {
        this.monsterManager.onBlur();
    }

    togglePasswordVisibility(): void {
        this.showPassword = !this.showPassword;
        this.monsterManager.onPasswordFocus(this.showPassword);
    }

    triggerErrorAnimation(): void {
        this.monsterManager.triggerErrorAnimation();
    }

    toggleTheme() { this.themeService.toggleTheme(); }
    toggleMode() { this.isRegisterMode = !this.isRegisterMode; this.errorMessage = ''; }

    handleSocialCallback(code: string, provider: 'google' | 'yandex'): void {
        this.isLoading = true;
        window.history.replaceState({}, document.title, window.location.pathname);
        this.authService.socialLogin(code, provider).subscribe({
            next: () => { this.isLoading = false; this.router.navigate(['/main']); },
            error: (err: any) => { this.isLoading = false; this.errorMessage = 'Auth Error'; this.triggerErrorAnimation(); }
        });
    }

    onSubmit(): void {
        this.errorMessage = '';

        if (!this.username || !this.password) {
            this.errorMessage = 'Fill fields';
            this.triggerErrorAnimation();
            return;
        }

        let captchaToken: string | undefined = undefined;
        if (this.captchaManager.shouldShowCaptcha()) {
            captchaToken = this.captchaManager.getToken();
            if (!captchaToken) {
                this.errorMessage = 'Please complete the reCAPTCHA';
                this.triggerErrorAnimation();
                return;
            }
        }

        const req: LoginRequest = {
            username: this.username,
            password: this.password,
            captchaToken: captchaToken
        };

        const obs = this.isRegisterMode ? this.authService.register(req) : this.authService.login(req);
        obs.subscribe({
            next: (response: any) => {
                if (response.token) {
                    this.captchaManager.hide();
                    this.failedAttempts = 0;
                    this.router.navigate(['/main']);
                } else {
                    this.failedAttempts = response.failedAttempts || 0;
                    this.errorMessage = response.message || 'Invalid credentials';
                    this.triggerErrorAnimation();

                    const needsCaptcha = response.requiresCaptcha || false;

                    if (needsCaptcha && !this.captchaManager.shouldShowCaptcha()) {
                        this.captchaManager.show();
                        setTimeout(() => this.captchaManager.render(), 100);
                    } else if (needsCaptcha && this.captchaManager.shouldShowCaptcha()) {
                        this.captchaManager.reset();
                    }
                }
            },
            error: (e: any) => {
                const message = e.error?.message || e.error?.error || 'Error';
                const isCaptchaMessage = message.toLowerCase().includes('recaptcha');
                this.errorMessage = isCaptchaMessage ? 'Please complete the reCAPTCHA' : message;
                this.triggerErrorAnimation();

                if (isCaptchaMessage && !this.captchaManager.shouldShowCaptcha()) {
                    this.captchaManager.show();
                    setTimeout(() => this.captchaManager.render(), 100);
                } else if (isCaptchaMessage && this.captchaManager.shouldShowCaptcha()) {
                    this.captchaManager.reset();
                }
            }
        });
    }

    checkPasswordStrength(): void {
        const result = this.passwordStrengthIndicator.calculateVisualStrength(this.password, this.isRegisterMode);
        this.passwordStrength = result.strength;
        this.passwordStrengthMessage = result.message;
        this.passwordStrengthColor = result.color;
    }

    onPasswordChange(): void {
        this.checkPasswordStrength();
    }

    loginWithGoogle() { window.location.href = `${environment.oauth.google.authUrl}?client_id=${environment.oauth.google.clientId}&redirect_uri=${environment.redirectUri}&response_type=code&scope=email profile&state=google&prompt=select_account`; }
    loginWithYandex() { window.location.href = `${environment.oauth.yandex.authUrl}?client_id=${environment.oauth.yandex.clientId}&redirect_uri=${environment.redirectUri}&response_type=code&state=yandex`; }
}