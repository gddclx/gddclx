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

/**
 * 考勤服务实现类 — 最复杂的Service之一
 * 提供签到签退、迟到/早退自动判定、周/月统计
 * 依赖：AttendanceMapper
 *
 * 核心规则：
 * - 每人每天只有一条考勤记录（通过 employee_id + checkin_date 查询防重复）
 * - 签到时间 > 9:00 → 迟到（>= 9:01）
 * - 签退时间 < 17:00 → 早退（<= 16:59）
 * - 工作日出勤天数计算：排除周六日
 *
 * 槽点：使用旧版 java.util.Date + Calendar，而非 java.time.LocalDateTime
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {
   @Autowired
   private AttendanceMapper attendanceMapper;
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   /**
    * 签到 — 每日每人仅限一次
    * 步骤：查今日是否已有记录 → 有则返回"已打卡" → 无则INSERT
    */
   public CheckinResponse checkin(String userId) {
      Date today = new Date();
      String todayStr = DATE_FORMAT.format(today);  // "2026-06-08"
      // 查询今日是否已打卡（employee_id + checkin_date联合查询）
      Attendance exist = this.attendanceMapper.selectByEmployeeIdAndDateStr(userId, todayStr);
      if (exist != null) {
         return new CheckinResponse(false, "今日已打卡，请勿重复打卡", (String)null);
      } else {
         Attendance attendance = new Attendance();
         attendance.setEmployeeId(userId);
         attendance.setCheckinDate(new Date());  // 打卡日期（只有日期部分有意义）
         attendance.setCheckinTime(today);       // 签到时间
         attendance.setCreateTime(today);
         int result = this.attendanceMapper.insert(attendance);
         return result <= 0
            ? new CheckinResponse(false, "打卡失败", (String)null)
            : new CheckinResponse(true, "打卡成功", TIME_FORMAT.format(today));
      }
   }

   /**
    * 签退 — 触发迟到/早退自动判定
    * 步骤：
    * 1. 查今日是否签过到（没签→拒绝）
    * 2. 查是否已签退（已签→拒绝）
    * 3. 判定迟到：签到时间 > 9:00
    * 4. 判定早退：签退时间 < 17:00
    * 5. UPDATE：写入checkout_time + is_late + is_early
    */
   public CheckoutResponse checkout(String userId) {
      Date now = new Date();
      String todayStr = DATE_FORMAT.format(now);

      // 校验1：必须先签到
      Attendance exist = this.attendanceMapper.selectByEmployeeIdAndDateStr(userId, todayStr);
      if (exist == null) {
         return new CheckoutResponse(false, "今日未签到，无法签退", null, false, false);
      }

      // 校验2：不能重复签退
      if (exist.getCheckoutTime() != null) {
         return new CheckoutResponse(false, "今日已签退，请勿重复签退",
                 TIME_FORMAT.format(exist.getCheckoutTime()), false, false);
      }

      // 迟到判定：签到时间 > 9:00（时>9 或 时=9且分>0）
      Calendar cal = Calendar.getInstance();
      cal.setTime(exist.getCheckinTime());
      int checkinHour = cal.get(Calendar.HOUR_OF_DAY);
      int checkinMinute = cal.get(Calendar.MINUTE);
      boolean isLate = (checkinHour > 9) || (checkinHour == 9 && checkinMinute > 0);

      // 早退判定：签退时间 < 17:00
      cal.setTime(now);
      int checkoutHour = cal.get(Calendar.HOUR_OF_DAY);
      boolean isEarly = (checkoutHour < 17);

      int lateVal = isLate ? 1 : 0;
      int earlyVal = isEarly ? 1 : 0;

      // 更新数据库
      this.attendanceMapper.updateCheckout(userId, lateVal, earlyVal);

      // 拼装状态描述
      String status = "";
      if (isLate && isEarly) status = " (迟到+早退)";
      else if (isLate) status = " (迟到)";
      else if (isEarly) status = " (早退)";

      return new CheckoutResponse(true, "签退成功" + status, TIME_FORMAT.format(now), isLate, isEarly);
   }

   /** 全公司今日考勤概况 */
   public CompanyAttendanceResponse getCompanyTodayAttendance() {
      String todayStr = DATE_FORMAT.format(new Date());
      int checkedCount = this.attendanceMapper.countTodayCheckinStr(todayStr);
      int totalCount = this.attendanceMapper.countEmployeeTotal();
      int attendanceRate = totalCount > 0 ? (int)Math.round((double)checkedCount / (double)totalCount * 100.0) : 0;
      return new CompanyAttendanceResponse(checkedCount, totalCount, attendanceRate);
   }

   /** 某人今日打卡状态 */
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

   /**
    * 本周考勤统计
    * 返回：7天数组（每天是否打卡）、连续打卡天数、等级（优秀/良好/一般）
    * 连续天数从今天往前推算，遇到中断就停
    */
   public WeekAttendanceResponse getWeekAttendance(String userId) {
      List<Map<String, Object>> weekList = new ArrayList();
      Calendar calendar = Calendar.getInstance();

      // 计算本周一日期
      Date today = this.truncateDate(new Date());
      calendar.setTime(today);
      calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
      Date monday = this.truncateDate(calendar.getTime());

      // 计算本周日日期
      calendar.setTime(monday);
      calendar.add(Calendar.DATE, 6);
      Date sunday = this.truncateDate(calendar.getTime());

      // 查询本周考勤记录
      List<Attendance> attendances = this.attendanceMapper.selectByEmployeeIdAndDateRange(userId, monday, sunday);
      Set<String> checkinDates = new HashSet();
      for (Attendance a : attendances) {
         checkinDates.add(DATE_FORMAT.format(a.getCheckinDate()));
      }

      // 构建7天打卡状态数组
      calendar.setTime(monday);
      for (int i = 0; i < 7; i++) {
         Date date = calendar.getTime();
         String dateStr = DATE_FORMAT.format(date);
         Map<String, Object> dayData = new HashMap();
         dayData.put("date", dateStr);
         dayData.put("checked", checkinDates.contains(dateStr));  // 是否打卡
         weekList.add(dayData);
         calendar.add(Calendar.DATE, 1);
      }

      // 计算连续打卡天数（从今天往前数）
      int continueDays = 0;
      calendar.setTime(today);
      for (int i = 0; i < 7; i++) {
         String dateStr = DATE_FORMAT.format(calendar.getTime());
         if (!checkinDates.contains(dateStr)) break;  // 遇到中断就停
         continueDays++;
         calendar.add(Calendar.DATE, -1);  // 往前一天
      }

      // 等级评定
      String level;
      if (continueDays >= 5) level = "优秀";
      else if (continueDays >= 3) level = "良好";
      else level = "一般";

      return new WeekAttendanceResponse(weekList, continueDays, level);
   }

   /**
    * 本月考勤统计
    * 应出勤天数：本月工作日（排除周六日）
    * 出勤率 = 实际打卡天数 / 应出勤天数 × 100
    */
   public MonthAttendanceResponse getMonthAttendance(String userId) {
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);

      int shouldWorkDays = this.calculateWorkDays(year, month);  // 月工作日数

      calendar.set(Calendar.DATE, 1);
      Date monthStart = this.truncateDate(calendar.getTime());
      calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
      Date monthEnd = this.truncateDate(calendar.getTime());

      List<Attendance> attendances = this.attendanceMapper.selectByEmployeeIdAndDateRange(userId, monthStart, monthEnd);
      int checkedDays = attendances.size();

      double attendanceRate = shouldWorkDays > 0 ? (double)checkedDays / (double)shouldWorkDays * 100.0 : 0.0;
      String description = String.format("本月应出勤%d天，已打卡%d天", shouldWorkDays, checkedDays);
      return new MonthAttendanceResponse(shouldWorkDays, checkedDays, attendanceRate, description);
   }

   /** 日期截断：清零时分秒毫秒 */
   private Date truncateDate(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return cal.getTime();
   }

   /** 计算指定年月的工作日天数（周一到周五） */
   private int calculateWorkDays(int year, int month) {
      int workDays = 0;
      Calendar calendar = Calendar.getInstance();
      calendar.set(year, month, 1);
      int daysInMonth = calendar.getActualMaximum(Calendar.DATE);

      for (int day = 1; day <= daysInMonth; day++) {
         calendar.set(Calendar.DATE, day);
         int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
         if (dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY) {
            workDays++;
         }
      }
      return workDays;
   }
}
