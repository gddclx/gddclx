package com.example.demo.controller;

import com.example.demo.dto.EmployeeCreateRequest;
import com.example.demo.dto.EmployeeDeleteRequest;
import com.example.demo.dto.EmployeeListResponse;
import com.example.demo.dto.EmployeeUpdateRequest;
import com.example.demo.service.EmployeeArchiveService;
import com.example.demo.service.impl.EmployeeArchiveServiceImpl;
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
 * 员工档案管理 Controller
 * 提供员工CRUD操作（创建、查看、修改、删除）
 * 接口路径前缀：/api/employee
 * 特点：三层异常分拣 — BusinessException(400) / Exception(500)
 * 注意：@CrossOrigin origins 限定 localhost:5501（历史遗留，全局CorsConfig已覆盖）
 */
@RestController
@RequestMapping({"/api/employee"})
@CrossOrigin(
   origins = {"http://localhost:5501"}  // VS Code Live Server 默认端口（历史遗留）
)
public class EmployeeArchiveController {
   @Autowired
   private EmployeeArchiveService employeeArchiveService;  // 员工档案CRUD

   /**
    * 获取全部员工列表
    * GET /api/employee/list
    * 返回数据包含：工号、姓名、部门、岗位、入职日期、状态
    * 不包含密码和薪资敏感字段
    */
   @GetMapping({"/list"})
   public Map<String, Object> list() {
      Map<String, Object> result = new HashMap();
      try {
         List<EmployeeListResponse> data = this.employeeArchiveService.getAllEmployees();
         result.put("code", 200);
         result.put("success", true);
         result.put("data", data);
      } catch (Exception var3) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取员工列表异常: " + var3.getMessage());
      }
      return result;
   }

   /**
    * 新增员工 — 三层异常分拣
    * POST /api/employee/create
    * BusinessException(如"工号已存在") → 400 业务错误
    * Exception(如数据库连接失败) → 500 系统错误
    * 新员工密码使用配置项 app.default-password 的默认值 Changeme_123
    */
   @PostMapping({"/create"})
   public Map<String, Object> create(@RequestBody EmployeeCreateRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         this.employeeArchiveService.createEmployee(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "新增成功");
      } catch (EmployeeArchiveServiceImpl.BusinessException var4) {
         // 业务异常：工号已存在、必填项为空等 → 400
         result.put("code", 400);
         result.put("success", false);
         result.put("message", var4.getMessage());
      } catch (Exception var5) {
         // 系统异常：数据库错误等 → 500
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "新增异常: " + var5.getMessage());
      }
      return result;
   }

   /**
    * 修改员工信息 — 支持修改工号
    * POST /api/employee/update
    * originalEmployeeId 定位员工，可以改工号（先检查新工号是否冲突）
    */
   @PostMapping({"/update"})
   public Map<String, Object> update(@RequestBody EmployeeUpdateRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         this.employeeArchiveService.updateEmployee(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "修改成功");
      } catch (EmployeeArchiveServiceImpl.BusinessException var4) {
         result.put("code", 400);
         result.put("success", false);
         result.put("message", var4.getMessage());
      } catch (Exception var5) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "修改异常: " + var5.getMessage());
      }
      return result;
   }

   /**
    * 删除员工
    * POST /api/employee/delete
    * 注意：使用POST而非DELETE方法（不够RESTful）
    * 删除前检查员工是否存在，不存在抛BusinessException
    */
   @PostMapping({"/delete"})
   public Map<String, Object> delete(@RequestBody EmployeeDeleteRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         this.employeeArchiveService.deleteEmployee(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "删除成功");
      } catch (EmployeeArchiveServiceImpl.BusinessException var4) {
         result.put("code", 400);
         result.put("success", false);
         result.put("message", var4.getMessage());
      } catch (Exception var5) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "删除异常: " + var5.getMessage());
      }
      return result;
   }
}
