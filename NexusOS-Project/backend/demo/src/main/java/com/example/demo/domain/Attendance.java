package com.example.demo.domain;

import java.io.Serializable;
import java.util.Date;

public class Attendance implements Serializable {
   private static final long serialVersionUID = 1L;
   private Integer id;
   private String employeeId;
   private Date checkinDate;
   private Date checkinTime;
   private Date createTime;

   public Integer getId() {
      return this.id;
   }

   public String getEmployeeId() {
      return this.employeeId;
   }

   public Date getCheckinDate() {
      return this.checkinDate;
   }

   public Date getCheckinTime() {
      return this.checkinTime;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public void setEmployeeId(String employeeId) {
      this.employeeId = employeeId;
   }

   public void setCheckinDate(Date checkinDate) {
      this.checkinDate = checkinDate;
   }

   public void setCheckinTime(Date checkinTime) {
      this.checkinTime = checkinTime;
   }

   public void setCreateTime(Date createTime) {
      this.createTime = createTime;
   }

}
