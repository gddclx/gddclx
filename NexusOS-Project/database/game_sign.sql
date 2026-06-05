-- 添加每日签到相关字段
ALTER TABLE employee_game
ADD COLUMN last_sign_date DATE DEFAULT NULL COMMENT '上次签到日期',
ADD COLUMN sign_count INT DEFAULT 0 COMMENT '连续签到天数';

-- 添加签到记录的表
CREATE TABLE IF NOT EXISTS game_sign_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    sign_date DATE NOT NULL,
    coins_earned BIGINT NOT NULL DEFAULT 200,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_employee_date (employee_id, sign_date),
    INDEX idx_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';
