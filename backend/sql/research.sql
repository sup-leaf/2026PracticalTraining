USE campus_market;

-- 科研项目表
CREATE TABLE IF NOT EXISTS t_research_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '项目标题',
    description TEXT COMMENT '项目描述',
    requirement TEXT COMMENT '招募要求',
    background TEXT COMMENT '项目背景',
    funding VARCHAR(100) COMMENT '经费支持',
    duration VARCHAR(50) COMMENT '预计周期',
    publisher_id BIGINT NOT NULL COMMENT '发布者(教师)ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-招募中 2-已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研项目表';

-- 科研项目申请表
CREATE TABLE IF NOT EXISTS t_research_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL COMMENT '项目ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 2-已驳回',
    note VARCHAR(500) COMMENT '申请备注/驳回原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_project_id (project_id),
    INDEX idx_student_id (student_id),
    UNIQUE KEY uk_project_student (project_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科研项目申请表';
