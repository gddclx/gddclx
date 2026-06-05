package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GameInvestment {
    private Long id;
    private Long employeeId;
    private Long amount;
    private Integer optionType;
    private Long resultCoins;
    private LocalDate investDate;
    private Integer periodType;
    private Integer settled;
    private LocalDateTime createdAt;
}
