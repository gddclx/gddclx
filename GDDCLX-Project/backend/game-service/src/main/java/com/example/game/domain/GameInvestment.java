package com.example.game.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 投资记录实体 — 对应数据库表 game_investment
 * 每天上/下午各可投资一次，次日0:00定时结算
 */
@Data
public class GameInvestment {
    private Long id;
    private String employeeId;            // 投资者工号
    private Long amount;                  // 投资金额（金币数）
    private Integer optionType;           // 选择的部门：1=HR, 2=研发, 3=销售
    private Long resultCoins;             // 结算后拿回的金币数（结算前为null）
    private LocalDate investDate;         // 投资日期
    private Integer periodType;           // 时段：1=上午, 2=下午
    private Integer settled;              // 是否已结算：0=未结算, 1=已结算
    private LocalDateTime createdAt;      // 投资时间
}
