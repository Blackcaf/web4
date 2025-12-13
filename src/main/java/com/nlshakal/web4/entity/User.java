package com.nlshakal.web4.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** Уникальный идентификатор пользователя */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email пользователя (используется как логин) */
    @Column(unique = true, nullable = false)
    private String username;

    /** BCrypt хеш пароля (NULL для OAuth пользователей) */
    @Column(nullable = true)
    private String passwordHash;

    /** Провайдер аутентификации: "local", "google", "yandex" */
    @Column(name = "auth_provider")
    private String authProvider;

    /**
     * Конструктор для создания нового пользователя.
     *
     * @param username email пользователя
     * @param passwordHash BCrypt хеш пароля (может быть null для OAuth)
     * @param authProvider провайдер аутентификации
     */
    public User(String username, String passwordHash, String authProvider) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.authProvider = authProvider;
    }
}