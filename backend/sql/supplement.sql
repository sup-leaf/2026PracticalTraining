USE campus_market;

-- 双向评价：学生对企业的实习评价表
CREATE TABLE IF NOT EXISTS t_internship_student_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    internship_id BIGINT NOT NULL COMMENT '实习记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    rating INT COMMENT '评分 1-5',
    review VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_internship_student (internship_id, student_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生对实习的评价表';

-- 学生会员：新增会员等级字段（如已执行过可忽略报错）
ALTER TABLE t_user ADD COLUMN member_level TINYINT DEFAULT 0 COMMENT '会员等级: 0-免费 1-VIP';

-- 企业分级：新增合作类型字段（如已执行过可忽略报错）
ALTER TABLE t_user ADD COLUMN cooperation_type TINYINT DEFAULT 2 COMMENT '合作类型: 1-深度合作 2-校外普通';
