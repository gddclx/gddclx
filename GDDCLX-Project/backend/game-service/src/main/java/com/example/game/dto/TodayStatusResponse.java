package com.example.game.dto;

/** 今日打卡状态响应（游戏微服务版本，与demo版本相似但字段更少） */
public class TodayStatusResponse {
   private Boolean isChecked;     // 今日是否已打卡
   private String checkinTime;    // 签到时间
   private String time;           // 时间（同checkinTime）

   public Boolean getIsChecked() { return this.isChecked; }
   public String getCheckinTime() { return this.checkinTime; }
   public String getTime() { return this.time; }
   public void setIsChecked(Boolean isChecked) { this.isChecked = isChecked; }
   public void setCheckinTime(String checkinTime) { this.checkinTime = checkinTime; }
   public void setTime(String time) { this.time = time; }
   public TodayStatusResponse() {}
   public TodayStatusResponse(Boolean isChecked, String checkinTime, String time) {
      this.isChecked = isChecked; this.checkinTime = checkinTime; this.time = time;
   }
}
