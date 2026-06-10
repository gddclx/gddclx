package com.example.game.dto;

import lombok.Data;

/** 金币流水记录响应 */
@Data
public class CoinRecordResponse {
    private Long id;             // 记录ID
    private String type;         // 类型：invest/settlement/collect/sign/upgrade
    private Long amount;         // 变动金额（正=收入, 负=支出）
    private Long balanceAfter;   // 变动后余额
    private String description;  // 描述
    private String createdAt;    // 记录时间
}
