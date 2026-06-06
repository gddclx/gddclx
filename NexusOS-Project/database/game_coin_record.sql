-- 金币变动记录表
CREATE TABLE IF NOT EXISTS game_coin_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    type VARCHAR(20) NOT NULL COMMENT '类型: invest/settlement/collect/sign/upgrade',
    amount BIGINT NOT NULL COMMENT '变动金额(正=收入,负=支出)',
    balance_after BIGINT NOT NULL COMMENT '变动后余额',
    description VARCHAR(255) DEFAULT NULL COMMENT '描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_employee_created (employee_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='金币变动记录表';
