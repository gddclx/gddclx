package com.example.demo.dto;

import lombok.Data;

@Data
public class CollectResponse {
    private boolean success;
    private String message;
    private Long coinsCollected;
    private Long totalCoins;
}
