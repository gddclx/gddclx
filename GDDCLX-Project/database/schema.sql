-- ============================================================
-- GDDCLX 数据库结构导出
-- 数据库名: wechat
-- 导出时间: 2026-05-31
-- ============================================================

-- ============================================================
-- 表1: employee（员工表）
-- ============================================================
CREATE TABLE `employee` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` varchar(50) NOT NULL COMMENT '员工ID（工号）',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `role` varchar(50) NOT NULL COMMENT '角色',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `position` varchar(50) DEFAULT NULL COMMENT '职位',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态（active在职/leave离职/trial试用）',
  `positions` varchar(500) DEFAULT NULL COMMENT '岗位信息（JSON格式）',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `employee_id` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工表';


-- ============================================================
-- 表2: attendance（考勤记录表）
-- ============================================================
CREATE TABLE `attendance` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` varchar(50) NOT NULL COMMENT '员工ID（工号）',
  `checkin_date` date NOT NULL COMMENT '打卡日期',
  `checkin_time` datetime NOT NULL COMMENT '打卡时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_date` (`employee_id`,`checkin_date`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考勤记录表';
