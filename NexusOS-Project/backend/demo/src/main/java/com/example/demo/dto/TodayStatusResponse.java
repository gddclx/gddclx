package com.example.demo.dto;

public class TodayStatusResponse {
   private Boolean isChecked;
   private String checkinTime;
   private String checkoutTime;
   private String time;

   public Boolean getIsChecked() { return this.isChecked; }
   public String getCheckinTime() { return this.checkinTime; }
   public String getCheckoutTime() { return this.checkoutTime; }
   public String getTime() { return this.time; }

   public void setIsChecked(Boolean isChecked) { this.isChecked = isChecked; }
   public void setCheckinTime(String checkinTime) { this.checkinTime = checkinTime; }
   public void setCheckoutTime(String checkoutTime) { this.checkoutTime = checkoutTime; }
   public void setTime(String time) { this.time = time; }

   public TodayStatusResponse() {}

   public TodayStatusResponse(Boolean isChecked, String checkinTime, String checkoutTime) {
      this.isChecked = isChecked;
      this.checkinTime = checkinTime;
      this.checkoutTime = checkoutTime;
      this.time = checkoutTime;
   }
}
