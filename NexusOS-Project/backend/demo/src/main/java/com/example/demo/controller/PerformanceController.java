package com.example.demo.controller;

import com.example.demo.dto.PerformanceResponse;
import com.example.demo.service.PerformanceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
}
