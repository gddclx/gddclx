package com.example.demo.dto;

/**
 * 今日打卡状态响应DTO — 后端 → 前端
 * GET /api/attendance/today/{userId} 的返回值
 * 用于前端显示"今日是否已打卡"以及签到/签退时间
 */
public class TodayStatusResponse {
   private Boolean isChecked;     // 今日是否已签到
   private String checkinTime;    // 签到时间字符串（未签到时为null）
   private String checkoutTime;   // 签退时间字符串（未签退时为null）
   private String time;           // 时间（签退时间同checkoutTime，构造时赋值）

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
      this.time = checkoutTime;  // 向后兼容字段
   }
}
