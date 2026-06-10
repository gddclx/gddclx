package com.example.game.dto;

import lombok.Data;

/** 游戏状态响应 — 金币、等级、待领取包数、升级费用 */
@Data
public class GameStatusResponse {
    private Long coins;                 // 当前金币数
    private Integer coinLevel;          // 金币等级（每包产出 = 100 + level）
    private Integer unclaimedCount;     // 当前可领取包数
    private Integer maxUnclaimed;       // 最大可累积包数（3）
    private Long nextCollectSeconds;    // 距离下一包剩余的秒数
    private Long upgradeCost;           // 升级所需金币 = (level+1)×1000
    private Integer currentIncome;      // 当前每包金币产出量
}
