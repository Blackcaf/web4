package com.nlshakal.web4.service.oauth;

public interface OAuthProvider {
    String getEmail(String code);
    String getProviderName();
}

