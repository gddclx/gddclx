package com.example.demo.dto;

public class EmployeeCreateRequest {
   private String name;
   private String employeeId;
   private String department;
   private String position;
   private String status;

   public String getName() {
      return this.name;
   }

   public String getEmployeeId() {
      return this.employeeId;
   }

   public String getDepartment() {
      return this.department;
   }

   public String getPosition() {
      return this.position;
   }

   public String getStatus() {
      return this.status;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setEmployeeId(String employeeId) {
      this.employeeId = employeeId;
   }

   public void setDepartment(String department) {
      this.department = department;
   }

   public void setPosition(String position) {
      this.position = position;
   }

   public void setStatus(String status) {
      this.status = status;
   }

}
