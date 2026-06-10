package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 注册请求DTO — 前端 → 后端
 * POST /api/register 的请求体
 * 前端JSON: {"employee_id":"A010","name":"张三","password":"123","confirm_password":"123"}
 * @JsonProperty 处理蛇形命名 employee_id ↔ employeeId, confirm_password ↔ confirmPassword
 * controller中用多层if校验字段是否为空、两次密码是否一致
 */
public class RegisterRequest {
   private String name;                             // 员工姓名

   @JsonProperty("employee_id")
   private String employeeId;                       // 员工工号

   private String password;                         // 登录密码

   @JsonProperty("confirm_password")
   private String confirmPassword;                  // 确认密码（不存库，仅用于校验两次输入一致）

   public String getName() { return this.name; }
   public String getEmployeeId() { return this.employeeId; }
   public String getPassword() { return this.password; }
   public String getConfirmPassword() { return this.confirmPassword; }

   public void setName(String name) { this.name = name; }

   @JsonProperty("employee_id")
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

   public void setPassword(String password) { this.password = password; }

   @JsonProperty("confirm_password")
   public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
