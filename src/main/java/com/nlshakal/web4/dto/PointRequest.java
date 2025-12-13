package com.nlshakal.web4.dto;

import com.nlshakal.web4.constants.ValidationConstants;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointRequest {
    @NotNull(message = "X не может быть null")
    @DecimalMin(value = "-5.0", message = ValidationConstants.X_MIN_MESSAGE)
    @DecimalMax(value = "3.0", message = ValidationConstants.X_MAX_MESSAGE)
    private Double x;

    @NotNull(message = "Y не может быть null")
    @DecimalMin(value = "-3.0", message = ValidationConstants.Y_MIN_MESSAGE)
    @DecimalMax(value = "5.0", message = ValidationConstants.Y_MAX_MESSAGE)
    private Double y;

    @NotNull(message = "R не может быть null")
    @DecimalMin(value = "0.0", inclusive = false, message = ValidationConstants.R_MIN_MESSAGE)
    @DecimalMax(value = "3.0", message = ValidationConstants.R_MAX_MESSAGE)
    private Double r;
}

