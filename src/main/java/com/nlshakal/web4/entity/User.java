package com.nlshakal.web4.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = true)
    private String passwordHash;

    @Column(name = "auth_provider")
    private String authProvider;

    public User(String username, String passwordHash, String authProvider) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.authProvider = authProvider;
    }
}