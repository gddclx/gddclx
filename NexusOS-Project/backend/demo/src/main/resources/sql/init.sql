-- 创建数据库
CREATE DATABASE IF NOT EXISTS wechat DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wechat;

-- 创建员工表
CREATE TABLE IF NOT EXISTS employee (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    employee_id VARCHAR(50) NOT NULL UNIQUE COMMENT '员工ID',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    role VARCHAR(50) NOT NULL COMMENT '角色',
    positions VARCHAR(500) DEFAULT NULL COMMENT '岗位信息(JSON格式)',
    hire_date DATE DEFAULT NULL COMMENT '入职日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 插入测试数据
INSERT INTO employee (employee_id, password, name, role) VALUES 
('1698523', '请修改为安全密码', '管理员', 'admin');
