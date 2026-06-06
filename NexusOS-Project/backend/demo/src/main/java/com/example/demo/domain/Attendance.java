package com.example.demo.domain;

import java.io.Serializable;
import java.util.Date;

public class Attendance implements Serializable {
   private static final long serialVersionUID = 1L;
   private Integer id;
   private String employeeId;
   private Date checkinDate;
   private Date checkinTime;
   private Date checkoutTime;
   private Integer isLate;
   private Integer isEarly;
   private Integer lateConfirmed;
   private Integer earlyConfirmed;
   private Integer salaryDeduct;
   private Date createTime;

   public Integer getId() { return this.id; }
   public String getEmployeeId() { return this.employeeId; }
   public Date getCheckinDate() { return this.checkinDate; }
   public Date getCheckinTime() { return this.checkinTime; }
   public Date getCheckoutTime() { return this.checkoutTime; }
   public Integer getIsLate() { return this.isLate; }
   public Integer getIsEarly() { return this.isEarly; }
   public Integer getLateConfirmed() { return this.lateConfirmed; }
   public Integer getEarlyConfirmed() { return this.earlyConfirmed; }
   public Integer getSalaryDeduct() { return this.salaryDeduct; }
   public Date getCreateTime() { return this.createTime; }

   public void setId(Integer id) { this.id = id; }
   public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
   public void setCheckinDate(Date checkinDate) { this.checkinDate = checkinDate; }
   public void setCheckinTime(Date checkinTime) { this.checkinTime = checkinTime; }
   public void setCheckoutTime(Date checkoutTime) { this.checkoutTime = checkoutTime; }
   public void setIsLate(Integer isLate) { this.isLate = isLate; }
   public void setIsEarly(Integer isEarly) { this.isEarly = isEarly; }
   public void setLateConfirmed(Integer lateConfirmed) { this.lateConfirmed = lateConfirmed; }
   public void setEarlyConfirmed(Integer earlyConfirmed) { this.earlyConfirmed = earlyConfirmed; }
   public void setSalaryDeduct(Integer salaryDeduct) { this.salaryDeduct = salaryDeduct; }
   public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
