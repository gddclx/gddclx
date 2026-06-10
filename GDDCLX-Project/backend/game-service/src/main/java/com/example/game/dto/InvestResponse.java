package com.example.game.dto;

import lombok.Data;

/** 投资响应 */
@Data
public class InvestResponse {
    private boolean success;       // 是否投资成功
    private String message;        // 提示信息
    private Long amountInvested;   // 投资金额
    private String departmentName; // 投资部门名
    private Long remainingCoins;   // 投资后剩余金币
}
