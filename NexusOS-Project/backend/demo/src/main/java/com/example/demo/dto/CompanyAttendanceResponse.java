package com.example.demo.dto;

public class CompanyAttendanceResponse {
   private Integer checkedCount;
   private Integer totalCount;
   private Integer attendanceRate;

   public Integer getCheckedCount() {
      return this.checkedCount;
   }

   public Integer getTotalCount() {
      return this.totalCount;
   }

   public Integer getAttendanceRate() {
      return this.attendanceRate;
   }

   public void setCheckedCount(Integer checkedCount) {
      this.checkedCount = checkedCount;
   }

   public void setTotalCount(Integer totalCount) {
      this.totalCount = totalCount;
   }

   public void setAttendanceRate(Integer attendanceRate) {
      this.attendanceRate = attendanceRate;
   }


   public CompanyAttendanceResponse() {
   }

   public CompanyAttendanceResponse(Integer checkedCount, Integer totalCount, Integer attendanceRate) {
      this.checkedCount = checkedCount;
      this.totalCount = totalCount;
      this.attendanceRate = attendanceRate;
   }
}
