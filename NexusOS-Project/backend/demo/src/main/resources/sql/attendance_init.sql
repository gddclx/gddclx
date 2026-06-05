-- ========================================
-- 员工考勤系统 - 数据库初始化脚本
-- ========================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS wechat DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wechat;

-- ========================================
-- 1. 员工表（如果不存在）
-- ========================================
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

-- ========================================
-- 2. 考勤记录表（新建）
-- ========================================
CREATE TABLE IF NOT EXISTS attendance (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '员工ID',
    checkin_date DATE NOT NULL COMMENT '打卡日期',
    checkin_time DATETIME NOT NULL COMMENT '打卡时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_employee_date (employee_id, checkin_date) COMMENT '员工+日期唯一索引，防止重复打卡'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- ========================================
-- 3. 插入测试数据
-- ========================================

-- 插入测试员工（如果不存在）
INSERT IGNORE INTO employee (employee_id, password, name, role) 
VALUES 
('1698523', '请修改为安全密码', '管理员', 'admin'),
('EMP-2026001', '请修改为安全密码', '张三', 'user'),
('EMP-2026002', '请修改为安全密码', '李四', 'user'),
('EMP-2026003', '请修改为安全密码', '王五', 'user');

-- 插入一些测试考勤数据（方便测试）
INSERT IGNORE INTO attendance (employee_id, checkin_date, checkin_time) 
VALUES 
('1698523', CURDATE() - INTERVAL 6 DAY, CONCAT(CURDATE() - INTERVAL 6 DAY, ' 09:00:00')),
('1698523', CURDATE() - INTERVAL 5 DAY, CONCAT(CURDATE() - INTERVAL 5 DAY, ' 08:55:00')),
('1698523', CURDATE() - INTERVAL 4 DAY, CONCAT(CURDATE() - INTERVAL 4 DAY, ' 09:10:00')),
('1698523', CURDATE() - INTERVAL 3 DAY, CONCAT(CURDATE() - INTERVAL 3 DAY, ' 08:45:00')),
('1698523', CURDATE() - INTERVAL 2 DAY, CONCAT(CURDATE() - INTERVAL 2 DAY, ' 09:05:00')),
('EMP-2026001', CURDATE() - INTERVAL 2 DAY, CONCAT(CURDATE() - INTERVAL 2 DAY, ' 08:50:00')),
('EMP-2026002', CURDATE() - INTERVAL 1 DAY, CONCAT(CURDATE() - INTERVAL 1 DAY, ' 09:00:00'));
