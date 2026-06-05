package com.example.demo.dto;

public class MonthAttendanceResponse {
   private Integer shouldWorkDays;
   private Integer checkedDays;
   private Double attendanceRate;
   private String description;

   public Integer getShouldWorkDays() {
      return this.shouldWorkDays;
   }

   public Integer getCheckedDays() {
      return this.checkedDays;
   }

   public Double getAttendanceRate() {
      return this.attendanceRate;
   }

   public String getDescription() {
      return this.description;
   }

   public void setShouldWorkDays(Integer shouldWorkDays) {
      this.shouldWorkDays = shouldWorkDays;
   }

   public void setCheckedDays(Integer checkedDays) {
      this.checkedDays = checkedDays;
   }

   public void setAttendanceRate(Double attendanceRate) {
      this.attendanceRate = attendanceRate;
   }

   public void setDescription(String description) {
      this.description = description;
   }


   public MonthAttendanceResponse() {
   }

   public MonthAttendanceResponse(Integer shouldWorkDays, Integer checkedDays, Double attendanceRate, String description) {
      this.shouldWorkDays = shouldWorkDays;
      this.checkedDays = checkedDays;
      this.attendanceRate = attendanceRate;
      this.description = description;
   }
}
