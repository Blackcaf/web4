package com.nlshakal.web4.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class PasswordValidationService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$"
    );

    public boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
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
}