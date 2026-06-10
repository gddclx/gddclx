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

/**
 * 岗位管理 Controller
 * 接口路径前缀：/api/position
 * 提供岗位列表（分页+搜索）、岗位统计、批量更新岗位
 *
 * 亮点：最精细的异常分拣 — NotFoundException(404) / BusinessException(400) / Exception(500)
 * 岗位数据存储在 employee.positions JSON列中，非标准范式但小项目够用
 * @RequestParam name="page_size" 实现URL蛇形参数 ↔ Java驼峰属性的映射
 */
@RestController
@RequestMapping({"/api/position"})
@CrossOrigin(
   origins = {"http://localhost:5501"}  // VS Code Live Server默认端口
)
public class PositionController {
   @Autowired
   private PositionService positionService;

   /**
    * 分页岗位列表 — 支持搜索
    * GET /api/position/list?page=1&page_size=10&search=张三
    * @param page 页码（从1开始）
    * @param pageSize 每页条数（name="page_size" 将URL的page_size映射到Java的pageSize）
    * @param search 搜索关键词（匹配工号/姓名/岗位名）
    */
   @GetMapping({"/list"})
   public Map<String, Object> list(
           @RequestParam(defaultValue = "1") int page,              // 默认第1页
           @RequestParam(name = "page_size", defaultValue = "10") int pageSize,  // 默认每页10条
           @RequestParam(defaultValue = "") String search) {       // 默认不搜索
      Map<String, Object> result = new HashMap();
      try {
         PositionListPageResponse data = this.positionService.getAllPositionList(page, pageSize, search);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "success");
         result.put("data", data);  // { list: [...], total: N }
      } catch (Exception var6) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取岗位列表异常: " + var6.getMessage());
      }
      return result;
   }

   /**
    * 岗位统计 — 各岗位人数分布
    * GET /api/position/stats
    * 返回：[ {name:"前端开发", count:3}, {name:"后端开发", count:2}, ... ]
    * 结果按人数降序排列
    */
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
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取岗位统计异常: " + var3.getMessage());
      }
      return result;
   }

   /**
    * 批量更新员工岗位
    * POST /api/position/update { employeeId, department, positions: ["前端","后端"] }
    * 四层异常分拣：NotFoundException(404) / BusinessException(400) / Exception(500)
    * 处理流程：查员工 → 前端传岗位数组 → Jackson序列化为JSON → 存入employee.positions列
    */
   @PostMapping({"/update"})
   public Map<String, Object> update(@RequestBody PositionUpdateRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         this.positionService.updatePositions(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "更新成功");
      } catch (PositionServiceImpl.NotFoundException var4) {
         // 找不到员工 → 404 Not Found
         result.put("code", 404);
         result.put("success", false);
         result.put("message", var4.getMessage());
      } catch (PositionServiceImpl.BusinessException var5) {
         // 业务校验失败 → 400 Bad Request
         result.put("code", 400);
         result.put("success", false);
         result.put("message", var5.getMessage());
      } catch (Exception var6) {
         // 系统异常 → 500 Internal Server Error
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "更新岗位异常: " + var6.getMessage());
      }
      return result;
   }
}
