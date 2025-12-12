package com.nlshakal.web4.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

    @Value("${captcha.secret:}")
    private String captchaSecret;

    @Value("${captcha.enabled:false}")
    private boolean captchaEnabled;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean verifyCaptcha(String token) {
        if (!captchaEnabled || captchaSecret == null || captchaSecret.isEmpty()) {
            return true;
        }

        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            String url = String.format("%s?secret=%s&response=%s",
                RECAPTCHA_VERIFY_URL, captchaSecret, token);

            String response = restTemplate.postForObject(url, null, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            return jsonNode.has("success") && jsonNode.get("success").asBoolean();
        } catch (Exception e) {
            System.err.println("Captcha verification error: " + e.getMessage());
            return false;
        }
    }
}

