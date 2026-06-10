package com.example.demo.controller;

import com.example.demo.dto.ConfirmDeductionRequest;
import com.example.demo.dto.PerformanceResponse;
import com.example.demo.service.PerformanceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 绩效管理 Controller
 * 接口路径前缀：/api/performance
 * 提供全员绩效列表 + 迟到/早退确认与赦免
 * 4个管理操作：confirm-late / confirm-early / forgive-late / forgive-early
 * 确认扣款每次50元，赦免撤销50元（带>=50防负数保护）
 * 绩效公式：提成 = 基本工资 × 岗位数 × 0.5 × (出勤率/100)
 */
@RestController
@RequestMapping({"/api/performance"})
@CrossOrigin
public class PerformanceController {
   @Autowired
   private PerformanceService performanceService;

   /**
    * 全员绩效列表
    * GET /api/performance/list
    * 返回每个员工的：基本工资、提成、绩效分、迟到次数、早退次数、累计扣款
    */
   @GetMapping({"/list"})
   public Map<String, Object> list() {
      Map<String, Object> result = new HashMap();
      try {
         List<PerformanceResponse> data = this.performanceService.getAllPerformance();
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "success");
         result.put("data", data);
      } catch (Exception var3) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取绩效列表异常: " + var3.getMessage());
         result.put("data", (Object)null);
      }
      return result;
   }

   /**
    * 确认迟到扣款 — salaryDeduct += 50
    * POST /api/performance/confirm-late { employeeId, type:"late" }
    */
   @PostMapping({"/confirm-late"})
   public Map<String, Object> confirmLate(@RequestBody ConfirmDeductionRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         int rows = this.performanceService.confirmLate(request.getEmployeeId());  // UPDATE影响行数
         if (rows > 0) {
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "迟到记录已确认，扣款50元（更新" + rows + "条）");
         } else {
            // rows==0 仍返回200（槽点：应该返回400）
            result.put("code", 200);
            result.put("success", false);
            result.put("message", "没有找到未确认的迟到记录，请确保该员工已签退且被标记为迟到");
         }
      } catch (Exception e) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "操作异常: " + e.getMessage());
      }
      return result;
   }

   /**
    * 确认早退扣款 — salaryDeduct += 50
    * POST /api/performance/confirm-early
    */
   @PostMapping({"/confirm-early"})
   public Map<String, Object> confirmEarly(@RequestBody ConfirmDeductionRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         int rows = this.performanceService.confirmEarly(request.getEmployeeId());
         if (rows > 0) {
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "早退记录已确认，扣款50元（更新" + rows + "条）");
         } else {
            result.put("code", 200);
            result.put("success", false);
            result.put("message", "没有找到未确认的早退记录，请确保该员工已签退且被标记为早退");
         }
      } catch (Exception e) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "操作异常: " + e.getMessage());
      }
      return result;
   }

   /**
    * 赦免迟到 — salaryDeduct -= 50（需满足 salaryDeduct >= 50 防负数）
    * POST /api/performance/forgive-late
    */
   @PostMapping({"/forgive-late"})
   public Map<String, Object> forgiveLate(@RequestBody ConfirmDeductionRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         int rows = this.performanceService.forgiveLate(request.getEmployeeId());
         if (rows > 0) {
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "迟到记录已赦免，扣款已撤销（更新" + rows + "条）");
         } else {
            result.put("code", 200);
            result.put("success", false);
            result.put("message", "没有找到已确认的迟到记录，请先确认再赦免");
         }
      } catch (Exception e) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "操作异常: " + e.getMessage());
      }
      return result;
   }

   /**
    * 赦免早退 — salaryDeduct -= 50
    * POST /api/performance/forgive-early
    */
   @PostMapping({"/forgive-early"})
   public Map<String, Object> forgiveEarly(@RequestBody ConfirmDeductionRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         int rows = this.performanceService.forgiveEarly(request.getEmployeeId());
         if (rows > 0) {
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "早退记录已赦免，扣款已撤销（更新" + rows + "条）");
         } else {
            result.put("code", 200);
            result.put("success", false);
            result.put("message", "没有找到已确认的早退记录，请先确认再赦免");
         }
      } catch (Exception e) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "操作异常: " + e.getMessage());
      }
      return result;
   }
}
