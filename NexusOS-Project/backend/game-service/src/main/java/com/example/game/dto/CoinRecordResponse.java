package com.example.game.dto;

import lombok.Data;

@Data
public class CoinRecordResponse {
    private Long id;
    private String type;
    private Long amount;
    private Long balanceAfter;
    private String description;
    private String createdAt;
}
