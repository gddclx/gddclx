package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckinRequest {
   @JsonProperty("userId")
   private String userId;

   public String getUserId() {
      return this.userId;
   }

   @JsonProperty("userId")
   public void setUserId(String userId) {
      this.userId = userId;
   }

}
