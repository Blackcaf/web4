package com.nlshakal.web4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("results")
public class ReactiveResult {
    @Id
    private Long id;

    private double x;
    private double y;
    private double r;
    private boolean hit;
    private LocalDateTime timestamp;

    @Column("execution_time")
    private long executionTime;

    @Column("user_id")
    private Long userId;

    public ReactiveResult(double x, double y, double r, boolean hit, LocalDateTime timestamp, long executionTime, Long userId) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.timestamp = timestamp;
        this.executionTime = executionTime;
        this.userId = userId;
    }
}

