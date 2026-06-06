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

@Service
public class PerformanceServiceImpl implements PerformanceService {
   @Autowired
   private EmployeeMapper employeeMapper;
   @Autowired
   private AttendanceMapper attendanceMapper;
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   public List<PerformanceResponse> getAllPerformance() {
      List<Employee> employees = this.employeeMapper.selectAll();
      if (employees != null && !employees.isEmpty()) {
         List<PerformanceResponse> result = new ArrayList();
         Iterator var3 = employees.iterator();

         while(var3.hasNext()) {
            Employee emp = (Employee)var3.next();
            PerformanceResponse response = this.buildPerformanceResponse(emp);
            result.add(response);
         }

         return result;
      } else {
         return Collections.emptyList();
      }
   }

   private PerformanceResponse buildPerformanceResponse(Employee emp) {
      String employeeId = emp.getEmployeeId();
      List<Map<String, String>> positionList = this.parsePositions(emp.getPositions());
      int positionCount = positionList.size();
      String positionName;
      String departmentName;
      if (positionCount == 0) {
         positionName = "无";
         departmentName = emp.getDepartment() != null ? emp.getDepartment() : "无";
      } else {
         positionName = (String)positionList.stream().map((p) -> {
            return (String)p.get("position");
         }).filter((p) -> {
            return p != null && !p.isEmpty();
         }).collect(Collectors.joining("、"));
         departmentName = emp.getDepartment() != null ? emp.getDepartment() : "";
      }

      double baseSalary = this.calculateBaseSalary(employeeId);
      double attendanceRate = this.calculateAttendanceRate(employeeId, emp.getHireDate());
      BigDecimal performanceScore = BigDecimal.valueOf(attendanceRate).setScale(2, RoundingMode.HALF_UP);
      BigDecimal commissionAmount;
      if (positionCount != 0 && attendanceRate != 0.0) {
         commissionAmount = BigDecimal.valueOf(baseSalary).multiply(BigDecimal.valueOf((long)positionCount)).multiply(BigDecimal.valueOf(0.5)).multiply(BigDecimal.valueOf(attendanceRate / 100.0)).setScale(2, RoundingMode.HALF_UP);
      } else {
         commissionAmount = BigDecimal.ZERO;
      }

      // Late / early / deduction stats
      int lateCount = 0;
      int earlyCount = 0;
      int salaryDeduct = 0;
      Map<String, Object> stats = this.attendanceMapper.countTotalLateEarlyByEmployeeId(employeeId);
      if (stats != null) {
         if (stats.get("totalLate") != null) lateCount = ((Number) stats.get("totalLate")).intValue();
         if (stats.get("totalEarly") != null) earlyCount = ((Number) stats.get("totalEarly")).intValue();
         if (stats.get("totalDeduct") != null) salaryDeduct = ((Number) stats.get("totalDeduct")).intValue();
      }

      return new PerformanceResponse(emp.getName(), employeeId, positionName, departmentName,
              performanceScore, commissionAmount, lateCount, earlyCount, salaryDeduct);
   }

   @Transactional
   public int confirmLate(String employeeId) {
      return this.attendanceMapper.confirmLate(employeeId);
   }

   @Transactional
   public int confirmEarly(String employeeId) {
      return this.attendanceMapper.confirmEarly(employeeId);
   }

   @Transactional
   public int forgiveLate(String employeeId) {
      return this.attendanceMapper.forgiveLate(employeeId);
   }

   @Transactional
   public int forgiveEarly(String employeeId) {
      return this.attendanceMapper.forgiveEarly(employeeId);
   }

   @Transactional
   public void autoMarkEarlyLeave() {
      this.attendanceMapper.autoMarkEarlyLeave();
   }

   private double calculateAttendanceRate(String employeeId, Date hireDate) {
      Calendar cal = Calendar.getInstance();
      int year = cal.get(1);
      int month = cal.get(2);
      cal.set(year, month, 1, 0, 0, 0);
      cal.set(14, 0);
      Date monthStart = cal.getTime();
      Date effectiveStart = monthStart;
      if (hireDate != null && hireDate.after(monthStart)) {
         effectiveStart = this.truncateDate(hireDate);
      }

      Date today = this.truncateDate(new Date());
      int shouldWorkDays = this.calculateWorkDaysBetween(effectiveStart, today);
      if (shouldWorkDays == 0) {
         return 0.0;
      } else {
         int checkedDays = this.attendanceMapper.countByEmployeeIdAndDateRange(employeeId, effectiveStart, today);
         return (double)checkedDays / (double)shouldWorkDays * 100.0;
      }
   }

   private int calculateWorkDaysBetween(Date startInclusive, Date endInclusive) {
      int workDays = 0;
      Calendar cal = Calendar.getInstance();
      cal.setTime(startInclusive);
      Calendar endCal = Calendar.getInstance();
      endCal.setTime(endInclusive);

      for(; !cal.after(endCal); cal.add(5, 1)) {
         int dayOfWeek = cal.get(7);
         if (dayOfWeek != 7 && dayOfWeek != 1) {
            ++workDays;
         }
      }

      return workDays;
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

   private double calculateBaseSalary(String employeeId) {
      if (employeeId != null && employeeId.length() >= 3) {
         String lastThree = employeeId.substring(employeeId.length() - 3);

         int suffix;
         try {
            suffix = Integer.parseInt(lastThree);
         } catch (NumberFormatException var5) {
            return 5000.0;
         }

         if (suffix < 200) {
            return 8000.0;
         } else {
            return suffix <= 300 ? 6000.0 : 5000.0;
         }
      } else {
         return 5000.0;
      }
   }

   private List<Map<String, String>> parsePositions(String positionsJson) {
      if (positionsJson != null && !positionsJson.trim().isEmpty()) {
         try {
            return (List)OBJECT_MAPPER.readValue(positionsJson, new TypeReference<List<Map<String, String>>>() {
            });
         } catch (Exception var3) {
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }
}
