package com.nlshakal.web4.util;

import com.nlshakal.web4.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final long expirationTime;
    private final SecretKey key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.expirationTime = jwtProperties.getExpiration();
        String secret = jwtProperties.getSecret();

        if (secret == null || secret.trim().isEmpty()) {
            logger.error("JWT secret is not configured!");
            throw new IllegalStateException("JWT secret is not configured! Check your .env file.");
        }

        if (secret.getBytes().length * 8 < 256) {
            logger.warn("JWT secret is less than 256 bits. Recommended: 256+ bits for HS256");
        }

        logger.info("JWT initialized: expiration={}ms, key strength={}bits",
                    expirationTime, secret.getBytes().length * 8);

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }

    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public Long getUserIdFromToken(String token) {
        return extractUserId(token);
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}

