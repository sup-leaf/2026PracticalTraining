# 校园集市 - 后端说明

## 项目简介
校园集市是北京交通大学"专业课程综合实训"项目，为校内学生提供实习招聘、科研选人、竞赛组队等人才供需与科创协作服务。

## 技术栈
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- Redis

## 项目结构
```
backend/
├── src/main/java/com/bjtumarket/
│   ├── controller/    # 控制层
│   ├── service/      # 业务层
│   ├── mapper/       # 数据访问层
│   ├── entity/       # 实体类
│   ├── config/       # 配置类
│   └── vo/           # 视图对象
├── src/main/resources/
│   ├── application.yml  # 配置文件
│   └── mapper/          # MyBatis XML
├── sql/              # 数据库脚本
├── uploads/         # 上传文件目录
└── README.md       # 说明文件
```

## 已完成功能

### 1. 用户模块 (第二阶段)
- [x] 用户注册（学生/企业/教师三种身份）
- [x] 用户登录

### 2. 岗位模块 (第三阶段)
- [x] 岗位发布（企业/教师）
- [x] 岗位列表查询（分页、筛选、关键词搜索）
- [x] 岗位详情查看
- [x] 岗位删除
- [x] 我的发布列表

### 3. 简历模块 (第四阶段)
- [x] 简历保存/更新
- [x] 简历查看
- [x] 文件上传简历

### 4. 投递模块 (第五阶段)
- [x] 投递岗位
- [x] 投递列表查询
- [x] 投递状态管理（待查看→已查看→面试中→已录用/已拒绝）

## 接口文档

### 用户模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/register | POST | 用户注册 |
| /api/auth/login | POST | 用户登录 |

### 岗位模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/job/list | GET | 岗位列表 |
| /api/job/detail/{id} | GET | 岗位详情 |
| /api/job/publish | POST | 发布岗位 |
| /api/job/delete/{id} | DELETE | 删除岗位 |
| /api/job/my | GET | 我的发布 |

### 简历模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/resume/save | POST | 保存简历 |
| /api/resume/detail | GET | 查看简历 |
| /api/file/upload | POST | 上传文件 |

### 投递模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/delivery/apply | POST | 投递岗位 |
| /api/delivery/my | GET | 我的投递 |
| /api/delivery/job/{jobId} | GET | 岗位投递列表 |
| /api/delivery/status | PUT | 更新投递状态 |

## 数据库表
- t_user - 用户表
- t_job - 岗位表
- t_resume - 简历表
- t_delivery - 投递记录表

## 启动命令
```bash
cd D:\dasanxiaShixun\project\backend
mvn spring-boot:run
```

## 开发周期（立项文档）
- 第1-2周：需求确认、原型设计、数据库设计
- 第3-4周：后端核心开发
- 第5-6周：前端开发
- 第7周：前后端联调、内部测试
- 第8-9周：小范围用户验证