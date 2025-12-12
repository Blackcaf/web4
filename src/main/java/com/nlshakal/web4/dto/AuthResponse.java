package com.nlshakal.web4.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private String message;
    private boolean requiresCaptcha;
    private int failedAttempts;
}

