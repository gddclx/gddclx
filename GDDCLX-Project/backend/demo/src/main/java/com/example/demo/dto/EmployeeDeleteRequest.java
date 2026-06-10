package com.example.demo.dto;

/**
 * 员工删除请求DTO — 前端 → 后端
 * POST /api/employee/delete 的请求体
 * 注意：使用POST而非DELETE方法（不够RESTful，但前端表单默认只支持GET/POST）
 */
public class EmployeeDeleteRequest {
   private String employeeId;  // 要删除的员工工号

   public String getEmployeeId() { return this.employeeId; }
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}
