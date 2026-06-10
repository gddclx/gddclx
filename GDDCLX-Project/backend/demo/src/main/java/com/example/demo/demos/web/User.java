package com.example.demo.demos.web;

/**
 * 示例POJO类（脚手架生成，非业务实体）
 * 演示最简单的手写getter/setter方式（未用Lombok）
 * 仅供 BasicController 的演示端点使用
 * 项目实际使用 domain/Employee.java、@Data Lombok注解
 *
 * 字段：name(姓名), age(年龄)
 * 这不是 domain/User.java，两者处于不同包，功能不同
 */
public class User {
   private String name;   // 姓名
   private Integer age;   // 年龄

   // 手写getter/setter（未使用Lombok @Data）
   public String getName() { return this.name; }
   public void setName(String name) { this.name = name; }
   public Integer getAge() { return this.age; }
   public void setAge(Integer age) { this.age = age; }
}
