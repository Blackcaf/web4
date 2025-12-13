package com.nlshakal.web4.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    /** Уникальный идентификатор результата */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Координата X проверенной точки */
    @Column(nullable = false)
    private double x;

    /** Координата Y проверенной точки */
    @Column(nullable = false)
    private double y;

    /** Радиус использованной области */
    @Column(nullable = false)
    private double r;

    /** Результат проверки: true - попадание, false - промах */
    @Column(nullable = false)
    private boolean hit;

    /** Время проверки */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** Время выполнения проверки в микросекундах */
    @Column(nullable = false)
    private long executionTime;

    /** Пользователь, выполнивший проверку */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Конструктор для создания нового результата проверки.
     *
     * @param x координата X
     * @param y координата Y
     * @param r радиус области
     * @param hit результат попадания
     * @param timestamp время проверки
     * @param executionTime время выполнения в микросекундах
     * @param user пользователь
     */
    public Result(double x, double y, double r, boolean hit, LocalDateTime timestamp, long executionTime, User user) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.timestamp = timestamp;
        this.executionTime = executionTime;
        this.user = user;
    }

}

