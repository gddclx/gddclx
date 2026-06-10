package com.example.demo.dto;

/**
 * 员工创建请求DTO — 前端 → 后端
 * POST /api/employee/create 的请求体
 * 管理员填写员工信息后提交，密码使用系统默认密码（Changeme_123）
 */
public class EmployeeCreateRequest {
   private String name;        // 员工姓名
   private String employeeId;  // 员工工号
   private String department;  // 所属部门
   private String position;    // 岗位名称
   private String status;      // 状态：在职/试用/离职

   public String getName() { return this.name; }
   public String getEmployeeId() { return this.employeeId; }
   public String getDepartment() { return this.department; }
   public String getPosition() { return this.position; }
   public String getStatus() { return this.status; }

   public void setName(String name) { this.name = name; }
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setDepartment(String department) { this.department = department; }
   public void setPosition(String position) { this.position = position; }
   public void setStatus(String status) { this.status = status; }
}
