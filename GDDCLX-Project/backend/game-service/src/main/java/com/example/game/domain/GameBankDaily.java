package com.example.game.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 银行每日利率实体 — 对应数据库表 game_bank_daily
 * 每天一条记录，包含上午/下午各3个部门的利率
 * 利率范围：0.85 ~ 1.30，每天随机洗牌分配
 */
@Data
public class GameBankDaily {
    private Long id;
    private LocalDate bankDate;           // 利率生效日期
    private BigDecimal hrRate;            // 上午-人力资源部利率
    private BigDecimal rdRate;            // 上午-研发部利率
    private BigDecimal salesRate;         // 上午-销售部利率
    private BigDecimal pmHrRate;          // 下午-人力资源部利率
    private BigDecimal pmRdRate;          // 下午-研发部利率
    private BigDecimal pmSalesRate;       // 下午-销售部利率
    private LocalDateTime createdAt;      // 记录创建时间
}
