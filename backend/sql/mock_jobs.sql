SET NAMES utf8mb4;
USE campus_market;

-- 插入模拟企业
INSERT INTO t_user (username, password, user_type, company_name, company_code, status) VALUES 
('huawei_hr', MD5(MD5('123456')), 2, '华为技术有限公司', '914403001922038216', 1),
('tencent_hr', MD5(MD5('123456')), 2, '腾讯科技（深圳）有限公司', '9144030071526726XG', 1),
('bytedance_hr', MD5(MD5('123456')), 2, '北京字节跳动科技有限公司', '91110108590680892Q', 1),
('ali_hr', MD5(MD5('123456')), 2, '阿里巴巴（中国）有限公司', '91330100716105852X', 1);

-- 获取刚插入的企业ID并插入岗位
-- 华为岗位
INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT 'Java开发实习生', '负责华为云相关业务的后端开发与维护。', '熟悉Java，了解Spring Boot，对分布式系统感兴趣。', 'Java,Spring Boot,MySQL', 1, '深圳', 6000, 8000, 6, id, 1, 1 
FROM t_user WHERE username = 'huawei_hr';

INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT '通用软件工程师', '参与终端BG软件架构设计与核心代码编写。', '计算机相关专业，精通C/C++或Java，算法基础扎实。', 'C++,Java,数据结构', 2, '北京', 20000, 35000, NULL, id, 1, 1 
FROM t_user WHERE username = 'huawei_hr';

-- 腾讯岗位
INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT '前端开发工程师', '负责腾讯视频网页端及移动端开发。', '精通HTML/CSS/JS，熟悉React或Vue。', 'React,TypeScript,CSS3', 2, '深圳', 18000, 30000, NULL, id, 1, 1 
FROM t_user WHERE username = 'tencent_hr';

INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT '产品经理实习生', '参与QQ音乐新功能策划与用户调研。', '热爱音乐，逻辑性强，有良好的沟通能力。', '产品策划,调研,沟通', 1, '深圳', 4000, 6000, 3, id, 1, 1 
FROM t_user WHERE username = 'tencent_hr';

-- 字节跳动岗位
INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT '算法工程师（抖音）', '负责短视频推荐算法的优化。', '硕士及以上学历，熟悉深度学习框架，有顶会论文者优先。', 'Python,PyTorch,机器学习', 2, '北京', 30000, 50000, NULL, id, 1, 1 
FROM t_user WHERE username = 'bytedance_hr';

INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT '新媒体运营实习生', '负责TikTok官方账号的日常运营。', '英语水平优秀，有创意，熟悉海外社交媒体。', '运营,英语,创意', 1, '上海', 3000, 5000, 4, id, 1, 1 
FROM t_user WHERE username = 'bytedance_hr';

-- 阿里巴巴岗位
INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT '测试开发工程师', '负责淘宝App的质量保障与自动化测试工具开发。', '熟悉Python/Java，有测试开发经验者优先。', '自动化测试,Python,Selenium', 2, '杭州', 15000, 25000, NULL, id, 1, 1 
FROM t_user WHERE username = 'ali_hr';

INSERT INTO t_job (title, description, requirement, skill_tags, job_type, location, salary_min, salary_max, duration, publisher_id, publisher_type, status) 
SELECT '后端实习生（高德）', '参与高德地图路径规划算法的工程落地。', '熟悉C++，了解基础GIS知识。', 'C++,算法,GIS', 1, '北京', 5000, 7000, 5, id, 1, 1 
FROM t_user WHERE username = 'ali_hr';
