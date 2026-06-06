package com.example.game.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GameCoinRecord {
    private Long id;
    private String employeeId;
    private String type;
    private Long amount;
    private Long balanceAfter;
    private String description;
    private LocalDateTime createdAt;
}
