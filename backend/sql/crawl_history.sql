-- 爬虫执行历史表
CREATE TABLE IF NOT EXISTS `t_crawl_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `source` VARCHAR(50) NOT NULL COMMENT '爬取来源',
    `status` INT NOT NULL DEFAULT 0 COMMENT '状态: 0-进行中, 1-成功, 2-失败',
    `jobs_found` INT DEFAULT 0 COMMENT '发现岗位数',
    `jobs_added` INT DEFAULT 0 COMMENT '新增岗位数',
    `error_message` TEXT COMMENT '错误信息',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
