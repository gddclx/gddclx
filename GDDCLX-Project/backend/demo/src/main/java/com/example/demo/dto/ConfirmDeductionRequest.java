package com.example.demo.dto;

import lombok.Data;

/**
 * 扣款确认/赦免请求DTO — 前端 → 后端
 * POST /api/performance/confirm-late 等4个端点的请求体
 * type 字段区分迟到(late)还是早退(early)
 * 唯一使用 @Data 注解的DTO请求类
 */
@Data
public class ConfirmDeductionRequest {
    private String employeeId;  // 目标员工工号
    private String type;        // 类型："late"(迟到) 或 "early"(早退)
}
