package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
   private String token;
   private UserInfo user;

   public String getToken() {
      return this.token;
   }

   public UserInfo getUser() {
      return this.user;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public void setUser(UserInfo user) {
      this.user = user;
   }

   public LoginResponse() {
   }

   public LoginResponse(String token, UserInfo user) {
      this.token = token;
      this.user = user;
   }

   public static class UserInfo {
      @JsonProperty("employee_id")
      private String employeeId;
      private String name;
      private String role;

      public String getEmployeeId() {
         return this.employeeId;
      }

      public String getName() {
         return this.name;
      }

      public String getRole() {
         return this.role;
      }

      @JsonProperty("employee_id")
      public void setEmployeeId(String employeeId) {
         this.employeeId = employeeId;
      }

      public void setName(String name) {
         this.name = name;
      }

      public void setRole(String role) {
         this.role = role;
      }
   }
}
