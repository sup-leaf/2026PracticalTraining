# 校园集市 - 后端文档

> 北京交通大学 "专业课程综合实训" —— 校内一体化人才供需与科创协作平台
>
> 学生端小程序（规划中） + Vue3 Web 管理端 + SpringBoot 后端

---

## 给后端开发人员

### 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17 | |
| Spring Boot | 2.7.18 | |
| MyBatis-Plus | 3.5.3.1 | LambdaQueryWrapper，无 XML |
| MySQL | 8.0 | |
| JWT | jjwt 0.9.1 | 登录鉴权 |
| Lombok | latest | |

### 项目结构（只列出源码）

```
src/main/java/com/bjtumarket/
├── MarketApplication.java              # 启动入口
├── config/
│   ├── CorsConfig.java                # 跨域
│   ├── WebConfig.java                 # uploads 静态资源映射
│   ├── Knife4jConfig.java             # Swagger API 文档
│   ├── LoginInterceptor.java          # JWT 拦截器
│   └── InterceptorConfig.java         # 拦截注册（排除 /api/auth/**）
├── util/
│   └── JwtUtil.java                   # JWT 签发/校验
├── entity/
│   ├── User.java / Job.java / Resume.java / Delivery.java
│   ├── ResearchProject.java / ResearchApplication.java
│   └── Internship.java / InternshipLog.java
├── mapper/
│   ├── UserMapper.java / JobMapper.java / ResumeMapper.java / DeliveryMapper.java
│   ├── ResearchProjectMapper.java / ResearchApplicationMapper.java
│   └── InternshipMapper.java / InternshipLogMapper.java
├── service/
│   ├── UserService.java / JobService.java / ResumeService.java
│   ├── DeliveryService.java   # buildResult() 附带简历+岗位信息
│   ├── AdminService.java      # 企业审核 + 数据统计
│   ├── ResearchService.java   # 科研项目选人
│   └── InternshipService.java # 实习全过程管理
├── controller/
│   ├── AuthController.java            # POST login(返回JWT) / register
│   ├── JobController.java             # CRUD + 分页列表
│   ├── ResumeController.java          # GET detail / POST save
│   ├── DeliveryController.java        # apply / my / publisher / status (支持筛选)
│   ├── AdminController.java           # 企业审核 / 6个stats接口
│   ├── FileController.java            # PDF/Word 上传
│   ├── ResearchProjectController.java # 科研项目CRUD + 申请审核
│   └── InternshipController.java      # 实习入职/日志/评分/证明
└── vo/
    ├── Result.java                    # {code, message, data}
    ├── LoginRequest.java / RegisterRequest.java
    └── LoginUser.java                 # {token, user}
```

### 当前进度一览

| 模块 | 进度 |
|------|------|
| JWT 登录鉴权 | ✅ |
| 多角色注册（学生/企业待审/教师） | ✅ |
| 岗位 CRUD + 分页搜索 | ✅ |
| 简历在线编辑 + 文件上传 | ✅ |
| 投递流程（5 状态 + 防重复） | ✅ |
| 企业入驻审核 | ✅ |
| 数据大屏（概览/专业率/趋势/Top5） | ✅ |
| 智能初筛（GPA/专业/技能筛选） | ✅ |
| 实习全过程管理（入职→日志→评分→证明→统计） | ✅ |
| 科研项目选人 | ✅ |
| 竞赛组队 | ✅ |
| 全局异常处理 / 短信 / 定时任务 / COS | ❌ |

 详见 `进度管理.md`（47 项任务逐项状态 + 待补充空行）。

### 下一步开发建议（按优先级）

1. **全局异常处理**（Phase 1.5）—— `@ControllerAdvice` 统一拦截
2. **COS 对接**（Phase 7.2）—— 文件上传迁移云存储
3. **实名认证 / 短信 / 定时任务**（Phase P2）—— 安全与基础设施

### 启动

```bash
cd backend
mysql -u root -p -e "source sql/init.sql; source sql/research.sql; source sql/internship.sql; USE campus_market; INSERT INTO t_user (username,password,user_type,teacher_no,status) VALUES ('admin',MD5(MD5('123456')),3,'T001',1);"
# 修改 application.yml 中你的 MySQL 密码
mvn spring-boot:run
```

---

## 给前端开发人员

### 后端地址

