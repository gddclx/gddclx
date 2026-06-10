package com.example.demo.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工实体类 — 对应数据库表 employee
 * 系统的核心实体，贯穿所有模块（考勤、任务、绩效、游戏）
 * 实现 Serializable 接口以支持序列化存储
 * 注意：此类手写 getter/setter，未使用Lombok（与Task、Message不同，属于早期代码风格）
 */
public class Employee implements Serializable {
    /** 序列化版本号，保证反序列化兼容性 */
    private static final long serialVersionUID = 1L;

    /** 数据库自增主键 */
    private Integer id;

    /** 员工工号（业务主键），如 "A001"，登录凭据之一 */
    private String employeeId;

    /** 登录密码（明文存储，生产环境应改为BCrypt加密） */
    private String password;

    /** 员工姓名 */
    private String name;

    /** 角色权限：admin=管理员（可看全部）, employee=普通员工（只看自己） */
    private String role;

    /** 所属部门，如 "技术研发"、"人力资源"、"销售" */
    private String department;

    /** 当前显示岗位（单个字符串，优选positions列表中的第一个） */
    private String position;

    /** 员工状态：在职 / 试用 / 离职 */
    private String status;

    /**
     * 兼任岗位JSON数组
     * 格式: [{"position":"前端开发","department":"技术研发"}, {"position":"后端开发","department":"技术研发"}]
     * 存入数据库一个 TEXT/VARCHAR 列中（非标准范式，小项目够用）
     * 与 position 字段的关系：position 是首选展示岗位，positions 是完整岗位列表
     */
    private String positions;

    /** 入职日期 */
    private Date hireDate;

    /** 记录创建时间 */
    private Date createTime;

    /** 记录最后更新时间 */
    private Date updateTime;

    // ========== 以下为手写getter/setter ==========

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
