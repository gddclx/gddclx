package com.example.game.dto;

import lombok.Data;

@Data
public class RankItemResponse {
    private Integer rank;
    private String name;
    private Long coins;
    private Long maxCoins;
}
