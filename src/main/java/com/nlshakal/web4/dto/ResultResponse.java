package com.nlshakal.web4.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Long id;
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private LocalDateTime timestamp;
    private long executionTime;
}

