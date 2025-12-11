package com.nlshakal.web4.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    public static final int MAX_LOGIN_ATTEMPTS = 5;

    public static final int RATE_LIMIT_MAX_REQUESTS = 10;
    public static final long RATE_LIMIT_WINDOW_MINUTES = 1;

    public static final int SUSPICIOUS_ACTIVITY_THRESHOLD = 10;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int STRONG_PASSWORD_LENGTH = 12;

    public static final String AUTH_PROVIDER_LOCAL = "local";
    public static final String AUTH_PROVIDER_GOOGLE = "google";
    public static final String AUTH_PROVIDER_YANDEX = "yandex";

    public static final String BEARER_PREFIX = "Bearer ";
}

