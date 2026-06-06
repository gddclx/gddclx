package com.example.demo.service.impl;

import com.example.demo.domain.Attendance;
import com.example.demo.dto.CheckinResponse;
import com.example.demo.dto.CheckoutResponse;
import com.example.demo.dto.CompanyAttendanceResponse;
import com.example.demo.dto.MonthAttendanceResponse;
import com.example.demo.dto.TodayStatusResponse;
import com.example.demo.dto.WeekAttendanceResponse;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.service.AttendanceService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendanceServiceImpl implements AttendanceService {
   @Autowired
   private AttendanceMapper attendanceMapper;
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public CheckinResponse checkin(String userId) {
      Date today = new Date();
      String todayStr = DATE_FORMAT.format(today);
      Attendance exist = this.attendanceMapper.selectByEmployeeIdAndDateStr(userId, todayStr);
      if (exist != null) {
         return new CheckinResponse(false, "今日已打卡，请勿重复打卡", (String)null);
      } else {
         Attendance attendance = new Attendance();
         attendance.setEmployeeId(userId);
         attendance.setCheckinDate(new Date());
         attendance.setCheckinTime(today);
         attendance.setCreateTime(today);
         int result = this.attendanceMapper.insert(attendance);
         return result <= 0 ? new CheckinResponse(false, "打卡失败", (String)null) : new CheckinResponse(true, "打卡成功", TIME_FORMAT.format(today));
      }
   }

   public CheckoutResponse checkout(String userId) {
      Date now = new Date();
      String todayStr = DATE_FORMAT.format(now);

      Attendance exist = this.attendanceMapper.selectByEmployeeIdAndDateStr(userId, todayStr);
      if (exist == null) {
         return new CheckoutResponse(false, "今日未签到，无法签退", null, false, false);
      }

      if (exist.getCheckoutTime() != null) {
         return new CheckoutResponse(false, "今日已签退，请勿重复签退",
                 TIME_FORMAT.format(exist.getCheckoutTime()), false, false);
      }

      // Judge late: checkin after 9:00
      Calendar cal = Calendar.getInstance();
      cal.setTime(exist.getCheckinTime());
      int checkinHour = cal.get(Calendar.HOUR_OF_DAY);
      int checkinMinute = cal.get(Calendar.MINUTE);
      boolean isLate = (checkinHour > 9) || (checkinHour == 9 && checkinMinute > 0);

      // Judge early: checkout before 17:00
      cal.setTime(now);
      int checkoutHour = cal.get(Calendar.HOUR_OF_DAY);
      boolean isEarly = (checkoutHour < 17);

      int lateVal = isLate ? 1 : 0;
      int earlyVal = isEarly ? 1 : 0;

      this.attendanceMapper.updateCheckout(userId, lateVal, earlyVal);

      String status = "";
      if (isLate && isEarly) status = " (迟到+早退)";
      else if (isLate) status = " (迟到)";
      else if (isEarly) status = " (早退)";

      return new CheckoutResponse(true, "签退成功" + status, TIME_FORMAT.format(now), isLate, isEarly);
   }

   public CompanyAttendanceResponse getCompanyTodayAttendance() {
      String todayStr = DATE_FORMAT.format(new Date());
      int checkedCount = this.attendanceMapper.countTodayCheckinStr(todayStr);
      int totalCount = this.attendanceMapper.countEmployeeTotal();
      int attendanceRate = totalCount > 0 ? (int)Math.round((double)checkedCount / (double)totalCount * 100.0) : 0;
      return new CompanyAttendanceResponse(checkedCount, totalCount, attendanceRate);
   }

   public TodayStatusResponse getTodayStatus(String userId) {
      String todayStr = DATE_FORMAT.format(new Date());
      Attendance exist = this.attendanceMapper.selectByEmployeeIdAndDateStr(userId, todayStr);
      if (exist != null) {
         String checkinTimeStr = TIME_FORMAT.format(exist.getCheckinTime());
         String checkoutTimeStr = exist.getCheckoutTime() != null ? TIME_FORMAT.format(exist.getCheckoutTime()) : null;
         return new TodayStatusResponse(true, checkinTimeStr, checkoutTimeStr);
      } else {
         return new TodayStatusResponse(false, (String)null, (String)null);
      }
   }

   public WeekAttendanceResponse getWeekAttendance(String userId) {
      List<Map<String, Object>> weekList = new ArrayList();
      Calendar calendar = Calendar.getInstance();
      Date today = this.truncateDate(new Date());
      calendar.setTime(today);
      calendar.set(7, 2);
      Date monday = this.truncateDate(calendar.getTime());
      calendar.setTime(monday);
      calendar.add(5, 6);
      Date sunday = this.truncateDate(calendar.getTime());
      List<Attendance> attendances = this.attendanceMapper.selectByEmployeeIdAndDateRange(userId, monday, sunday);
      Set<String> checkinDates = new HashSet();
      Iterator var9 = attendances.iterator();

      while(var9.hasNext()) {
         Attendance a = (Attendance)var9.next();
         checkinDates.add(DATE_FORMAT.format(a.getCheckinDate()));
      }

      calendar.setTime(monday);

      String dateStr;
      int continueDays;
      for(continueDays = 0; continueDays < 7; ++continueDays) {
         Date date = calendar.getTime();
         dateStr = DATE_FORMAT.format(date);
         Map<String, Object> dayData = new HashMap();
         dayData.put("date", dateStr);
         dayData.put("checked", checkinDates.contains(dateStr));
         weekList.add(dayData);
         calendar.add(5, 1);
      }

      continueDays = 0;
      calendar.setTime(today);

      for(int i = 0; i < 7; ++i) {
         dateStr = DATE_FORMAT.format(calendar.getTime());
         if (!checkinDates.contains(dateStr)) {
            break;
         }

         ++continueDays;
         calendar.add(5, -1);
      }

      String level;
      if (continueDays >= 5) {
         level = "优秀";
      } else if (continueDays >= 3) {
         level = "良好";
      } else {
         level = "一般";
      }

      return new WeekAttendanceResponse(weekList, continueDays, level);
   }

   public MonthAttendanceResponse getMonthAttendance(String userId) {
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(1);
      int month = calendar.get(2);
      int shouldWorkDays = this.calculateWorkDays(year, month);
      calendar.set(5, 1);
      Date monthStart = this.truncateDate(calendar.getTime());
      calendar.set(5, calendar.getActualMaximum(5));
      Date monthEnd = this.truncateDate(calendar.getTime());
      List<Attendance> attendances = this.attendanceMapper.selectByEmployeeIdAndDateRange(userId, monthStart, monthEnd);
      int checkedDays = attendances.size();
      double attendanceRate = shouldWorkDays > 0 ? (double)checkedDays / (double)shouldWorkDays * 100.0 : 0.0;
      String description = String.format("本月应出勤%d天，已打卡%d天", shouldWorkDays, checkedDays);
      return new MonthAttendanceResponse(shouldWorkDays, checkedDays, attendanceRate, description);
   }

   private Date truncateDate(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(11, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      return cal.getTime();
   }

   private int calculateWorkDays(int year, int month) {
      int workDays = 0;
      Calendar calendar = Calendar.getInstance();
      calendar.set(year, month, 1);
      int daysInMonth = calendar.getActualMaximum(5);

      for(int day = 1; day <= daysInMonth; ++day) {
         calendar.set(5, day);
         int dayOfWeek = calendar.get(7);
         if (dayOfWeek != 7 && dayOfWeek != 1) {
            ++workDays;
         }
      }

      return workDays;
   }
}
