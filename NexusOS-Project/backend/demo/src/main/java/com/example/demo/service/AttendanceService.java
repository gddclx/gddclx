package com.example.demo.service;

import com.example.demo.dto.CheckinResponse;
import com.example.demo.dto.CompanyAttendanceResponse;
import com.example.demo.dto.MonthAttendanceResponse;
import com.example.demo.dto.TodayStatusResponse;
import com.example.demo.dto.WeekAttendanceResponse;

public interface AttendanceService {
   CheckinResponse checkin(String var1);

   CompanyAttendanceResponse getCompanyTodayAttendance();

   TodayStatusResponse getTodayStatus(String var1);

   WeekAttendanceResponse getWeekAttendance(String var1);

   MonthAttendanceResponse getMonthAttendance(String var1);
}
