-- 摸鱼小游戏数据库表

-- 员工游戏核心数据表
CREATE TABLE IF NOT EXISTS employee_game (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL UNIQUE COMMENT '关联员工ID',
    coins BIGINT DEFAULT 0 COMMENT '当前金币数',
    coin_level INT DEFAULT 0 COMMENT '升级等级',
    unclaimed_count INT DEFAULT 0 COMMENT '未领取累计次数，上限3',
    last_collect_time DATETIME COMMENT '上次领取时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工游戏核心数据';

-- 游戏投资记录表
CREATE TABLE IF NOT EXISTS game_investment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    amount BIGINT NOT NULL COMMENT '投入金币数',
    option_type TINYINT NOT NULL COMMENT '投资选项：1=人力 2=研发 3=销售',
    result_coins BIGINT COMMENT '结算后返回金币，null=未结算',
    invest_date DATE NOT NULL COMMENT '投资日期',
    settled TINYINT DEFAULT 0 COMMENT '是否结算：0=未结算 1=已结算',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_employee_date (employee_id, invest_date),
    INDEX idx_employee_id (employee_id),
    INDEX idx_invest_date (invest_date),
    INDEX idx_settled (settled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏投资记录';

-- 每日银行收益率表
CREATE TABLE IF NOT EXISTS game_bank_daily (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bank_date DATE NOT NULL UNIQUE COMMENT '当天日期',
    hr_rate DECIMAL(5,2) NOT NULL COMMENT '人力部门收益率',
    rd_rate DECIMAL(5,2) NOT NULL COMMENT '研发部门收益率',
    sales_rate DECIMAL(5,2) NOT NULL COMMENT '销售部门收益率',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bank_date (bank_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日银行收益率';

-- 初始化明天的收益率数据（用于测试）
INSERT INTO game_bank_daily (bank_date, hr_rate, rd_rate, sales_rate) VALUES
(DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1.05, 1.30, 0.85),
(CURDATE(), 1.05, 1.30, 0.85);
