package com.example.demo.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 考勤记录实体类 — 对应数据库表 attendance
 * 每个员工每天只有一条考勤记录（通过唯一约束保证）
 * 签到时间 > 9:00 自动标记迟到，签退时间 < 17:00 自动标记早退
 * 管理员确认后 salaryDeduct += 50，赦免后 salaryDeduct -= 50
 */
public class Attendance implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录主键ID，自增 */
    private Integer id;

    /** 员工工号（外键 → employee表） */
    private String employeeId;

    /** 打卡日期（只取日期部分，用于每日唯一约束） */
    private Date checkinDate;

    /** 签到时间（精确到时分秒，如 2026-06-08 08:55:30） */
    private Date checkinTime;

    /** 签退时间（精确到时分秒，未签退时为null） */
    private Date checkoutTime;

    /** 是否迟到：0=正常, 1=迟到（系统自动判定：签到时间 > 9:00） */
    private Integer isLate;

    /** 是否早退：0=正常, 1=早退（系统自动判定：签退时间 < 17:00） */
    private Integer isEarly;

    /** 迟到扣款是否已确认：0=未确认, 1=已确认（确认后 salaryDeduct += 50） */
    private Integer lateConfirmed;

    /** 早退扣款是否已确认：0=未确认, 1=已确认（确认后 salaryDeduct += 50） */
    private Integer earlyConfirmed;

    /**
     * 累计已确认扣款金额（元）
     * 每次确认迟到/早退 +50，每次赦免 -50
     * 赦免前检查 salaryDeduct >= 50，防止扣成负数
     */
    private Integer salaryDeduct;

    /** 记录创建时间 */
    private Date createTime;

    // ========== 手写getter/setter ==========

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
