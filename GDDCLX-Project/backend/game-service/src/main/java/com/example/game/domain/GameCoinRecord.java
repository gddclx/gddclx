package com.example.game.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 金币流水记录实体 — 对应数据库表 game_coin_record
 * 记录所有金币变动（投资、结算、领取、签到、升级）
 */
@Data
public class GameCoinRecord {
    private Long id;
    private String employeeId;         // 员工工号
    private String type;               // 类型：invest(投资) / settlement(结算) / collect(领取) / sign(签到) / upgrade(升级)
    private Long amount;               // 变动金额（正=收入, 负=支出）
    private Long balanceAfter;         // 变动后余额
    private String description;        // 描述（如 "签到奖励"、"投资-研发"）
    private LocalDateTime createdAt;   // 变动时间
}
