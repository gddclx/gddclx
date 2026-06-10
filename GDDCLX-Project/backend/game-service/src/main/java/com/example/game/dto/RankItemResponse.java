package com.example.game.dto;

import lombok.Data;

/** 排行榜单条记录 */
@Data
public class RankItemResponse {
    private Integer rank;     // 排名（1开始）
    private String name;      // 员工姓名（LEFT JOIN查得）
    private Long coins;       // 金币数
    private Long maxCoins;    // 第一名金币数（用于进度条计算）
}
