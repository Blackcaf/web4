package com.nlshakal.web4.service.oauth;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OAuthService {

    private final Map<String, OAuthProvider> providers;

    public OAuthService(List<OAuthProvider> providerList) {
        this.providers = new HashMap<>();
        for (OAuthProvider provider : providerList) {
            providers.put(provider.getProviderName(), provider);
        }
    }

    public String authenticateAndGetEmail(String code, String providerName) {
        OAuthProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null) {
            throw new RuntimeException("Неизвестный провайдер: " + providerName);
        }
        return provider.getEmail(code);
    }
}

