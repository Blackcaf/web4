package com.nlshakal.web4.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import org.springframework.http.HttpHeaders;
import java.util.List;

@Component
public class HttpRequestHelper {

    public String extractClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isInvalidIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIP(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalidIP(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }

    public String extractUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }

    private String firstHeader(HttpHeaders headers, String name) {
        List<String> values = headers.get(name);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }

    private boolean isInvalidIP(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
}

