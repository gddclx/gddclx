package com.example.demo.dto;

public class CheckinResponse {
   private boolean success;
   private String message;
   private String checkinTime;

   public boolean isSuccess() {
      return this.success;
   }

   public String getMessage() {
      return this.message;
   }

   public String getCheckinTime() {
      return this.checkinTime;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public void setCheckinTime(String checkinTime) {
      this.checkinTime = checkinTime;
   }


   public CheckinResponse() {
   }

   public CheckinResponse(boolean success, String message, String checkinTime) {
      this.success = success;
      this.message = message;
      this.checkinTime = checkinTime;
   }
}
