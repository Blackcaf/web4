package com.nlshakal.web4.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JAX-RS ExceptionMapper для обработки ошибок Jakarta Validation.
 * Преобразует ConstraintViolationException в структурированный JSON ответ с деталями валидации.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /**
     * Преобразует исключение валидации в HTTP ответ 400 Bad Request.
     *
     * @param exception исключение с нарушениями ограничений
     * @return Response со списком сообщений об ошибках валидации
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Validation failed");
        error.put("details", exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}

