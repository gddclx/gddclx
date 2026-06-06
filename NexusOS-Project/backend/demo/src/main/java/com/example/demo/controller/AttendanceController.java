package com.example.demo.controller;

import com.example.demo.dto.CheckinRequest;
import com.example.demo.dto.CheckinResponse;
import com.example.demo.dto.CheckoutRequest;
import com.example.demo.dto.CheckoutResponse;
import com.example.demo.dto.CompanyAttendanceResponse;
import com.example.demo.dto.MonthAttendanceResponse;
import com.example.demo.dto.TodayStatusResponse;
import com.example.demo.dto.WeekAttendanceResponse;
import com.example.demo.service.AttendanceService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/attendance"})
@CrossOrigin
public class AttendanceController {
   @Autowired
   private AttendanceService attendanceService;

   @PostMapping({"/checkin"})
   public Map<String, Object> checkin(@RequestBody CheckinRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         if (request.getUserId() == null || request.getUserId().isEmpty()) {
            result.put("code", 400);
            result.put("success", false);
            result.put("message", "员工ID不能为空");
            return result;
         }

         CheckinResponse response = this.attendanceService.checkin(request.getUserId());
         result.put("code", response.isSuccess() ? 200 : 400);
         result.put("success", response.isSuccess());
         result.put("message", response.getMessage());
         result.put("data", response);
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "打卡异常: " + e.getMessage());
      }

      return result;
   }

   @PostMapping({"/checkout"})
   public Map<String, Object> checkout(@RequestBody CheckoutRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         if (request.getUserId() == null || request.getUserId().isEmpty()) {
            result.put("code", 400);
            result.put("success", false);
            result.put("message", "员工ID不能为空");
            return result;
         }

         CheckoutResponse response = this.attendanceService.checkout(request.getUserId());
         result.put("code", response.isSuccess() ? 200 : 400);
         result.put("success", response.isSuccess());
         result.put("message", response.getMessage());
         result.put("data", response);
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "签退异常: " + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/today/company"})
   public Map<String, Object> getCompanyTodayAttendance() {
      Map<String, Object> result = new HashMap();

      try {
         CompanyAttendanceResponse response = this.attendanceService.getCompanyTodayAttendance();
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "获取成功");
         result.put("data", response);
      } catch (Exception var3) {
         Exception e = var3;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/today/{userId}"})
   public Map<String, Object> getTodayStatus(@PathVariable String userId) {
      Map<String, Object> result = new HashMap();

      try {
         TodayStatusResponse response = this.attendanceService.getTodayStatus(userId);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "获取成功");
         result.put("data", response);
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/week/{userId}"})
   public Map<String, Object> getWeekAttendance(@PathVariable String userId) {
      Map<String, Object> result = new HashMap();

      try {
         WeekAttendanceResponse response = this.attendanceService.getWeekAttendance(userId);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "获取成功");
         result.put("data", response);
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/month/{userId}"})
   public Map<String, Object> getMonthAttendance(@PathVariable String userId) {
      Map<String, Object> result = new HashMap();

      try {
         MonthAttendanceResponse response = this.attendanceService.getMonthAttendance(userId);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "获取成功");
         result.put("data", response);
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + e.getMessage());
      }

      return result;
   }
}
