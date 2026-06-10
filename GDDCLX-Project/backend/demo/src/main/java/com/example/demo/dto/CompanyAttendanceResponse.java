package com.example.demo.dto;

/**
 * 全公司考勤统计响应DTO — 后端 → 前端
 * GET /api/attendance/today/company 的返回值
 * 用于管理员看板展示今日整体出勤情况
 */
public class CompanyAttendanceResponse {
   /** 今日已打卡人数 */
   private Integer checkedCount;
   /** 公司在职员工总人数 */
   private Integer totalCount;
   /** 出勤率 = checkedCount / totalCount * 100（取整） */
   private Integer attendanceRate;

   public Integer getCheckedCount() { return this.checkedCount; }
   public Integer getTotalCount() { return this.totalCount; }
   public Integer getAttendanceRate() { return this.attendanceRate; }

   public void setCheckedCount(Integer checkedCount) { this.checkedCount = checkedCount; }
   public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
   public void setAttendanceRate(Integer attendanceRate) { this.attendanceRate = attendanceRate; }

   public CompanyAttendanceResponse() {}

   public CompanyAttendanceResponse(Integer checkedCount, Integer totalCount, Integer attendanceRate) {
      this.checkedCount = checkedCount;
      this.totalCount = totalCount;
      this.attendanceRate = attendanceRate;
   }
}
