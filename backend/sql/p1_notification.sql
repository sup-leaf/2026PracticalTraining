USE campus_market;

-- S6.2 通知表
CREATE TABLE IF NOT EXISTS t_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收者ID（岗位发布者）',
    type VARCHAR(50) NOT NULL COMMENT '通知类型: stale_job',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    related_id BIGINT COMMENT '关联ID（岗位ID等）',
    is_read TINYINT DEFAULT 0 COMMENT '0-未读 1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- S1.2 面试评价表
CREATE TABLE IF NOT EXISTS t_delivery_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    delivery_id BIGINT NOT NULL COMMENT '投递ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    rating INT NOT NULL COMMENT '评分(1-5)',
    review TEXT COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_delivery (delivery_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试体验评价表';
