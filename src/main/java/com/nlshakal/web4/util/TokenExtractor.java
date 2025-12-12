package com.nlshakal.web4.util;

import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtUtil jwtUtil;


    public Long extractUserId(HttpHeaders headers) {
        String token = extractToken(headers);
        validateToken(token);
        return jwtUtil.getUserIdFromToken(token);
    }

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    private void validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
    }
}

