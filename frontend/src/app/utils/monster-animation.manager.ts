export type MonsterState = 'idle' | 'peek' | 'shy' | 'error';

export class MonsterAnimationManager {
    private state: MonsterState = 'idle';
    private isBlinking: boolean = false;
    private blinkInterval: any;
    private mouseX: number = 0;
    private mouseY: number = 0;

    getState(): MonsterState {
        return this.state;
    }

    isInBlinkingState(): boolean {
        return this.isBlinking;
    }

    startBlinkingLoop(): void {
        const randomTime = Math.random() * 3000 + 2000;
        this.blinkInterval = setTimeout(() => {
            if (this.state === 'idle' || this.state === 'peek') {
                this.isBlinking = true;
                setTimeout(() => this.isBlinking = false, 200);
            }
            this.startBlinkingLoop();
        }, randomTime);
    }

    stopBlinkingLoop(): void {
        if (this.blinkInterval) {
            clearTimeout(this.blinkInterval);
        }
    }

    updateMousePosition(clientX: number, clientY: number, windowWidth: number, windowHeight: number): void {
        if (this.state === 'shy' || this.state === 'error') return;

        const cx = windowWidth / 2;
        const cy = windowHeight / 2;
        this.mouseX = (clientX - cx) / (windowWidth / 3);
        this.mouseY = (clientY - cy) / (windowHeight / 3);
    }

    getLookTransform(multiplier: number): string {
        if (this.state === 'shy' || this.state === 'error') {
            return 'translate(0px, 0px)';
        }

        const x = Math.min(Math.max(this.mouseX * multiplier, -15), 15);
        const y = Math.min(Math.max(this.mouseY * multiplier, -10), 10);
        return `translate(${x}px, ${y}px)`;
    }

    getYellowFaceTransform(): string {
        if (this.state === 'shy' || this.state === 'error') {
            return 'translate(0px, 0px)';
        }

        const threshold = -0.1;
        const scaleX = this.mouseX < threshold ? -1 : 1;
        const smoothY = Math.min(Math.max(this.mouseY * 5, -5), 5);
        const moveX = Math.min(Math.max(this.mouseX * 3, -3), 3);

        return `translate(${moveX}px, ${smoothY}px) scale(${scaleX}, 1)`;
    }

    /**
     * Запускает анимацию ошибки (испуганное выражение лица).
     * Автоматически возвращается в idle состояние через 1.5 секунды.
     */
    triggerErrorAnimation(): void {
        this.state = 'error';
        setTimeout(() => this.state = 'idle', 1500);
    }

    onEmailFocus(): void {
        if (this.state !== 'error') {
            this.state = 'peek';
        }
    }

    onPasswordFocus(showPassword: boolean): void {
        if (this.state === 'error') return;
        this.state = showPassword ? 'idle' : 'shy';
    }

    onBlur(): void {
        if (this.state !== 'error') {
            this.state = 'idle';
        }
    }
}

