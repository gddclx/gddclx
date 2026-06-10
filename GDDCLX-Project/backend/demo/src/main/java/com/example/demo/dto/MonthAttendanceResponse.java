package com.example.demo.dto;

/**
 * 月考勤统计响应DTO — 后端 → 前端
 * GET /api/attendance/month/{userId} 的返回值
 * 本月已打卡天数 vs 应出勤工作日天数的对比
 */
public class MonthAttendanceResponse {
   /** 本月应出勤工作日天数（排除周六日） */
   private Integer shouldWorkDays;
   /** 本月实际打卡天数 */
   private Integer checkedDays;
   /** 出勤率 = checkedDays / shouldWorkDays * 100 */
   private Double attendanceRate;
   /** 文字描述，如 "本月应出勤22天，已打卡20天" */
   private String description;

   public Integer getShouldWorkDays() { return this.shouldWorkDays; }
   public Integer getCheckedDays() { return this.checkedDays; }
   public Double getAttendanceRate() { return this.attendanceRate; }
   public String getDescription() { return this.description; }

   public void setShouldWorkDays(Integer shouldWorkDays) { this.shouldWorkDays = shouldWorkDays; }
   public void setCheckedDays(Integer checkedDays) { this.checkedDays = checkedDays; }
   public void setAttendanceRate(Double attendanceRate) { this.attendanceRate = attendanceRate; }
   public void setDescription(String description) { this.description = description; }

   public MonthAttendanceResponse() {}

   public MonthAttendanceResponse(Integer shouldWorkDays, Integer checkedDays, Double attendanceRate, String description) {
      this.shouldWorkDays = shouldWorkDays;
      this.checkedDays = checkedDays;
      this.attendanceRate = attendanceRate;
      this.description = description;
   }
}
