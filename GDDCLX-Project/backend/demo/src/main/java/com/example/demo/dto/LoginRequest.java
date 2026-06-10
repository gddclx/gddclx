package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 登录请求DTO — 前端 → 后端
 * POST /api/login 的请求体
 * 前端JSON: {"employee_id":"A001", "password":"123456"}
 * @JsonProperty("employee_id") 将前端的蛇形命名映射到Java的驼峰命名
 */
public class LoginRequest {
   @JsonProperty("employee_id")  // 前端JSON字段名为 employee_id
   private String employeeId;    // Java属性名为 employeeId（驼峰）
   private String password;      // 密码（不需要@JsonProperty，因为JSON key也是password）

   public String getEmployeeId() { return this.employeeId; }
   public String getPassword() { return this.password; }

   @JsonProperty("employee_id")
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

   public void setPassword(String password) { this.password = password; }
}
