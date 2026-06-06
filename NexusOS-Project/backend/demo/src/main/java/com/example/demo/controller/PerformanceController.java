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

@RestController
@RequestMapping({"/api/performance"})
@CrossOrigin
public class PerformanceController {
   @Autowired
   private PerformanceService performanceService;

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
         Exception e = var3;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取绩效列表异常: " + e.getMessage());
         result.put("data", (Object)null);
      }

      return result;
   }

   @PostMapping({"/confirm-late"})
   public Map<String, Object> confirmLate(@RequestBody ConfirmDeductionRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         int rows = this.performanceService.confirmLate(request.getEmployeeId());
         if (rows > 0) {
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "迟到记录已确认，扣款50元（更新" + rows + "条）");
         } else {
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
