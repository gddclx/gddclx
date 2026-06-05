package com.example.demo.dto;

public class EmployeeListResponse {
   private String employeeId;
   private String name;
   private String department;
   private String position;
   private String hireDate;
   private String status;

   public String getEmployeeId() {
      return this.employeeId;
   }

   public String getName() {
      return this.name;
   }

   public String getDepartment() {
      return this.department;
   }

   public String getPosition() {
      return this.position;
   }

   public String getHireDate() {
      return this.hireDate;
   }

   public String getStatus() {
      return this.status;
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

   public void setPosition(String position) {
      this.position = position;
   }

   public void setHireDate(String hireDate) {
      this.hireDate = hireDate;
   }

   public void setStatus(String status) {
      this.status = status;
   }


   public EmployeeListResponse() {
   }

   public EmployeeListResponse(String employeeId, String name, String department, String position, String hireDate, String status) {
      this.employeeId = employeeId;
      this.name = name;
      this.department = department;
      this.position = position;
      this.hireDate = hireDate;
      this.status = status;
   }
}
