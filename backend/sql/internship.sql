USE campus_market;

-- 实习记录表
CREATE TABLE IF NOT EXISTS t_internship (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    resume_id BIGINT COMMENT '简历ID',
    job_id BIGINT COMMENT '岗位ID',
    delivery_id BIGINT COMMENT '关联的投递记录ID',
    company_name VARCHAR(100) COMMENT '企业名称',
    position VARCHAR(100) COMMENT '实习岗位',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-进行中 1-已完成 2-提前终止',
    rating INT COMMENT '企业评分 1-5',
    review VARCHAR(500) COMMENT '企业评价',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实习记录表';

-- 实习日志表
CREATE TABLE IF NOT EXISTS t_internship_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    internship_id BIGINT NOT NULL COMMENT '实习记录ID',
    week_num INT COMMENT '第几周',
    content TEXT COMMENT '日志内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_internship_id (internship_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实习日志表';
