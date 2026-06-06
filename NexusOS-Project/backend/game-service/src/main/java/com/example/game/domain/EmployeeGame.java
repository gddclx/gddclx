package com.example.game.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeGame {
    private Long id;
    private String employeeId;
    private Long coins;
    private Integer coinLevel;
    private Integer unclaimedCount;
    private LocalDateTime lastCollectTime;
    private LocalDateTime updatedAt;
    private LocalDate lastSignDate;
    private Integer signCount;
}
