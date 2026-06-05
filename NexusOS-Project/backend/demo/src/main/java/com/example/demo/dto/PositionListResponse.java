package com.example.demo.dto;

import java.util.List;

public class PositionListResponse {
   private String employeeId;
   private String name;
   private String department;
   private List<String> positions;

   public String getEmployeeId() {
      return this.employeeId;
   }

   public String getName() {
      return this.name;
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

   public void setName(String name) {
      this.name = name;
   }

   public void setDepartment(String department) {
      this.department = department;
   }

   public void setPositions(List<String> positions) {
      this.positions = positions;
   }


   public PositionListResponse() {
   }

   public PositionListResponse(String employeeId, String name, String department, List<String> positions) {
      this.employeeId = employeeId;
      this.name = name;
      this.department = department;
      this.positions = positions;
   }
}
