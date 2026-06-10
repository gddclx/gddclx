package com.example.demo.dto;

/**
 * 签到响应DTO — 后端 → 前端
 * 包含签到是否成功以及具体签到时间
 */
public class CheckinResponse {
   private boolean success;     // 是否签到成功
   private String message;      // 提示信息（如 "打卡成功" 或 "今日已打卡，请勿重复打卡"）
   private String checkinTime;  // 签到时间字符串（如 "2026-06-08 08:55:30"）

   public boolean isSuccess() { return this.success; }
   public String getMessage() { return this.message; }
   public String getCheckinTime() { return this.checkinTime; }

   public void setSuccess(boolean success) { this.success = success; }
   public void setMessage(String message) { this.message = message; }
   public void setCheckinTime(String checkinTime) { this.checkinTime = checkinTime; }

   /** 无参构造（Jackson反序列化需要） */
   public CheckinResponse() {}

   /** 全参构造（Service层直接构建响应对象） */
   public CheckinResponse(boolean success, String message, String checkinTime) {
      this.success = success;
      this.message = message;
      this.checkinTime = checkinTime;
   }
}
