package com.nlshakal.web4.service.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class YandexOAuthProvider implements OAuthProvider {

    @Value("${YANDEX_CLIENT_ID:}")
    private String clientId;

    @Value("${YANDEX_CLIENT_SECRET:}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getEmail(String code) {
        try {
            String accessToken = exchangeCodeForToken(code);
            return fetchUserEmail(accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка авторизации Yandex: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "yandex";
    }

    private String exchangeCodeForToken(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "https://oauth.yandex.ru/token", request, String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("access_token").asText();
    }

    private String fetchUserEmail(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "OAuth " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "https://login.yandex.ru/info", HttpMethod.GET, entity, String.class
        );

        JsonNode userNode = objectMapper.readTree(response.getBody());
        return userNode.path("default_email").asText();
    }
}

