package com.example.demo.dto;

public class TodayStatusResponse {
   private Boolean isChecked;
   private String checkinTime;
   private String time;

   public Boolean getIsChecked() {
      return this.isChecked;
   }

   public String getCheckinTime() {
      return this.checkinTime;
   }

   public String getTime() {
      return this.time;
   }

   public void setIsChecked(Boolean isChecked) {
      this.isChecked = isChecked;
   }

   public void setCheckinTime(String checkinTime) {
      this.checkinTime = checkinTime;
   }

   public void setTime(String time) {
      this.time = time;
   }


   public TodayStatusResponse() {
   }

   public TodayStatusResponse(Boolean isChecked, String checkinTime, String time) {
      this.isChecked = isChecked;
      this.checkinTime = checkinTime;
      this.time = time;
   }
}
