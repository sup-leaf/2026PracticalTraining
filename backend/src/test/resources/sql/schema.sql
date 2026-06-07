-- H2 内存数据库初始化脚本 (MySQL 兼容模式)
-- 注意: 移除了 MySQL 特有的 ENGINE=InnoDB, DEFAULT CHARSET, COMMENT 语法

-- 用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    user_type TINYINT NOT NULL,
    real_name VARCHAR(50),
    student_id VARCHAR(20),
    campus_card_no VARCHAR(20),
    company_name VARCHAR(100),
    company_code VARCHAR(20),
    teacher_no VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(50),
    status TINYINT DEFAULT 1,
    member_level TINYINT DEFAULT 0,
    cooperation_type TINYINT DEFAULT 2,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 岗位表
DROP TABLE IF EXISTS t_job;
CREATE TABLE t_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    requirement TEXT,
    skill_tags VARCHAR(200),
    job_type TINYINT NOT NULL,
    location VARCHAR(100),
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    duration INT,
    publisher_id BIGINT NOT NULL,
    publisher_type TINYINT NOT NULL,
    status TINYINT DEFAULT 1,
    view_count INT DEFAULT 0,
    delivery_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 简历表
DROP TABLE IF EXISTS t_resume;
CREATE TABLE t_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(50),
    gender VARCHAR(10),
    age INT,
    phone VARCHAR(20),
    email VARCHAR(50),
    avatar VARCHAR(255),
    major VARCHAR(100),
    grade VARCHAR(50),
    gpa DOUBLE,
    skills TEXT,
    projects TEXT,
    awards TEXT,
    experience TEXT,
    self_evaluation TEXT,
    file_url VARCHAR(255),
    status TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 投递记录表
DROP TABLE IF EXISTS t_delivery;
CREATE TABLE t_delivery (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id BIGINT NOT NULL,
    resume_id BIGINT NOT NULL,
    job_publisher_id BIGINT NOT NULL,
    status TINYINT DEFAULT 0,
    hr_note VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 实习记录表
DROP TABLE IF EXISTS t_internship;
CREATE TABLE t_internship (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    resume_id BIGINT,
    job_id BIGINT,
    delivery_id BIGINT,
    company_name VARCHAR(100),
    position VARCHAR(100),
    start_date DATE,
    end_date DATE,
    status TINYINT DEFAULT 0,
    rating INT,
    review VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 实习日志表
DROP TABLE IF EXISTS t_internship_log;
CREATE TABLE t_internship_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    internship_id BIGINT NOT NULL,
    week_num INT,
    content TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 实习评价表 (学生对企业的评价)
DROP TABLE IF EXISTS t_internship_student_review;
CREATE TABLE t_internship_student_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    internship_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    rating INT,
    review VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (internship_id, student_id)
);

-- 实习沟通消息表
DROP TABLE IF EXISTS t_internship_message;
CREATE TABLE t_internship_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    internship_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 科研项目表
DROP TABLE IF EXISTS t_research_project;
CREATE TABLE t_research_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    requirement TEXT,
    background TEXT,
    funding VARCHAR(100),
    duration VARCHAR(50),
    publisher_id BIGINT NOT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 科研项目申请表
DROP TABLE IF EXISTS t_research_application;
CREATE TABLE t_research_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status TINYINT DEFAULT 0,
    note VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (project_id, student_id)
);

-- 竞赛组队表
DROP TABLE IF EXISTS t_competition_team;
CREATE TABLE t_competition_team (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    competition_name VARCHAR(100) NOT NULL,
    description TEXT,
    requirement TEXT,
    skill_tags VARCHAR(200),
    max_members INT DEFAULT 3,
    current_members INT DEFAULT 1,
    leader_id BIGINT NOT NULL,
    status TINYINT DEFAULT 1,
    deadline DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 组队申请表
DROP TABLE IF EXISTS t_team_application;
CREATE TABLE t_team_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status TINYINT DEFAULT 0,
    note VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (team_id, student_id)
);

-- 组队留言表
DROP TABLE IF EXISTS t_team_message;
CREATE TABLE t_team_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 爬虫执行历史表
DROP TABLE IF EXISTS t_crawl_history;
CREATE TABLE t_crawl_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source VARCHAR(50) NOT NULL,
    status INT NOT NULL DEFAULT 0,
    jobs_found INT DEFAULT 0,
    jobs_added INT DEFAULT 0,
    error_message TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME DEFAULT NULL
);

-- 通知表
DROP TABLE IF EXISTS t_notification;
CREATE TABLE t_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    related_id BIGINT,
    is_read TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 面试评价表
DROP TABLE IF EXISTS t_delivery_review;
CREATE TABLE t_delivery_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    delivery_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    rating INT NOT NULL,
    review TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (delivery_id)
);
