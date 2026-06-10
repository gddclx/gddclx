package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 签到请求DTO — 前端 → 后端
 * POST /api/attendance/checkin 的请求体
 * 前端JSON: {"userId":"A001"}
 * @JsonProperty 指定JSON字段名为 "userId"（此处与Java属性名相同，实为冗余）
 */
public class CheckinRequest {
   @JsonProperty("userId")
   private String userId;  // 员工工号

   public String getUserId() { return this.userId; }

   @JsonProperty("userId")
   public void setUserId(String userId) { this.userId = userId; }
}
