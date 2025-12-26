package com.nlshakal.web4.util;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtUtil jwtUtil;

    public Long extractUserId(HttpHeaders headers) {
        String token = extractToken(headers);
        validateToken(token);
        return jwtUtil.getUserIdFromToken(token);
    }

    public Long extractUserId(HttpServletRequest request) {
        String token = extractToken(request);
        validateToken(token);
        return jwtUtil.getUserIdFromToken(token);
    }

    private String extractToken(HttpHeaders headers) {
        Cookie authCookie = headers.getCookies().get("auth_token");
        if (authCookie != null && authCookie.getValue() != null && !authCookie.getValue().isEmpty()) {
            return authCookie.getValue();
        }

        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        throw new RuntimeException("Missing or invalid authentication token");
    }

    private void validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
    }

    private String extractToken(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.isEmpty()) {
                        return value;
                    }
                }
            }
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        throw new RuntimeException("Missing or invalid authentication token");
    }


}
