package com.example.demo.service.impl;

import com.example.demo.domain.Employee;
import com.example.demo.dto.PerformanceResponse;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.PerformanceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 绩效服务实现类
 * 提供全员绩效计算、迟到/早退确认与赦免、定时自动标记早退
 * 依赖：EmployeeMapper（员工数据）+ AttendanceMapper（考勤数据）
 *
 * 核心公式：
 * 基本工资 = 按工号后三位分档：<200→8000, 200~300→6000, >300→5000
 * 提成 = 基本工资 × 岗位数量 × 0.5 × (出勤率/100)
 * 绩效分 = 出勤率
 * 扣款 = 迟到次数 × 50 + 早退次数 × 50
 *
 * 使用 BigDecimal 确保金额计算精度（避免double浮点误差）
 */
@Service
public class PerformanceServiceImpl implements PerformanceService {
   @Autowired
   private EmployeeMapper employeeMapper;
   @Autowired
   private AttendanceMapper attendanceMapper;
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();  // Jackson JSON解析器

   /**
    * 全员绩效列表 — 遍历所有员工，逐个计算绩效数据
    */
   public List<PerformanceResponse> getAllPerformance() {
      List<Employee> employees = this.employeeMapper.selectAll();
      if (employees != null && !employees.isEmpty()) {
         List<PerformanceResponse> result = new ArrayList();
         Iterator var3 = employees.iterator();
         while(var3.hasNext()) {
            Employee emp = (Employee)var3.next();
            PerformanceResponse response = this.buildPerformanceResponse(emp);  // 逐个计算
            result.add(response);
         }
         return result;
      } else {
         return Collections.emptyList();
      }
   }

   /**
    * 构建单个员工的绩效数据 — 核心计算逻辑
    * 包含：岗位解析、基本工资计算、出勤率计算、提成计算、扣款统计
    */
   private PerformanceResponse buildPerformanceResponse(Employee emp) {
      String employeeId = emp.getEmployeeId();

      // 第1步：解析JSON岗位列表
      List<Map<String, String>> positionList = this.parsePositions(emp.getPositions());  // [{"position":"前端"}]
      int positionCount = positionList.size();

      // 第2步：拼接岗位名（多岗位用"、"连接）
      String positionName;
      String departmentName;
      if (positionCount == 0) {
         positionName = "无";
         departmentName = emp.getDepartment() != null ? emp.getDepartment() : "无";
      } else {
         positionName = positionList.stream().map(p -> p.get("position"))
            .filter(p -> p != null && !p.isEmpty()).collect(Collectors.joining("、"));
         departmentName = emp.getDepartment() != null ? emp.getDepartment() : "";
      }

      // 第3步：计算基本工资（按工号后三位分档）
      double baseSalary = this.calculateBaseSalary(employeeId);

      // 第4步：计算出勤率（实际打卡天数 ÷ 应出勤工作日天数 × 100）
      double attendanceRate = this.calculateAttendanceRate(employeeId, emp.getHireDate());

      // 第5步：绩效得分 = 出勤率（保留2位小数）
      BigDecimal performanceScore = BigDecimal.valueOf(attendanceRate).setScale(2, RoundingMode.HALF_UP);

      // 第6步：计算提成金额
      // 公式：基本工资 × 岗位数量 × 0.5 × (出勤率/100)
      BigDecimal commissionAmount;
      if (positionCount != 0 && attendanceRate != 0.0) {
         commissionAmount = BigDecimal.valueOf(baseSalary)
            .multiply(BigDecimal.valueOf((long)positionCount))     // × 岗位数
            .multiply(BigDecimal.valueOf(0.5))                      // × 提成系数0.5
            .multiply(BigDecimal.valueOf(attendanceRate / 100.0))    // × 出勤率
            .setScale(2, RoundingMode.HALF_UP);                     // 保留2位小数
      } else {
         commissionAmount = BigDecimal.ZERO;
      }

      // 第7步：统计迟到/早退/扣款数据
      int lateCount = 0, earlyCount = 0, salaryDeduct = 0;
      Map<String, Object> stats = this.attendanceMapper.countTotalLateEarlyByEmployeeId(employeeId);
      if (stats != null) {
         if (stats.get("totalLate") != null) lateCount = ((Number) stats.get("totalLate")).intValue();
         if (stats.get("totalEarly") != null) earlyCount = ((Number) stats.get("totalEarly")).intValue();
         if (stats.get("totalDeduct") != null) salaryDeduct = ((Number) stats.get("totalDeduct")).intValue();
      }

      return new PerformanceResponse(emp.getName(), employeeId, positionName, departmentName,
              performanceScore, commissionAmount, lateCount, earlyCount, salaryDeduct);
   }

