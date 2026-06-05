package com.example.demo.controller;

import com.example.demo.domain.Employee;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.EmployeeService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api"})
@CrossOrigin
public class EmployeeController {
   @Autowired
   private EmployeeService employeeService;

   @PostMapping({"/login"})
   public Map<String, Object> login(@RequestBody LoginRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         Employee employee = this.employeeService.login(request.getEmployeeId(), request.getPassword());
         if (employee != null) {
            String token = this.employeeService.generateToken(employee);
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setEmployeeId(employee.getEmployeeId());
            userInfo.setName(employee.getName());
            userInfo.setRole(employee.getRole());
            LoginResponse loginResponse = new LoginResponse(token, userInfo);
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", loginResponse);
         } else {
            result.put("code", 401);
            result.put("success", false);
            result.put("message", "员工ID或密码错误");
         }
      } catch (Exception var7) {
         Exception e = var7;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "登录异常: " + e.getMessage());
      }

      return result;
   }

   @PostMapping({"/register"})
   public Map<String, Object> register(@RequestBody RegisterRequest request) {
      Map<String, Object> result = new HashMap();

      try {
         if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()) {
            if (request.getName() != null && !request.getName().isEmpty()) {
               if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                  if (!request.getPassword().equals(request.getConfirmPassword())) {
                     result.put("code", 400);
                     result.put("success", false);
                     result.put("message", "两次密码不一致");
                     return result;
                  } else {
                     boolean success = this.employeeService.register(request);
                     if (success) {
                        result.put("code", 200);
                        result.put("success", true);
                        result.put("message", "注册成功，请登录");
                        result.put("data", (Object)null);
                     } else {
                        result.put("code", 400);
                        result.put("success", false);
                        result.put("message", "注册失败，员工ID已存在");
                     }

                     return result;
                  }
               } else {
                  result.put("code", 400);
                  result.put("success", false);
                  result.put("message", "密码不能为空");
                  return result;
               }
            } else {
               result.put("code", 400);
               result.put("success", false);
               result.put("message", "姓名不能为空");
               return result;
            }
         } else {
            result.put("code", 400);
            result.put("success", false);
            result.put("message", "员工ID不能为空");
            return result;
         }
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "注册异常: " + e.getMessage());
         return result;
      }
   }
}
