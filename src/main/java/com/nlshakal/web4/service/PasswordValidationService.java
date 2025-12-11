package com.nlshakal.web4.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PasswordValidationService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$"
    );

    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{12,}$"
    );

    public boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean isStrong(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    public String getValidationMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Пароль не может быть пустым";
        }

        if (password.length() < 8) {
            return "Пароль должен содержать минимум 8 символов";
        }

        if (!password.matches(".*[A-Za-z].*")) {
            return "Пароль должен содержать хотя бы одну букву";
        }

        if (!password.matches(".*\\d.*")) {
            return "Пароль должен содержать хотя бы одну цифру";
        }

        if (isCommonPassword(password)) {
            return "Этот пароль слишком распространен. Выберите более уникальный";
        }

        return "OK";
    }

    public int getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0;
        }

        int strength = 0;

        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) {
            strength++;
        }

        if (password.matches(".*\\d.*")) {
            strength++;
        }

        if (password.matches(".*[@$!%*?&#].*")) {
            strength++;
        }

        return Math.min(strength, 4);
    }

    private boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        String[] commonPasswords = {
                "password", "123456", "12345678", "qwerty", "abc123",
                "password123", "111111", "123123", "admin", "letmein",
                "welcome", "monkey", "dragon", "master", "sunshine",
                "princess", "football", "iloveyou", "admin123", "root"
        };

        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }

        return false;
    }

    public String getPasswordSuggestion(String password) {
        if (isStrong(password)) {
            return "Отличный пароль!";
        }

        StringBuilder suggestion = new StringBuilder("Для улучшения пароля: ");

        if (password.length() < 12) {
            suggestion.append("увеличьте длину до 12+ символов, ");
        }

        if (!password.matches(".*[a-z].*")) {
            suggestion.append("добавьте строчные буквы, ");
        }

        if (!password.matches(".*[A-Z].*")) {
            suggestion.append("добавьте заглавные буквы, ");
        }

        if (!password.matches(".*\\d.*")) {
            suggestion.append("добавьте цифры, ");
        }

        if (!password.matches(".*[@$!%*?&#].*")) {
            suggestion.append("добавьте спецсимволы (@$!%*?&#), ");
        }

        return suggestion.toString().replaceAll(", $", "");
    }
}

