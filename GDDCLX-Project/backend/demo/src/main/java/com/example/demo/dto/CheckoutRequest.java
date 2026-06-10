package com.example.demo.dto;

import lombok.Data;

/**
 * 签退请求DTO — 前端 → 后端
 * POST /api/attendance/checkout 的请求体
 * 使用Lombok @Data 自动生成getter/setter（DTO中唯一使用Lombok的请求类之一）
 */
@Data
public class CheckoutRequest {
    private String userId;  // 员工工号
}
