package com.example.demo.dto;

import java.util.List;

public class PositionUpdateRequest {
   private String employeeId;
   private String department;
   private List<String> positions;

   public String getEmployeeId() {
      return this.employeeId;
   }

   public String getDepartment() {
      return this.department;
   }

   public List<String> getPositions() {
      return this.positions;
   }

   public void setEmployeeId(String employeeId) {
      this.employeeId = employeeId;
   }

   public void setDepartment(String department) {
      this.department = department;
   }

   public void setPositions(List<String> positions) {
      this.positions = positions;
   }

}