| 环境 | 地址 |
|------|------|
| 开发 | `http://localhost:8080` |
| 前端 Vite 代理 | `/api/*` → 8080，`/uploads/*` → 8080 |

### 鉴权方式

```
登录 → 拿到 token → 每次请求带 Authorization: Bearer <token>
```

- 无需鉴权：`POST /api/auth/login`、`POST /api/auth/register`
- 需要鉴权：其余所有 `/api/**` 接口
- 教师专属：`/api/admin/**` 额外要求 `userType == 3`
- token 有效期：24 小时
- 上传文件时：`el-upload` 需通过 `:headers="{ Authorization: 'Bearer ' + token }"` 传递

### 统一响应格式

```json
// 成功
{ "code": 200, "message": "success", "data": <具体数据> }

// 登录成功
{ "code": 200, "data": { "token": "eyJ...", "user": { "id":1, "username":"admin", "userType":3, ... } } }

// 错误
{ "code": 500, "message": "错误描述" }
// 或
{ "code": 401, "message": "用户名或密码错误" }
```

### 全部接口速查（46 个）

#### 认证 —— `/api/auth`
```
POST /api/auth/login         { username, password(MD5前端加密), userType }  →  { token, user }
POST /api/auth/register      { username, password, userType, ... }          →  { code, message }
```

#### 岗位 —— `/api/job`
```
GET    /api/job/list?page=1&size=10&jobType=1&keyword=Java  →  { records[], total, pages }
GET    /api/job/detail/{id}                                     →  Job 对象
POST   /api/job/publish      { title, jobType, location, ... }  →  发布成功/失败
PUT    /api/job/update        { id, title, ... }                  →  更新（仅限自己的）
DELETE /api/job/delete/{id}                                      →  删除（仅限自己的）
GET    /api/job/my?page=1&size=10                                →  我发布的岗位
```

#### 简历 —— `/api/resume`
```
GET    /api/resume/detail     →  Resume 对象（JWT 自动识别用户）
POST   /api/resume/save       { name, major, skills, fileUrl, ... }  →  保存成功
```

#### 投递 —— `/api/delivery`
```
POST   /api/delivery/apply?jobId={id}                          →  投递（防重复，需先有简历）
GET    /api/delivery/my?page=1&size=10                          →  我的投递 [{id, jobTitle, status, hrNote, createTime}]
GET    /api/delivery/publisher?page=1&size=10&gpaMin=&major=&skillTag=  →  我收到的投递（支持筛选）
GET    /api/delivery/job/{jobId}?page=1&size=10&gpaMin=&major=&skillTag=  →  某岗位投递（支持筛选）
PUT    /api/delivery/status?deliveryId={id}&deliveryStatus={0-4}&note={备注}  →  更新状态
```

`deliveryStatus` 状态流转：`0待查看 → 1已查看 → 2面试中 → 3已录用↔可撤回 / 4已拒绝↔可重开`

#### 科研 —— `/api/research`
```
POST   /api/research/project/publish       教师发布科研项目
GET    /api/research/project/list?page=&size=&keyword=  项目广场
GET    /api/research/project/{id}          项目详情
POST   /api/research/apply?projectId=&note=  学生申请加入
POST   /api/research/application/audit?applicationId=&status=  教师审核(1通过/2驳回)
GET    /api/research/project/{id}/applications  某项目申请列表
GET    /api/research/my/applications       我的申请
GET    /api/research/my/projects           我发布的项目
```

#### 竞赛组队 —— `/api/competition`
```
POST   /api/competition/publish               发布组队招募
GET    /api/competition/list?page=&size=&keyword=&skillTag=  组队广场
GET    /api/competition/{id}                  队伍详情
POST   /api/competition/apply?teamId=&note=   申请入队
POST   /api/competition/audit?applicationId=&status=  队长审核
GET    /api/competition/team/{teamId}/applications  队伍申请列表
GET    /api/competition/my/applications       我的申请
GET    /api/competition/my/teams              我发布的队伍
POST   /api/competition/team/{teamId}/message?content=  发送组队消息
GET    /api/competition/team/{teamId}/messages  查看组队消息
```

