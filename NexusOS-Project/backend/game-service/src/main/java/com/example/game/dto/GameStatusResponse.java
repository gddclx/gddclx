package com.example.game.dto;

import lombok.Data;

@Data
public class GameStatusResponse {
    private Long coins;
    private Integer coinLevel;
    private Integer unclaimedCount;
    private Integer maxUnclaimed;
    private Long nextCollectSeconds;
    private Long upgradeCost;
    private Integer currentIncome;
}
