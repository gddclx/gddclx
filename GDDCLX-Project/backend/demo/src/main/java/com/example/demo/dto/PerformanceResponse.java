package com.example.demo.dto;

import java.math.BigDecimal;

/**
 * 绩效数据响应DTO — 后端 → 前端
 * GET /api/performance/list 的返回值中的单条记录
 * 综合计算了员工的基本工资、提成、出勤率和扣款
 * 使用 BigDecimal 保证金额计算的精度（避免 double 浮点误差）
 */
public class PerformanceResponse {
   private String name;                    // 员工姓名
   private String employeeId;              // 员工工号
   private String position;                // 岗位名称（多岗位用"、"连接）
   private String department;              // 所属部门
   private BigDecimal performanceScore;    // 绩效得分（= 出勤率，保留2位小数）
   private BigDecimal commissionAmount;    // 提成金额 = 基本工资 × 岗位数 × 0.5 × (出勤率/100)
   private int lateCount;                  // 迟到次数
   private int earlyCount;                 // 早退次数
   private int salaryDeduct;               // 累计扣款金额（迟到/早退每次50元）

   public String getName() { return this.name; }
   public String getEmployeeId() { return this.employeeId; }
   public String getPosition() { return this.position; }
   public String getDepartment() { return this.department; }
   public BigDecimal getPerformanceScore() { return this.performanceScore; }
   public BigDecimal getCommissionAmount() { return this.commissionAmount; }
   public int getLateCount() { return this.lateCount; }
   public int getEarlyCount() { return this.earlyCount; }
   public int getSalaryDeduct() { return this.salaryDeduct; }

   public void setName(String name) { this.name = name; }
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setPosition(String position) { this.position = position; }
   public void setDepartment(String department) { this.department = department; }
   public void setPerformanceScore(BigDecimal performanceScore) { this.performanceScore = performanceScore; }
   public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }
   public void setLateCount(int lateCount) { this.lateCount = lateCount; }
   public void setEarlyCount(int earlyCount) { this.earlyCount = earlyCount; }
   public void setSalaryDeduct(int salaryDeduct) { this.salaryDeduct = salaryDeduct; }

   public PerformanceResponse() {}

   public PerformanceResponse(String name, String employeeId, String position, String department,
                              BigDecimal performanceScore, BigDecimal commissionAmount,
                              int lateCount, int earlyCount, int salaryDeduct) {
      this.name = name;
      this.employeeId = employeeId;
      this.position = position;
      this.department = department;
      this.performanceScore = performanceScore;
      this.commissionAmount = commissionAmount;
      this.lateCount = lateCount;
      this.earlyCount = earlyCount;
      this.salaryDeduct = salaryDeduct;
   }
}
