package com.example.demo.dto;

import java.math.BigDecimal;

public class PerformanceResponse {
   private String name;
   private String employeeId;
   private String position;
   private String department;
   private BigDecimal performanceScore;
   private BigDecimal commissionAmount;
   private int lateCount;
   private int earlyCount;
   private int salaryDeduct;

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
