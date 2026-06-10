package com.example.demo.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类 — 对应数据库表 user（已废弃）
 * 这是Spring Initializr脚手架自动生成的示例表对应的实体
 * 项目实际使用的是 Employee 表，此实体及对应的UserMapper/UserService/UserController均未使用
 * 字段：username(用户名), password(密码), email(邮箱), age(年龄) — 都是示例属性
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Integer id;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
    /** 邮箱 */
    private String email;
    /** 年龄 */
    private Integer age;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

    // ========== 手写getter/setter（废弃代码，未使用Lombok） ==========

    public Integer getId() { return this.id; }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public String getEmail() { return this.email; }
    public Integer getAge() { return this.age; }
    public Date getCreateTime() { return this.createTime; }
    public Date getUpdateTime() { return this.updateTime; }

    public void setId(Integer id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setAge(Integer age) { this.age = age; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
