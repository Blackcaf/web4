package com.nlshakal.web4.service;

import com.nlshakal.web4.constants.SecurityConstants;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, RequestTracker> requests = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blacklistedIPs = new ConcurrentHashMap<>();

    public boolean isAllowed(String ipAddress) {
        if (isBlacklisted(ipAddress)) {
            return false;
        }

        RequestTracker tracker = requests.computeIfAbsent(ipAddress, k -> new RequestTracker());
        tracker.addRequest();

        tracker.cleanup();

        if (tracker.getRequestsLastMinute() > SecurityConstants.RATE_LIMIT_MAX_REQUESTS) {
            return false;
        }

        if (tracker.getRequestsLastHour() > 50) {
            if (tracker.getTotalRequests() > 100) {
                blacklistIP(ipAddress, 60);
            }
            return false;
        }

        return true;
    }

    public boolean isBlacklisted(String ipAddress) {
        LocalDateTime unblockTime = blacklistedIPs.get(ipAddress);
        if (unblockTime == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(unblockTime)) {
            blacklistedIPs.remove(ipAddress);
            return false;
        }

        return true;
    }

    public void blacklistIP(String ipAddress, int minutes) {
        blacklistedIPs.put(ipAddress, LocalDateTime.now().plusMinutes(minutes));
    }

    public void resetIP(String ipAddress) {
        requests.remove(ipAddress);
    }

    private static class RequestTracker {
        private final Map<LocalDateTime, Integer> requests = new ConcurrentHashMap<>();

        public void addRequest() {
            LocalDateTime now = LocalDateTime.now();
            requests.merge(now.withSecond(0).withNano(0), 1, Integer::sum);
        }

        public int getRequestsLastMinute() {
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            return requests.entrySet().stream()
                    .filter(e -> e.getKey().isAfter(oneMinuteAgo))
                    .mapToInt(Map.Entry::getValue)
                    .sum();
        }

        public int getRequestsLastHour() {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            return requests.entrySet().stream()
                    .filter(e -> e.getKey().isAfter(oneHourAgo))
                    .mapToInt(Map.Entry::getValue)
                    .sum();
        }

        public int getTotalRequests() {
            return requests.values().stream().mapToInt(Integer::intValue).sum();
        }

        public void cleanup() {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            requests.entrySet().removeIf(e -> e.getKey().isBefore(oneHourAgo));
        }
    }
}

