package com.nlshakal.web4.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * JAX-RS ресурс для мониторинга состояния приложения.
 * Предоставляет информацию о времени работы и статусе сервера.
 */
@Component
@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    private static final LocalDateTime START_TIME = LocalDateTime.now();

    /**
     * Проверка здоровья приложения.
     * GET /api/health
     *
     * @return Response с информацией о статусе, времени работы и версии
     */
    @GET
    public Response healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        health.put("uptime", getUptime());
        health.put("application", "Web4 Area Check");
        health.put("version", "1.0-SNAPSHOT");

        return Response.ok(health).build();
    }

    private String getUptime() {
        long uptimeSeconds = java.time.Duration.between(START_TIME, LocalDateTime.now()).getSeconds();
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

