package com.nlshakal.web4.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для хеширования и проверки паролей.
 * Использует BCrypt алгоритм через Spring Security PasswordEncoder.
 */
@Service
public class PasswordHashingService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordHashingService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Хеширует пароль с использованием BCrypt.
     *
     * @param rawPassword исходный пароль в открытом виде
     * @return BCrypt хеш пароля
     */
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Проверяет соответствие пароля его хешу.
     *
     * @param rawPassword пароль в открытом виде
     * @param hashedPassword BCrypt хеш для сравнения
     * @return true если пароль соответствует хешу
     */
    public boolean verify(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}

