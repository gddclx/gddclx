package com.example.demo.dto;

import java.util.List;
import java.util.Map;

public class WeekAttendanceResponse {
   private List<Map<String, Object>> weekList;
   private Integer continueDays;
   private String level;

   public List<Map<String, Object>> getWeekList() {
      return this.weekList;
   }

   public Integer getContinueDays() {
      return this.continueDays;
   }

   public String getLevel() {
      return this.level;
   }

   public void setWeekList(List<Map<String, Object>> weekList) {
      this.weekList = weekList;
   }

   public void setContinueDays(Integer continueDays) {
      this.continueDays = continueDays;
   }

   public void setLevel(String level) {
      this.level = level;
   }


   public WeekAttendanceResponse() {
   }

   public WeekAttendanceResponse(List<Map<String, Object>> weekList, Integer continueDays, String level) {
      this.weekList = weekList;
      this.continueDays = continueDays;
      this.level = level;
   }
}
