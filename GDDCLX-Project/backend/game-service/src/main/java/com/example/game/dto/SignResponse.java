package com.example.game.dto;

import lombok.Data;

/** 签到响应 */
@Data
public class SignResponse {
    private Boolean success;     // 是否签到成功
    private String message;      // 提示信息
    private Long coinsEarned;    // 签到获得金币（固定200）
    private Long totalCoins;     // 签到后金币总数
    private Integer signCount;   // 连续签到天数
}
