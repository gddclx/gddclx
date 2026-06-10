package com.example.game.dto;

import lombok.Data;

/** 升级响应 */
@Data
public class UpgradeResponse {
    private boolean success;    // 是否升级成功
    private String message;     // 提示信息
    private Long coinsSpent;    // 花费金币数
    private Long totalCoins;    // 升级后剩余金币
    private Integer newLevel;   // 新等级
}
