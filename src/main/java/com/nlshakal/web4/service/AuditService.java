package com.nlshakal.web4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger("AUDIT");

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

    public void logRegistration(String username, String ipAddress, String method) {
        logger.info("[REGISTRATION] username={}, ip={}, authProvider={}",
                    username, ipAddress, method);
    }
}

