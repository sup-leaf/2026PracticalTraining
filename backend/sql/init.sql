-- 校园集市数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_market DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_market;

-- 用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    user_type TINYINT NOT NULL COMMENT '用户类型: 1-学生 2-企业 3-教师',
    real_name VARCHAR(50) COMMENT '真实姓名',
    student_id VARCHAR(20) COMMENT '学号(学生)',
    campus_card_no VARCHAR(20) COMMENT '校园卡号(学生)',
    company_name VARCHAR(100) COMMENT '企业名称(企业)',
    company_code VARCHAR(20) COMMENT '统一社会信用代码(企业)',
    teacher_no VARCHAR(20) COMMENT '工号(教师)',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_user_type (user_type),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 岗位表
DROP TABLE IF EXISTS t_job;
CREATE TABLE t_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '岗位标题',
    description TEXT COMMENT '岗位描述',
    requirement TEXT COMMENT '岗位要求',
    skill_tags VARCHAR(200) COMMENT '技能标签(逗号分隔)',
    job_type TINYINT NOT NULL COMMENT '岗位类型: 1-实习 2-全职 3-科研助理',
    location VARCHAR(100) COMMENT '工作地点',
    salary_min DECIMAL(10,2) COMMENT '最低薪资',
    salary_max DECIMAL(10,2) COMMENT '最高薪资',
    duration INT COMMENT '实习时长(月)',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    publisher_type TINYINT NOT NULL COMMENT '发布者类型: 1-企业 2-教师',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    delivery_count INT DEFAULT 0 COMMENT '投递次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_job_type (job_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 简历表
DROP TABLE IF EXISTS t_resume;
CREATE TABLE t_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    age INT COMMENT '年龄',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(50) COMMENT '年级',
    gpa DOUBLE COMMENT '绩点',
    skills TEXT COMMENT '技能(逗号分隔)',
    projects TEXT COMMENT '项目经验',
    awards TEXT COMMENT '获奖经历',
    experience TEXT COMMENT '实习经历',
    self_evaluation TEXT COMMENT '自我评价',
    file_url VARCHAR(255) COMMENT '简历文件URL',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-未完善 1-已完善',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 投递记录表
DROP TABLE IF EXISTS t_delivery;
CREATE TABLE t_delivery (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id BIGINT NOT NULL COMMENT '岗位ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    job_publisher_id BIGINT NOT NULL COMMENT '岗位发布者ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待查看 1-已查看 2-面试中 3-已录用 4-已拒绝',
    hr_note VARCHAR(500) COMMENT 'HR备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_job_id (job_id),
    INDEX idx_resume_id (resume_id),
    INDEX idx_job_publisher_id (job_publisher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递记录表';