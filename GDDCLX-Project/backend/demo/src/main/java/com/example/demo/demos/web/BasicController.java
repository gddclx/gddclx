package com.example.demo.demos.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Spring MVC 基础示例 Controller（脚手架生成，非业务代码）
 * 演示 @Controller（返回视图）和 @ResponseBody（返回JSON）的用法
 * 项目实际业务Controller全部使用 @RestController = @Controller + @ResponseBody
 *
 * 端点（均为示例，项目未使用）：
 * /hello?name=xxx       → 返回纯文本 Hello xxx
 * /user                 → 返回硬编码JSON {"name":"theonefx","age":666}
 * /save_user?name=&age= → 参数自动注入到User对象
 * /html                 → 返回视图名 "index.html"（项目无模板引擎，会报错）
 */
@Controller  // 注意：不是 @RestController，返回视图名而非JSON
public class BasicController {

   /**
    * 演示 @RequestParam — 从URL参数取值
    * GET /hello?name=张三 → 返回 "Hello 张三"
    * defaultValue 指定参数为空时的默认值
    */
   @RequestMapping({"/hello"})
   @ResponseBody  // 必须加此注解才返回字符串而非去找 hello.html 视图
   public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
      return "Hello " + name;
   }

   /**
    * 演示直接返回对象 → Jackson自动序列化为JSON
    * GET /user → {"name":"theonefx","age":666}
    */
   @RequestMapping({"/user"})
   @ResponseBody
   public User user() {
      User user = new User();
      user.setName("theonefx");  // 硬编码示例数据
      user.setAge(666);
      return user;
   }

   /**
    * 演示Spring自动参数绑定 — URL参数自动注入到User对象的同名属性
    * GET /save_user?name=test&age=25 → "user will save: name=test, age=25"
    */
   @RequestMapping({"/save_user"})
   @ResponseBody
   public String saveUser(User u) {  // Spring自动把 name→u.name, age→u.age
      return "user will save: name=" + u.getName() + ", age=" + u.getAge();
   }

   /**
    * 演示返回视图名（不加@ResponseBody）
    * GET /html → Spring去 templates/ 或 static/ 找 index.html
    * 当前项目没有配置Thymeleaf等模板引擎，此端点会报错
    */
   @RequestMapping({"/html"})
   public String html() {  // 返回值为视图名
      return "index.html";
   }

   /**
    * 演示 @ModelAttribute — 在每个请求处理前执行
    * 无论前端传什么参数，都会先把 User 对象的 name 强制覆盖为 "zhangsan"，age 强制覆盖为 18
    * 访问 /save_user?name=李四&age=30 → 实际存的是 zhangsan/18
    */
   @ModelAttribute
   public void parseUser(@RequestParam(name = "name", defaultValue = "unknown user") String name,
                         @RequestParam(name = "age", defaultValue = "12") Integer age,
                         User user) {
      user.setName("zhangsan");  // 强制覆盖
      user.setAge(18);
   }
}
