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

@RestController
@RequestMapping({"/api/employee"})
@CrossOrigin(
   origins = {"http://localhost:5501"}
)
public class EmployeeArchiveController {
   @Autowired
   private EmployeeArchiveService employeeArchiveService;

   @GetMapping({"/list"})
   public Map<String, Object> list() {
      Map<String, Object> result = new HashMap();

      try {
         List<EmployeeListResponse> data = this.employeeArchiveService.getAllEmployees();
         result.put("code", 200);
         result.put("success", true);
         result.put("data", data);
      } catch (Exception var3) {
         Exception e = var3;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "获取员工列表异常: " + e.getMessage());
      }

      return result;
   }

   @PostMapping({"/create"})
   public Map<String, Object> create(@RequestBody EmployeeCreateRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         this.employeeArchiveService.createEmployee(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "新增成功");
      } catch (EmployeeArchiveServiceImpl.BusinessException var4) {
         EmployeeArchiveServiceImpl.BusinessException e = var4;
         result.put("code", 400);
         result.put("success", false);
         result.put("message", e.getMessage());
      } catch (Exception var5) {
         Exception e = var5;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "新增异常: " + e.getMessage());
      }

      return result;
   }

   @PostMapping({"/update"})
   public Map<String, Object> update(@RequestBody EmployeeUpdateRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         this.employeeArchiveService.updateEmployee(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "修改成功");
      } catch (EmployeeArchiveServiceImpl.BusinessException var4) {
         EmployeeArchiveServiceImpl.BusinessException e = var4;
         result.put("code", 400);
         result.put("success", false);
         result.put("message", e.getMessage());
      } catch (Exception var5) {
         Exception e = var5;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "修改异常: " + e.getMessage());
      }

      return result;
   }

   @PostMapping({"/delete"})
   public Map<String, Object> delete(@RequestBody EmployeeDeleteRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         this.employeeArchiveService.deleteEmployee(request);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "删除成功");
      } catch (EmployeeArchiveServiceImpl.BusinessException var4) {
         EmployeeArchiveServiceImpl.BusinessException e = var4;
         result.put("code", 400);
         result.put("success", false);
         result.put("message", e.getMessage());
      } catch (Exception var5) {
         Exception e = var5;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "删除异常: " + e.getMessage());
      }

      return result;
   }
}
