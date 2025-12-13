package com.nlshakal.web4.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO для ответа с результатом проверки точки.
 * Содержит все данные о проверке, включая время выполнения.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    /** Уникальный идентификатор результата */
    private Long id;

    /** Координата X проверенной точки */
    private double x;

    /** Координата Y проверенной точки */
    private double y;

    /** Радиус использованной области */
    private double r;

    /** Результат проверки: true - попадание, false - промах */
    private boolean hit;

    /** Время проверки */
    private LocalDateTime timestamp;

    /** Время выполнения проверки в микросекундах */
    private long executionTime;
}

