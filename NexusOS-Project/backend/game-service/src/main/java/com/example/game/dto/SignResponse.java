package com.example.game.dto;

import lombok.Data;

@Data
public class SignResponse {
    private Boolean success;
    private String message;
    private Long coinsEarned;
    private Long totalCoins;
    private Integer signCount;
}
