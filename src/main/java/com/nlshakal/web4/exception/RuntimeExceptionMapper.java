package com.nlshakal.web4.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException exception) {
        logger.error("Runtime exception occurred: {}", exception.getMessage(), exception);

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());
        error.put("timestamp", String.valueOf(System.currentTimeMillis()));

        Response.Status status = determineStatus(exception);

        return Response.status(status)
                .entity(error)
                .build();
    }

    /**
     * Определяет HTTP статус на основе текста сообщения исключения.
     *
     * @param exception исключение для анализа
     * @return подходящий HTTP статус (404, 401, 403 или 400)
     */
    private Response.Status determineStatus(RuntimeException exception) {
        String message = exception.getMessage().toLowerCase();

        if (message.contains("не найден") || message.contains("not found")) {
            return Response.Status.NOT_FOUND;
        }
        if (message.contains("unauthorized") || message.contains("invalid token") ||
            message.contains("missing") || message.contains("authorization")) {
            return Response.Status.UNAUTHORIZED;
        }
        if (message.contains("forbidden") || message.contains("access denied")) {
            return Response.Status.FORBIDDEN;
        }

        return Response.Status.BAD_REQUEST;
    }
}

