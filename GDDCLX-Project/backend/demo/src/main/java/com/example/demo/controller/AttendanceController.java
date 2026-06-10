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

/**
 * 考勤管理 Controller
 * 提供签到、签退、考勤统计（日/周/月/全公司）
 * 6个端点，全部使用统一的 try-catch 骨架
 * 迟到判定：签到时间 > 9:00（>= 9:01算迟到）
 * 早退判定：签退时间 < 17:00（<= 16:59算早退）
 * 接口路径前缀：/api/attendance
 */
@RestController
@RequestMapping({"/api/attendance"})
@CrossOrigin
public class AttendanceController {
   @Autowired
   private AttendanceService attendanceService;  // 考勤业务逻辑

   /**
    * 签到
    * POST /api/attendance/checkin
    * @param request { "userId": "A001" }
    * @return { code, success, message, data: { checkinTime } }
    * 内部逻辑：查今日是否已打卡 → 无记录则INSERT → 有记录则返回"已打卡"
    */
   @PostMapping({"/checkin"})
   public Map<String, Object> checkin(@RequestBody CheckinRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         // 参数校验：员工ID不能为空
         if (request.getUserId() == null || request.getUserId().isEmpty()) {
            result.put("code", 400);       // 400 Bad Request
            result.put("success", false);
            result.put("message", "员工ID不能为空");
            return result;                 // 提前返回
         }

         CheckinResponse response = this.attendanceService.checkin(request.getUserId());
         result.put("code", response.isSuccess() ? 200 : 400);  // 成功200，业务失败400
         result.put("success", response.isSuccess());
         result.put("message", response.getMessage());
         result.put("data", response);
      } catch (Exception var4) {
         result.put("code", 500);          // 系统异常500
         result.put("success", false);
         result.put("message", "打卡异常: " + var4.getMessage());
      }
      return result;
   }

   /**
    * 签退 — 触发迟到/早退自动判定
    * POST /api/attendance/checkout
    * 系统自动判定：
    * - 签到时间 > 9:00 → isLate = true
    * - 签退时间 < 17:00 → isEarly = true
    */
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
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "签退异常: " + var4.getMessage());
      }
      return result;
   }

   /**
    * 全公司今日考勤概况 — 管理员看板用
    * GET /api/attendance/today/company
    * @return { checkedCount(已打卡人数), totalCount(总人数), attendanceRate(出勤率%) }
    */
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
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + var3.getMessage());
      }
      return result;
   }

   /**
    * 查询某员工今日打卡状态
    * GET /api/attendance/today/A001
    * @param userId 员工工号（@PathVariable从URL提取）
    */
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
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + var4.getMessage());
      }
      return result;
   }

   /**
    * 周考勤统计（本周一到周日）
    * GET /api/attendance/week/A001
    * 返回：7天数组 + 连续打卡天数 + 等级（优秀/良好/一般）
    */
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
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + var4.getMessage());
      }
      return result;
   }

   /**
    * 月考勤统计（本月1号到最后一天）
    * GET /api/attendance/month/A001
    * 返回：应出勤工作日天数 + 实际打卡天数 + 出勤率
    */
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
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取异常: " + var4.getMessage());
      }
      return result;
   }
}
