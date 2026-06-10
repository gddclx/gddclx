package com.example.game.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工游戏状态实体 — 对应数据库表 employee_game
 * 每个员工只有一条游戏记录（1:1关系）
 * 核心数值：金币(coins)、等级(coinLevel)、待领取包数(unclaimedCount)
 */
@Data
public class EmployeeGame {
    private Long id;                      // 主键
    private String employeeId;            // 员工工号
    private Long coins;                   // 当前金币总数
    private Integer coinLevel;            // 金币等级（每包金币 = 100 + coinLevel × 100）
    private Integer unclaimedCount;       // 待领取金币包数（每30分钟产1包，最多3包）
    private LocalDateTime lastCollectTime; // 上次领取时间（用于计算新产出的包数）
    private LocalDateTime updatedAt;       // 最后更新时间
    private LocalDate lastSignDate;        // 最后签到日期（用于判断今天是否已签到）
    private Integer signCount;             // 连续签到天数（断开重置）
}
