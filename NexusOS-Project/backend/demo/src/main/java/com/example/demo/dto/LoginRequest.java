package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
   @JsonProperty("employee_id")
   private String employeeId;
   private String password;

   public String getEmployeeId() {
      return this.employeeId;
   }

   public String getPassword() {
      return this.password;
   }

   @JsonProperty("employee_id")
   public void setEmployeeId(String employeeId) {
      this.employeeId = employeeId;
   }

   public void setPassword(String password) {
      this.password = password;
   }

}
