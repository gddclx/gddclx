package com.example.demo.dto;

/**
 * 员工更新请求DTO — 前端 → 后端
 * POST /api/employee/update 的请求体
 * originalEmployeeId 用于定位要修改的员工（允许修改工号本身）
 * 先用 originalEmployeeId 查到员工 → 再用新 employeeId 更新
 * 如果 employeeId 变了，会先检查新工号是否已被占用
 */
public class EmployeeUpdateRequest {
   private String originalEmployeeId;  // 原始工号（用于WHERE条件查找员工）
   private String name;                // 新姓名
   private String employeeId;          // 新工号（可以和originalEmployeeId不同）
   private String department;          // 新部门
   private String position;            // 新岗位
   private String status;              // 新状态

   public String getOriginalEmployeeId() { return this.originalEmployeeId; }
   public String getName() { return this.name; }
   public String getEmployeeId() { return this.employeeId; }
   public String getDepartment() { return this.department; }
   public String getPosition() { return this.position; }
   public String getStatus() { return this.status; }

   public void setOriginalEmployeeId(String originalEmployeeId) { this.originalEmployeeId = originalEmployeeId; }
   public void setName(String name) { this.name = name; }
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setDepartment(String department) { this.department = department; }
   public void setPosition(String position) { this.position = position; }
   public void setStatus(String status) { this.status = status; }
}
