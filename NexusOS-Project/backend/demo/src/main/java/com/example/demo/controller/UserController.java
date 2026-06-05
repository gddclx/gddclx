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

@RestController
@RequestMapping({"/api/user"})
@CrossOrigin
public class UserController {
   @Autowired
   private UserService userService;

   @PostMapping({"/add"})
   public Map<String, Object> addUser(@RequestBody User user) {
      Map<String, Object> result = new HashMap();

      try {
         int count = this.userService.addUser(user);
         if (count > 0) {
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
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "添加异常: " + e.getMessage());
      }

      return result;
   }

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
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "更新异常: " + e.getMessage());
      }

      return result;
   }

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
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "删除异常: " + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/get/{id}"})
   public Map<String, Object> getUserById(@PathVariable Integer id) {
      Map<String, Object> result = new HashMap();

      try {
         User user = this.userService.getUserById(id);
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "查询成功");
         result.put("data", user);
      } catch (Exception var4) {
         Exception e = var4;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "查询异常: " + e.getMessage());
      }

      return result;
   }

   @GetMapping({"/list"})
   public Map<String, Object> getAllUsers() {
      Map<String, Object> result = new HashMap();

      try {
         List<User> userList = this.userService.getAllUsers();
         result.put("code", 200);
         result.put("success", true);
         result.put("message", "查询成功");
         result.put("data", userList);
      } catch (Exception var3) {
         Exception e = var3;
         result.put("code", 500);
         result.put("success", false);
         result.put("message", "查询异常: " + e.getMessage());
      }

      return result;
   }
}
