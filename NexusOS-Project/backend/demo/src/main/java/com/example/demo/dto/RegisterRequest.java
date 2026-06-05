package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRequest {
   private String name;
   @JsonProperty("employee_id")
   private String employeeId;
   private String password;
   @JsonProperty("confirm_password")
   private String confirmPassword;

   public String getName() {
      return this.name;
   }

   public String getEmployeeId() {
      return this.employeeId;
   }

   public String getPassword() {
      return this.password;
   }

   public String getConfirmPassword() {
      return this.confirmPassword;
   }

   public void setName(String name) {
      this.name = name;
   }

   @JsonProperty("employee_id")
   public void setEmployeeId(String employeeId) {
      this.employeeId = employeeId;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   @JsonProperty("confirm_password")
   public void setConfirmPassword(String confirmPassword) {
      this.confirmPassword = confirmPassword;
   }

}
