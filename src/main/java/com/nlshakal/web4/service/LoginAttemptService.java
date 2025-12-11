package com.nlshakal.web4.service;

import com.nlshakal.web4.constants.SecurityConstants;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {


    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public void recordFailedAttempt(String username) {
        LoginAttempt attempt = attempts.computeIfAbsent(username, k -> new LoginAttempt());
        attempt.incrementFailedAttempts();
        attempt.setLastAttempt(LocalDateTime.now());
    }

    public void resetAttempts(String username) {
        attempts.remove(username);
    }

    public boolean requiresCaptcha(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) {
            return false;
        }
        return attempt.getFailedAttempts() >= SecurityConstants.MAX_LOGIN_ATTEMPTS;
    }

    public int getFailedAttempts(String username) {
        LoginAttempt attempt = attempts.get(username);
        return attempt != null ? attempt.getFailedAttempts() : 0;
    }

    public long getRemainingLockTime(String username) {
        return 0;
    }

    private static class LoginAttempt {
        private int failedAttempts = 0;
        private LocalDateTime lastAttempt;

        public void incrementFailedAttempts() {
            this.failedAttempts++;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public LocalDateTime getLastAttempt() {
            return lastAttempt;
        }

        public void setLastAttempt(LocalDateTime lastAttempt) {
            this.lastAttempt = lastAttempt;
        }
    }
}

