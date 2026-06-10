package com.example.game.dto;

import lombok.Data;

/** 投资请求 — 前端→后端 */
@Data
public class InvestRequest {
    private Long amount;        // 投资金额（金币数）
    private Integer optionType; // 选择的部门：1=HR, 2=研发, 3=销售
}
