package com.example.game.dto;

import lombok.Data;

/** 金币领取响应 */
@Data
public class CollectResponse {
    private boolean success;       // 是否领取成功
    private String message;        // 提示信息
    private Long coinsCollected;   // 本次领取金币数
    private Long totalCoins;       // 领取后金币总数
}
