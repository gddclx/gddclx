package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理 Controller（已废弃）
 * 操作的是旧的 user 表（Spring Initializr脚手架生成），不是项目使用的 employee 表
 * 前端没有任何页面调用这些接口（搜索 /api/user 结果为0个匹配）
 * 功能已被 EmployeeArchiveController 完全覆盖
 * 保留原因：未清理的脚手架遗留代码，不影响运行
 *
 * 接口列表（均未使用）：
 * POST   /api/user/add       — 新增用户
 * PUT    /api/user/update    — 更新用户
 * DELETE /api/user/delete/{id} — 删除用户（RESTful标准，用@PathVariable）
 * GET    /api/user/get/{id}  — 查询单个用户
 * GET    /api/user/list      — 用户列表
 */
@RestController
@RequestMapping({"/api/user"})
@CrossOrigin
public class UserController {
   @Autowired
   private UserService userService;  // 操作 user 表（非 employee 表）

   /** 新增用户 — POST /api/user/add */
   @PostMapping({"/add"})
   public Map<String, Object> addUser(@RequestBody User user) {
      Map<String, Object> result = new HashMap();
      try {
         int count = this.userService.addUser(user);  // MyBatis返回影响行数
         if (count > 0) {  // 成功插入
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "添加成功");
            result.put("data", user);
         } else {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "添加失败");
         }
      } catch (Exception var4) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "添加异常: " + var4.getMessage());
      }
      return result;
   }

   /** 更新用户 — PUT /api/user/update（使用PUT方法，符合RESTful规范） */
   @PutMapping({"/update"})
   public Map<String, Object> updateUser(@RequestBody User user) {
      Map<String, Object> result = new HashMap();
      try {
         int count = this.userService.updateUser(user);
         if (count > 0) {
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "更新成功");
         } else {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "更新失败");
         }
      } catch (Exception var4) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "更新异常: " + var4.getMessage());
      }
      return result;
   }

   /** 删除用户 — DELETE /api/user/delete/{id}（RESTful标准写法） */
   @DeleteMapping({"/delete/{id}"})
   public Map<String, Object> deleteUser(@PathVariable Integer id) {
      Map<String, Object> result = new HashMap();
      try {
         int count = this.userService.deleteUser(id);
         if (count > 0) {
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "删除成功");
         } else {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "删除失败");
         }
      } catch (Exception var4) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "删除异常: " + var4.getMessage());
      }
      return result;
   }

   /** 查询单个用户 — GET /api/user/get/{id} */
   @GetMapping({"/get/{id}"})
   public Map<String, Object> getUserById(@PathVariable Integer id) {
      Map<String, Object> result = new HashMap();
      try {
         User user = this.userService.getUserById(id);
         result.put("code", 200);
         result.put("success", true);
         result.put("data", user);
      } catch (Exception var4) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "查询异常: " + var4.getMessage());
      }
      return result;
   }

   /** 查询所有用户 — GET /api/user/list */
   @GetMapping({"/list"})
   public Map<String, Object> getAllUsers() {
      Map<String, Object> result = new HashMap();
      try {
         List<User> userList = this.userService.getAllUsers();
         result.put("code", 200);
         result.put("success", true);
         result.put("data", userList);
      } catch (Exception var3) {
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "查询异常: " + var3.getMessage());
      }
      return result;
   }
}
