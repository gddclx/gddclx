package com.example.demo.dto;

import lombok.Data;

@Data
public class UpgradeResponse {
    private boolean success;
    private String message;
    private Long coinsSpent;
    private Long totalCoins;
    private Integer newLevel;
}
