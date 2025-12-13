package com.nlshakal.web4.dto;

import com.nlshakal.web4.constants.ValidationConstants;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO для запроса проверки попадания точки в область.
 * Содержит Jakarta Validation аннотации для валидации координат.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointRequest {
    /** Координата X (допустимый диапазон: [-5, 3]) */
    @NotNull(message = "X не может быть null")
    @DecimalMin(value = "-5.0", message = ValidationConstants.X_MIN_MESSAGE)
    @DecimalMax(value = "3.0", message = ValidationConstants.X_MAX_MESSAGE)
    private Double x;

    /** Координата Y (допустимый диапазон: [-3, 5]) */
    @NotNull(message = "Y не может быть null")
    @DecimalMin(value = "-3.0", message = ValidationConstants.Y_MIN_MESSAGE)
    @DecimalMax(value = "5.0", message = ValidationConstants.Y_MAX_MESSAGE)
    private Double y;

    /** Радиус области (допустимый диапазон: (0, 3]) */
    @NotNull(message = "R не может быть null")
    @DecimalMin(value = "0.0", inclusive = false, message = ValidationConstants.R_MIN_MESSAGE)
    @DecimalMax(value = "3.0", message = ValidationConstants.R_MAX_MESSAGE)
    private Double r;
}

