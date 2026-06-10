package com.example.demo.dto;

import java.util.List;

/**
 * 岗位更新请求DTO — 前端 → 后端
 * POST /api/position/update 的请求体
 * 前端JSON: {"employeeId":"A001", "department":"技术研发", "positions":["前端开发","后端开发"]}
 * positions 数组会被序列化为JSON字符串存入 employee.positions 列
 */
public class PositionUpdateRequest {
   private String employeeId;       // 员工工号
   private String department;       // 更新后的部门
   private List<String> positions;  // 更新后的岗位名称列表

   public String getEmployeeId() { return this.employeeId; }
   public String getDepartment() { return this.department; }
   public List<String> getPositions() { return this.positions; }

   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setDepartment(String department) { this.department = department; }
   public void setPositions(List<String> positions) { this.positions = positions; }
}
