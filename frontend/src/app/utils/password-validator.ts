export interface PasswordStrengthResult {
    strength: number;
    message: string;
    color: string;
}

/**
 * Индикатор силы пароля на основе длины.
 * Предоставляет визуальную оценку надежности пароля для UI.
 */
export class PasswordStrengthIndicator {
    /**
     * Вычисляет визуальную силу пароля на основе его длины.
     *
     * @param password - Пароль для анализа
     * @param isRegisterMode - Флаг режима регистрации (индикатор показывается только при регистрации)
     * @returns Объект с силой (0-4), сообщением и цветом
     */
    calculateVisualStrength(password: string, isRegisterMode: boolean): PasswordStrengthResult {
        if (!isRegisterMode || !password) {
            return { strength: 0, message: '', color: '#e5e7eb' };
        }

        const length = password.length;
        let visualStrength = 0;

        if (length > 0) visualStrength = 1;
        if (length >= 8) visualStrength = 2;
        if (length >= 12) visualStrength = 3;
        if (length >= 16) visualStrength = 4;

        return {
            strength: visualStrength,
            message: this.getStrengthMessage(visualStrength),
            color: this.getStrengthColor(visualStrength)
        };
    }

    private getStrengthMessage(strength: number): string {
        switch (strength) {
            case 0:
                return '';
            case 1:
                return 'Too short';
            case 2:
                return 'Fair length';
            case 3:
                return 'Good length';
            case 4:
                return 'Strong length';
            default:
                return '';
        }
    }

    private getStrengthColor(strength: number): string {
        switch (strength) {
            case 0:
                return '#e5e7eb';
            case 1:
                return '#ef4444';
            case 2:
                return '#f59e0b';
            case 3:
                return '#3b82f6';
            case 4:
                return '#10b981';
            default:
                return '#e5e7eb';
        }
    }
}

