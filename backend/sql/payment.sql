USE campus_market;

-- 支付订单表
CREATE TABLE IF NOT EXISTS t_payment_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '商户订单号',
    trade_no VARCHAR(50) COMMENT '第三方交易号（支付成功后填充）',
    user_id BIGINT NOT NULL COMMENT '支付者ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    fee_type VARCHAR(30) NOT NULL COMMENT '费用类型: vip-会员费',
    status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending-待支付/success-已支付/failed-支付失败',
    pay_url VARCHAR(500) COMMENT '支付链接/二维码地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';