   /** 确认迟到扣款 — 单条UPDATE，@Transactional手动标记 */
   @Transactional
   public int confirmLate(String employeeId) {
      return this.attendanceMapper.confirmLate(employeeId);  // UPDATE SET late_confirmed=1, salary_deduct+=50
   }

   /** 确认早退扣款 */
   @Transactional
   public int confirmEarly(String employeeId) {
      return this.attendanceMapper.confirmEarly(employeeId);
   }

   /** 赦免迟到 — salary_deduct -= 50（需满足 >=50 防负数） */
   @Transactional
   public int forgiveLate(String employeeId) {
      return this.attendanceMapper.forgiveLate(employeeId);  // UPDATE SET late_confirmed=0, salary_deduct-=50 WHERE salary_deduct>=50
   }

   /** 赦免早退 */
   @Transactional
   public int forgiveEarly(String employeeId) {
      return this.attendanceMapper.forgiveEarly(employeeId);
   }

   /** 定时任务 — 每天凌晨1:00自动标记昨天未签退者 */
   @Transactional
   public void autoMarkEarlyLeave() {
      this.attendanceMapper.autoMarkEarlyLeave();
   }

   /**
    * 计算员工的出勤率
    * 出勤率 = 实际打卡天数 / 应出勤工作日天数 × 100
    * 应出勤天数从入职日期（如果本月入职）或本月1号开始算到今天
    */
   private double calculateAttendanceRate(String employeeId, Date hireDate) {
      Calendar cal = Calendar.getInstance();
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      cal.set(year, month, 1, 0, 0, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date monthStart = cal.getTime();

      // 如果本月才入职，从入职日期开始算
      Date effectiveStart = monthStart;
      if (hireDate != null && hireDate.after(monthStart)) {
         effectiveStart = this.truncateDate(hireDate);
      }

      Date today = this.truncateDate(new Date());
      int shouldWorkDays = this.calculateWorkDaysBetween(effectiveStart, today);  // 排除周末
      if (shouldWorkDays == 0) return 0.0;

      int checkedDays = this.attendanceMapper.countByEmployeeIdAndDateRange(employeeId, effectiveStart, today);
      return (double)checkedDays / (double)shouldWorkDays * 100.0;
   }

   /** 计算两个日期之间的工作日天数（排除周六日） */
   private int calculateWorkDaysBetween(Date startInclusive, Date endInclusive) {
      int workDays = 0;
      Calendar cal = Calendar.getInstance();
      cal.setTime(startInclusive);
      Calendar endCal = Calendar.getInstance();
      endCal.setTime(endInclusive);

      for (; !cal.after(endCal); cal.add(Calendar.DATE, 1)) {
         int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
         if (dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY) {
            workDays++;  // 周一到周五才算工作日
         }
      }
      return workDays;
   }

   /** 日期截断：清零时分秒毫秒，只保留日期部分 */
   private Date truncateDate(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return cal.getTime();
   }

   /**
    * 按工号后三位确定基本工资
    * < 200 → 8000元（如 A001~A199）
    * 200~300 → 6000元（如 A200~A300）
    * > 300 → 5000元（如 A301+）
    */
   private double calculateBaseSalary(String employeeId) {
      if (employeeId != null && employeeId.length() >= 3) {
         String lastThree = employeeId.substring(employeeId.length() - 3);
         try {
            int suffix = Integer.parseInt(lastThree);
            if (suffix < 200) return 8000.0;
            else return suffix <= 300 ? 6000.0 : 5000.0;
         } catch (NumberFormatException var5) {
            return 5000.0;  // 异常默认5000
         }
      } else {
         return 5000.0;
      }
   }

   /**
    * 解析员工positions JSON列
    * "[{\"position\":\"前端开发\",\"department\":\"技术研发\"}]" → List<Map>
    */
   private List<Map<String, String>> parsePositions(String positionsJson) {
      if (positionsJson != null && !positionsJson.trim().isEmpty()) {
         try {
            return OBJECT_MAPPER.readValue(positionsJson, new TypeReference<List<Map<String, String>>>() {});
         } catch (Exception var3) {
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }
}
