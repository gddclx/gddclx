package com.example.demo.dto;

import java.util.List;

/**
 * 岗位列表响应DTO — 后端 → 前端
 * PositionListPageResponse 的 list 数组中的单条记录
 * 展示每个员工的兼岗情况
 */
public class PositionListResponse {
   private String employeeId;       // 员工工号
   private String name;             // 员工姓名
   private String department;       // 所属部门
   private List<String> positions;  // 岗位名称列表（如 ["前端开发","后端开发"]）

   public String getEmployeeId() { return this.employeeId; }
   public String getName() { return this.name; }
   public String getDepartment() { return this.department; }
   public List<String> getPositions() { return this.positions; }

   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setName(String name) { this.name = name; }
   public void setDepartment(String department) { this.department = department; }
   public void setPositions(List<String> positions) { this.positions = positions; }

   public PositionListResponse() {}

   public PositionListResponse(String employeeId, String name, String department, List<String> positions) {
      this.employeeId = employeeId;
      this.name = name;
      this.department = department;
      this.positions = positions;
   }
}
