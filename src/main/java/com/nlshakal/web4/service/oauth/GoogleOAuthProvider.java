package com.nlshakal.web4.service.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleOAuthProvider implements OAuthProvider {

    @Value("${oauth.google.client-id:}")
    private String clientId;

    @Value("${oauth.google.client-secret:}")
    private String clientSecret;

    @Value("${oauth.redirect-uri:http://localhost:4200/login}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getEmail(String code) {
        try {
            System.out.println("[Google OAuth] Starting authentication with code: " + code.substring(0, Math.min(10, code.length())) + "...");
            System.out.println("[Google OAuth] Client ID: " + (clientId != null ? clientId.substring(0, 10) + "..." : "NULL"));
            System.out.println("[Google OAuth] Redirect URI: " + redirectUri);

            String accessToken = exchangeCodeForToken(code);
            System.out.println("[Google OAuth] Access token obtained successfully");

            String email = fetchUserEmail(accessToken);
            System.out.println("[Google OAuth] User email: " + email);

            return email;
        } catch (Exception e) {
            System.err.println("[Google OAuth] Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка авторизации Google: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "google";
    }

    private String exchangeCodeForToken(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", code);
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "https://oauth2.googleapis.com/token", request, String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("access_token").asText();
    }

    private String fetchUserEmail(String accessToken) throws Exception {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
        String userInfoResponse = restTemplate.getForObject(url, String.class);
        JsonNode userNode = objectMapper.readTree(userInfoResponse);
        return userNode.path("email").asText();
    }
}

