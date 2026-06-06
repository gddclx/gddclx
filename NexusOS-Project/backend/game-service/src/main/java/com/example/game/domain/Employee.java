package com.example.game.domain;

import java.io.Serializable;
import java.util.Date;

public class Employee implements Serializable {
   private static final long serialVersionUID = 1L;
   private Integer id;
   private String employeeId;
   private String password;
   private String name;
   private String role;
   private String department;
   private String position;
   private String status;
   private String positions;
   private Date hireDate;
   private Date createTime;
   private Date updateTime;

   public Integer getId() {
      return this.id;
   }

   public String getEmployeeId() {
      return this.employeeId;
   }

   public String getPassword() {
      return this.password;
   }

   public String getName() {
      return this.name;
   }

   public String getRole() {
      return this.role;
   }

   public String getDepartment() {
      return this.department;
   }

   public String getPosition() {
      return this.position;
   }

   public String getStatus() {
      return this.status;
   }

   public String getPositions() {
      return this.positions;
   }

   public Date getHireDate() {
      return this.hireDate;
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

   public void setEmployeeId(String employeeId) {
      this.employeeId = employeeId;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public void setDepartment(String department) {
      this.department = department;
   }

   public void setPosition(String position) {
      this.position = position;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setPositions(String positions) {
      this.positions = positions;
   }

   public void setHireDate(Date hireDate) {
      this.hireDate = hireDate;
   }

   public void setCreateTime(Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
   }

}
