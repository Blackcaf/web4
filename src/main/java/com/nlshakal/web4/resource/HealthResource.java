package com.nlshakal.web4.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @Value("${oauth.google.client-id:NOT_SET}")
    private String googleClientId;

    @Value("${oauth.yandex.client-id:NOT_SET}")
    private String yandexClientId;

    @Value("${oauth.redirect-uri:NOT_SET}")
    private String redirectUri;

    @GET
    @Path("/oauth")
    public Response checkOAuthConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("googleClientId", maskSecret(googleClientId));
        config.put("yandexClientId", maskSecret(yandexClientId));
        config.put("redirectUri", redirectUri);
        config.put("status", "OK");

        return Response.ok(config).build();
    }

    private String maskSecret(String secret) {
        if (secret == null || secret.equals("NOT_SET") || secret.isEmpty()) {
            return "NOT_SET";
        }
        int len = Math.min(10, secret.length());
        return secret.substring(0, len) + "...";
    }
}

