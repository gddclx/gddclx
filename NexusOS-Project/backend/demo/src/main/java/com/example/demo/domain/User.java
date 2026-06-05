package com.example.demo.domain;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
   private static final long serialVersionUID = 1L;
   private Integer id;
   private String username;
   private String password;
   private String email;
   private Integer age;
   private Date createTime;
   private Date updateTime;

   public Integer getId() {
      return this.id;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public String getEmail() {
      return this.email;
   }

   public Integer getAge() {
      return this.age;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setAge(Integer age) {
      this.age = age;
   }

   public void setCreateTime(Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
   }

}
