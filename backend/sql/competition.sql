USE campus_market;

-- 竞赛组队表
CREATE TABLE IF NOT EXISTS t_competition_team (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '组队标题',
    competition_name VARCHAR(100) NOT NULL COMMENT '赛事名称',
    description TEXT COMMENT '队伍描述',
    requirement TEXT COMMENT '队员要求',
    skill_tags VARCHAR(200) COMMENT '技能标签(逗号分隔)',
    max_members INT DEFAULT 3 COMMENT '人数上限',
    current_members INT DEFAULT 1 COMMENT '当前人数',
    leader_id BIGINT NOT NULL COMMENT '队长(学生)ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-招募中 2-已满员/已结束',
    deadline DATETIME COMMENT '截止日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_leader_id (leader_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛组队表';

-- 组队申请表
CREATE TABLE IF NOT EXISTS t_team_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL COMMENT '队伍ID',
    student_id BIGINT NOT NULL COMMENT '申请人ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 2-已拒绝',
    note VARCHAR(500) COMMENT '申请备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_team_id (team_id),
    INDEX idx_student_id (student_id),
    UNIQUE KEY uk_team_student (team_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组队申请表';
