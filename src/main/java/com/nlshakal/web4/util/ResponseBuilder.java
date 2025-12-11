package com.nlshakal.web4.util;

import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;

@Component
public class ResponseBuilder {

    public Response error(Response.Status status, String message) {
        return Response.status(status)
                .entity("{\"error\": \"" + escapeJson(message) + "\"}")
                .build();
    }

    public Response tooManyRequests() {
        return error(Response.Status.fromStatusCode(429), "Слишком много запросов. Попробуйте позже.");
    }

    public Response badRequest(String message) {
        return error(Response.Status.BAD_REQUEST, message);
    }

    public Response unauthorized(String message) {
        return error(Response.Status.UNAUTHORIZED, message);
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}

