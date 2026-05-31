USE campus_market;

-- S8.1 组队留言表
CREATE TABLE IF NOT EXISTS t_team_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL COMMENT '队伍ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_team_id (team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组队讨论区消息表';

-- S8.2 实习沟通表
CREATE TABLE IF NOT EXISTS t_internship_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    internship_id BIGINT NOT NULL COMMENT '实习ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_internship_id (internship_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实习沟通消息表';
