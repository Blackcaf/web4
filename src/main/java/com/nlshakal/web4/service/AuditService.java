package com.nlshakal.web4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger("AUDIT");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logSuccessfulLogin(String username, String ipAddress, String userAgent) {
        logger.info("[LOGIN_SUCCESS] username={}, ip={}, userAgent={}",
                    username, ipAddress, sanitizeUserAgent(userAgent));
    }

    private String sanitizeUserAgent(String userAgent) {
        return userAgent != null && userAgent.length() > 100
            ? userAgent.substring(0, 100) + "..."
            : userAgent;
    }

    public void logFailedLogin(String username, String ipAddress, String reason, String userAgent) {
        logger.warn("[LOGIN_FAILED] username={}, ip={}, reason={}, userAgent={}",
                    username, ipAddress, reason, sanitizeUserAgent(userAgent));
    }

    public void logSuspiciousActivity(String username, String ipAddress, String activity) {
        logger.error("[SUSPICIOUS_ACTIVITY] username={}, ip={}, activity={}",
                     username, ipAddress, activity);
    }

    public void logIPBlocked(String ipAddress, String reason, int minutes) {
        logger.error("[IP_BLOCKED] ip={}, reason={}, durationMinutes={}",
                     ipAddress, reason, minutes);
    }

    public void logRegistration(String username, String ipAddress, String method) {
        logger.info("[REGISTRATION] username={}, ip={}, authProvider={}",
                    username, ipAddress, method);
    }

    public void logPasswordChange(String username, String ipAddress) {
        String message = String.format(
                "[PASSWORD CHANGE] User: %s | IP: %s | Time: %s",
                username, ipAddress, LocalDateTime.now().format(formatter)
        );
        logger.info(message);
    }
}

