export class CaptchaManager {
    private showCaptcha: boolean = false;
    private captchaSolved: boolean = false;
    private captchaInitialized: boolean = false;
    private readonly siteKey: string;

    constructor(siteKey: string) {
        this.siteKey = siteKey;
    }

    shouldShowCaptcha(): boolean {
        return this.showCaptcha;
    }

    show(): void {
        this.showCaptcha = true;
        this.captchaSolved = false;
        this.captchaInitialized = false;
    }

    hide(): void {
        this.showCaptcha = false;
        this.captchaSolved = false;
        this.captchaInitialized = false;
    }

    getToken(): string | undefined {
        if (typeof (window as any).grecaptcha !== 'undefined') {
            const token = (window as any).grecaptcha.getResponse();
            return token || undefined;
        }
        return undefined;
    }

    reset(): void {
        if (typeof (window as any).grecaptcha !== 'undefined') {
            try {
                (window as any).grecaptcha.reset();
                this.captchaSolved = false;
            } catch (e) {
                console.log('reCAPTCHA reset error (safe to ignore):', e);
            }
        }
    }

    /**
     * Рендерит CAPTCHA виджет в DOM.
     * Автоматически повторяет попытки если grecaptcha еще не загружен.
     *
     * @param retry - Счетчик попыток для рекурсивного вызова
     */
    render(retry: number = 0): void {
        const maxRetries = 10;
        const grecaptcha = (window as any).grecaptcha;

        if (typeof grecaptcha === 'undefined') {
            if (retry < maxRetries) {
                setTimeout(() => this.render(retry + 1), 300);
            }
            return;
        }

        const container = document.querySelector('.g-recaptcha') as HTMLElement;
        if (!container) return;

        if (container.hasChildNodes()) {
            container.innerHTML = '';
        }

        try {
            grecaptcha.render(container, {
                sitekey: this.siteKey,
                'expired-callback': () => {
                    this.captchaSolved = false;
                }
            });
            this.captchaInitialized = true;
        } catch (e) {
            console.log('reCAPTCHA render error:', e);
            if (retry < 3) {
                container.innerHTML = '';
                setTimeout(() => this.render(retry + 1), 500);
            }
        }
    }
}

