package com.example.demo.controller;

import com.example.demo.dto.PositionListPageResponse;
import com.example.demo.dto.PositionStatsResponse;
import com.example.demo.dto.PositionUpdateRequest;
import com.example.demo.service.PositionService;
import com.example.demo.service.impl.PositionServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/position"})
@CrossOrigin(
   origins = {"http://localhost:5501"}
)
public class PositionController {
   @Autowired
   private PositionService positionService;

   @GetMapping({"/list"})
   public Map<String, Object> list(@RequestParam(defaultValue = "1") int page, @RequestParam(name = "page_size",defaultValue = "10") int pageSize, @RequestParam(defaultValue = "") String search) {
      Map<String, Object> result = new HashMap();

      try {
         PositionListPageResponse data = this.positionService.getAllPositionList(page, pageSize, search);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "success");
         result.put("data", data);
      } catch (Exception var6) {
         Exception e = var6;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取岗位列表异常: " + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/stats"})
   public Map<String, Object> stats() {
      Map<String, Object> result = new HashMap();

      try {
         List<PositionStatsResponse> data = this.positionService.getPositionStats();
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "success");
         result.put("data", data);
      } catch (Exception var3) {
         Exception e = var3;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取岗位统计异常: " + e.getMessage());
      }

      return result;
   }

   @PostMapping({"/update"})
   public Map<String, Object> update(@RequestBody PositionUpdateRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         this.positionService.updatePositions(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "更新成功");
      } catch (PositionServiceImpl.NotFoundException var4) {
         PositionServiceImpl.NotFoundException e = var4;
         result.put("code", 404);
         result.put("success", false);
         result.put("message", e.getMessage());
      } catch (PositionServiceImpl.BusinessException var5) {
         PositionServiceImpl.BusinessException e = var5;
         result.put("code", 400);
         result.put("success", false);
         result.put("message", e.getMessage());
      } catch (Exception var6) {
         Exception e = var6;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "更新岗位异常: " + e.getMessage());
      }

      return result;
   }
}
