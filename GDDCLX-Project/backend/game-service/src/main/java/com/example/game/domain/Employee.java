package com.example.game.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工实体（游戏微服务副本）
 * 与 demo 微服务的 Employee 类完全一致，属于跨服务重复代码
 * 此处只读使用，用于根据工号查询员工基本信息和Session用户识别
 */
public class Employee implements Serializable {
   private static final long serialVersionUID = 1L;
   private Integer id;
   private String employeeId;  // 工号
   private String password;
   private String name;        // 姓名
   private String role;        // admin/employee
   private String department;  // 部门
   private String position;    // 岗位
   private String status;      // 在职/试用/离职
   private String positions;   // JSON多岗位
   private Date hireDate;      // 入职日期
   private Date createTime;
   private Date updateTime;

   // 手写getter/setter（略）
   public Integer getId() { return this.id; }
   public String getEmployeeId() { return this.employeeId; }
   public String getPassword() { return this.password; }
   public String getName() { return this.name; }
   public String getRole() { return this.role; }
   public String getDepartment() { return this.department; }
   public String getPosition() { return this.position; }
   public String getStatus() { return this.status; }
   public String getPositions() { return this.positions; }
   public Date getHireDate() { return this.hireDate; }
   public Date getCreateTime() { return this.createTime; }
   public Date getUpdateTime() { return this.updateTime; }

   public void setId(Integer id) { this.id = id; }
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setPassword(String password) { this.password = password; }
   public void setName(String name) { this.name = name; }
   public void setRole(String role) { this.role = role; }
   public void setDepartment(String department) { this.department = department; }
   public void setPosition(String position) { this.position = position; }
   public void setStatus(String status) { this.status = status; }
   public void setPositions(String positions) { this.positions = positions; }
   public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
   public void setCreateTime(Date createTime) { this.createTime = createTime; }
   public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
