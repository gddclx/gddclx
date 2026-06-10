package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 登录响应DTO — 后端 → 前端
 * 登录成功后返回Token和用户基本信息
 * 返回JSON格式: {"token":"eyJ...", "user":{"employee_id":"A001", "name":"管理员", "role":"admin"}}
 * 注意：响应中不包含密码字段，保证安全性
 */
public class LoginResponse {
   /** JWT Token（Base64编码的简易Token，非标准JWT格式） */
   private String token;
   /** 用户基本信息（嵌套对象） */
   private UserInfo user;

   public String getToken() { return this.token; }
   public UserInfo getUser() { return this.user; }

   public void setToken(String token) { this.token = token; }
   public void setUser(UserInfo user) { this.user = user; }

   public LoginResponse() {}

   public LoginResponse(String token, UserInfo user) {
      this.token = token;
      this.user = user;
   }

   /**
    * 用户信息内部类 — 嵌套在登录响应中的用户数据
    * 只包含必要字段（employeeId, name, role），不含密码
    * 前端将此对象存入 localStorage.userInfo
    */
   public static class UserInfo {
      @JsonProperty("employee_id")  // 返回JSON时序列化为 employee_id
      private String employeeId;    // 员工工号
      private String name;          // 员工姓名
      private String role;          // 角色权限：admin / employee

      public String getEmployeeId() { return this.employeeId; }
      public String getName() { return this.name; }
      public String getRole() { return this.role; }

      @JsonProperty("employee_id")
      public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
      public void setName(String name) { this.name = name; }
      public void setRole(String role) { this.role = role; }
   }
}
