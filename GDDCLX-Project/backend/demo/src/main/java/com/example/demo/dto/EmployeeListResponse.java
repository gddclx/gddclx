package com.example.demo.dto;

/**
 * 员工列表响应DTO — 后端 → 前端
 * GET /api/employee/list 的返回值中的单条记录
 * 用于管理员查看的全员列表，不包含密码和薪资等敏感字段
 */
public class EmployeeListResponse {
   private String employeeId;  // 员工工号
   private String name;        // 员工姓名
   private String department;  // 所属部门
   private String position;    // 当前显示岗位
   private String hireDate;    // 入职日期（从 createTime 字段转换）
   private String status;      // 状态：在职/试用/离职

   public String getEmployeeId() { return this.employeeId; }
   public String getName() { return this.name; }
   public String getDepartment() { return this.department; }
   public String getPosition() { return this.position; }
   public String getHireDate() { return this.hireDate; }
   public String getStatus() { return this.status; }

   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setName(String name) { this.name = name; }
   public void setDepartment(String department) { this.department = department; }
   public void setPosition(String position) { this.position = position; }
   public void setHireDate(String hireDate) { this.hireDate = hireDate; }
   public void setStatus(String status) { this.status = status; }

   public EmployeeListResponse() {}

   public EmployeeListResponse(String employeeId, String name, String department, String position, String hireDate, String status) {
      this.employeeId = employeeId;
      this.name = name;
      this.department = department;
      this.position = position;
      this.hireDate = hireDate;
      this.status = status;
   }
}
