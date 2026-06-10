package com.example.demo.dto;

import java.util.List;
import java.util.Map;

/**
 * 周考勤统计响应DTO — 后端 → 前端
 * GET /api/attendance/week/{userId} 的返回值
 * 包含本周7天的打卡情况、连续打卡天数和评价等级
 */
public class WeekAttendanceResponse {
   /** 本周7天数据列表，每项含 date(日期) 和 checked(是否打卡) */
   private List<Map<String, Object>> weekList;
   /** 连续打卡天数（从今天往前数） */
   private Integer continueDays;
   /**
    * 打卡等级评价
    * ≥5天 → "优秀"
    * ≥3天 → "良好"
    * 其他 → "一般"
    */
   private String level;

   public List<Map<String, Object>> getWeekList() { return this.weekList; }
   public Integer getContinueDays() { return this.continueDays; }
   public String getLevel() { return this.level; }

   public void setWeekList(List<Map<String, Object>> weekList) { this.weekList = weekList; }
   public void setContinueDays(Integer continueDays) { this.continueDays = continueDays; }
   public void setLevel(String level) { this.level = level; }

   public WeekAttendanceResponse() {}

   public WeekAttendanceResponse(List<Map<String, Object>> weekList, Integer continueDays, String level) {
      this.weekList = weekList;
      this.continueDays = continueDays;
      this.level = level;
   }
}
