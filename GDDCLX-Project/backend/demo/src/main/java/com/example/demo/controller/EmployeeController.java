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

/**
 * 员工认证 Controller — 登录 + 注册
 * 接口路径前缀：/api（全局入口，不属于子模块）
 * 提供 POST /api/login 和 POST /api/register
 * Token生成：Base64编码的简易Token(非标准JWT)，包含 employeeId + name + timestamp
 */
@RestController
@RequestMapping({"/api"})         // 全局根路径
@CrossOrigin
public class EmployeeController {
   @Autowired
   private EmployeeService employeeService;  // 登录/注册服务

   /**
    * 用户登录
    * POST /api/login
    * @param request { "employee_id": "A001", "password": "123456" }
    * @return { code:200/401, data: { token:"eyJ...", user:{ employee_id, name, role } } }
    * 核心流程：查数据库验密码 → 生成Token → 构建UserInfo(不含password) → 返回
    * 密码校验：employee.getPassword().equals(inputPassword) — 明文比对
    */
   @PostMapping({"/login"})
   public Map<String, Object> login(@RequestBody LoginRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         // 第1步：数据库验证用户名密码
         Employee employee = this.employeeService.login(request.getEmployeeId(), request.getPassword());
         if (employee != null) {  // 登录成功
            // 第2步：生成简易Token（Base64编码）
            String token = this.employeeService.generateToken(employee);

            // 第3步：构建用户信息（不含密码）
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setEmployeeId(employee.getEmployeeId());
            userInfo.setName(employee.getName());
            userInfo.setRole(employee.getRole());  // admin 或 employee

            // 第4步：组装响应
            LoginResponse loginResponse = new LoginResponse(token, userInfo);
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("data", loginResponse);
         } else {
            // 登录失败：返回401 Unauthorized
            result.put("code", 401);
            result.put("success", false);
            result.put("message", "员工ID或密码错误");
         }
      } catch (Exception var7) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "登录异常: " + var7.getMessage());
      }
      return result;
   }

   /**
    * 用户注册
    * POST /api/register
    * 4层if-else嵌套校验（箭头代码反模式，改进方向：early return 或 @Valid注解）
    * 校验顺序：工号Empty → 姓名Empty → 密码Empty → 两次密码不一致 → 工号已存在
    */
   @PostMapping({"/register"})
   public Map<String, Object> register(@RequestBody RegisterRequest request) {
      Map<String, Object> result = new HashMap();
      try {
         if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()) {
            if (request.getName() != null && !request.getName().isEmpty()) {
               if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                  // 检查两次密码是否一致
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
                        // 注册失败（工号已存在）
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
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "注册异常: " + var4.getMessage());
         return result;
      }
   }
}