#### 实习 —— `/api/internship`
```
POST   /api/internship/start?deliveryId=          学生发起实习（仅已录用投递）
GET    /api/internship/my                         查看全部实习记录
POST   /api/internship/log?internshipId=&weekNum=&content=  提交周日志
GET    /api/internship/logs?internshipId=         查看日志列表
PUT    /api/internship/end?internshipId=          结束实习
PUT    /api/internship/rate?internshipId=&rating=&review=  企业评分(1-5)
GET    /api/internship/certificate/{id}           实习证明数据
GET   /api/internship/publisher                  企业查看实习生
POST  /api/internship/rate/student?internshipId=&rating=&review=  学生对实习企业评价
POST  /api/internship/{internshipId}/message?content=  发送实习沟通消息
GET   /api/internship/{internshipId}/messages     查看实习消息列表
```

#### 会员 —— `/api/member`
```
GET   /api/member/level       查询当前会员等级
PUT   /api/member/toggle      切换VIP状态（0↔1，测试用）
GET   /api/member/alerts      VIP专属提醒（最新3个岗位）
```

#### 成长足迹 —— `/api/profile`
```
GET   /api/profile/timeline   个人时间线（聚合实习+科研+竞赛履历）
```

#### 管理员 —— `/api/admin`（仅教师 userType=3）
```
GET   /api/admin/enterprise/list?page=&size=&status=&keyword=  企业列表
PUT   /api/admin/enterprise/audit/{id}?status=1                审核（1通过/2拒绝）
GET   /api/admin/stats/overview        → {studentCount, enterpriseCount, jobCount, deliveryCountThisMonth, internshipRate, internshipTotal, internshipActive}
GET   /api/admin/stats/major           → {majors: [{major, studentCount, acceptedCount, rate}]}
GET   /api/admin/stats/trend           → {trend: [{date, count}]}  近7天
GET   /api/admin/stats/top-enterprises → {enterprises: [{rank, name, jobCount, applicantCount}]}
GET   /api/admin/stats/hot-jobs        → {jobs: [{rank, title, company, applicants}]}
GET   /api/admin/stats/internship      → {totalInternships, activeInternships, majorDistribution, topCompanies}
GET   /api/admin/stats/enterprise-rating → {totalReviews, averageRating}
GET   /api/admin/stats/stale-jobs    → {staleJobs: [...], count}  超14天无人投递的岗位
```

#### 文件 —— `/api/file`
```
POST  /api/file/upload        multipart/form-data, file字段  → 文件URL（PDF/Word，≤5MB）
```

### 状态码速查

| 字段 | 值 | 含义 |
|------|----|------|
| userType | 1 / 2 / 3 | 学生 / 企业 / 教师 |
| jobType | 1 / 2 / 3 | 实习 / 全职 / 科研助理 |
| deliveryStatus | 0→1→2→3↔4 | 待查看→已查看→面试中→已录用(可撤回)/已拒绝(可重开) |
| userStatus | 0 / 1 / 2 | 待审核 / 正常 / 已拒绝 |
| internship status | 0 / 1 / 2 | 进行中 / 已完成 / 提前终止 |
| memberLevel | 0 / 1 | 免费 / VIP |
| cooperationType | 1 / 2 | 深度合作 / 校外普通 |

---

## 数据库表

| 表 | 说明 | 核心字段 |
|----|------|---------|
| `t_user` | 用户 | username, password(MD5), user_type, status(企业0待审), company_name |
| `t_job` | 岗位 | title, job_type, publisher_id, status, view_count, delivery_count |
| `t_resume` | 简历 | user_id(唯一), name, major, gpa, skills, file_url |
| `t_delivery` | 投递 | job_id, resume_id, status(0-4), hr_note |
| `t_research_project` | 科研项目 | title, description, requirement, publisher_id, status |
| `t_research_application` | 科研申请 | project_id, student_id, status(0-2), note |
| `t_internship` | 实习 | student_id, job_id, delivery_id, status(0-2), rating, review |
| `t_internship_log` | 实习日志 | internship_id, week_num, content |
| `t_internship_student_review` | 学生评企业 | internship_id, student_id, rating, review |
| `t_team_message` | 组队消息 | team_id, sender_id, content |
| `t_internship_message` | 实习消息 | internship_id, sender_id, content |
| `t_competition_team` | 竞赛队伍 | title, competition_name, leader_id, max_members, status |
| `t_team_application` | 入队申请 | team_id, student_id, status(0-2), note |

建表脚本：`sql/init.sql` / `sql/research.sql` / `sql/internship.sql` / `sql/message.sql` / `sql/supplement.sql` / `sql/competition.sql`

---

## 成员

hide