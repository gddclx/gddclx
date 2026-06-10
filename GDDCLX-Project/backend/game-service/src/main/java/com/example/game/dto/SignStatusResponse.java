package com.example.game.dto;

import lombok.Data;

/** 签到状态响应 */
@Data
public class SignStatusResponse {
    private Boolean canSign;     // 今日是否可签到（false=已签到）
    private Integer signCount;   // 连续签到天数
    private Long signReward;     // 签到奖励（200金币）
    private String lastSignDate; // 上次签到日期
}
