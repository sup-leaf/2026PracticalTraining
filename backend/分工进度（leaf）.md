# leaf - 个人分工进度

> 日期：2026-05-21
> 角色：leaf（后端核心开发者）
> 参考：分工安排（二人版）.md · 进度管理.md

---

## 一、P1 任务完成情况

### 3.9 智能初筛 ✅ 已完成

在教师/企业端投递列表增加按 GPA 最低分、专业、技能标签筛选。

| 层面 | 实现 |
|------|------|
| 后端 | `DeliveryController` `/publisher` 和 `/job/{jobId}` 加 `gpaMin`/`major`/`skillTag` 三个可选参数；`DeliveryServiceImpl.buildResult()` 内新增 `matchFilter()` 方法，利用已有的简历缓存数据进行内存过滤 |
| 前端 | `Deliveries.vue` 表格上方加筛选栏（GPA ≥ / 专业 / 技能标签 输入框 + 筛选按钮）；展开详情区新增 GPA 显示 |

---

### 6.1~6.5 实习全过程管理 ✅ 已完成

为岗位招聘闭环增加从"录用→实习→日志→评分→证明→统计"的完整生命周期。

#### 6.1 实习入职报备 ✅

- `POST /api/internship/start?deliveryId=` —— 学生从已录用(status=3)投递发起实习
- 自动关联简历、岗位、投递记录；一个学生同时只能有一个进行中实习
- 前端：实习管理页"我的实习"tab → 展示已录用投递列表 → 点"发起实习"

#### 6.2 实习日志提交 ✅

- `POST /api/internship/log` —— 学生按周提交日志（weekNum + content）
- `GET /api/internship/logs?internshipId=` —— 按周数升序查看所有日志
- 前端：卡片式日志展示，突出周标签和时间戳

#### 6.3 企业/导师评分 ✅

- `PUT /api/internship/rate?internshipId=&rating=&review=` —— 企业/教师对实习评分(1-5)
- **修复过**：原限制仅岗位发布者可评分 → 改为所有企业(2)/教师(3)均可评分
- 前端：企业端"实习生列表"tab → 点"评价" → el-rate 评分组件 + 评语输入

#### 6.4 电子实习证明 ✅

- `GET /api/internship/certificate/{id}` —— 综合简历、岗位、实习记录返回结构化 JSON
- 前端：选择任意一段实习（下拉框）→ 生成证明数据展示

#### 6.5 全院实习统计 ✅

- `GET /api/admin/stats/internship` —— 总人次、进行中、各专业分布(人数+均分)、Top 实习企业
- 整合进 `DataScreen.vue`（人才供需大屏第二行统计卡片）

---

### 额外完成的工作

| 事项 | 说明 |
|------|------|
| 投递状态撤回 | 已录用(3) → 取消录用退回面试中(2)；已拒绝(4) → 重新打开退回已查看(1) |
| 实习历史列表 | `GET /api/internship/my` 改为返回全部实习记录（按时间倒序），前端卡片列表展示 |
| 代码注释 | `InternshipServiceImpl.java` 和 `InternshipController.java` 每个方法加 Javadoc |
| 前端 code-check | 所有实习相关 API 调用加 `res.code === 200` 判断，防止静默失败 |
| 响应式角色 | `Internship.vue` 按 userType 默认显示对应 tab（学生→我的实习、企业→实习生列表、教师→实习统计） |

---

## 二、新建/修改文件清单

### 新建（9 后端 + 1 前端）

| 文件 | 说明 |
|------|------|
| `sql/internship.sql` | `t_internship` / `t_internship_log` 两张表 |
| `entity/Internship.java` | 实习记录实体 |
| `entity/InternshipLog.java` | 实习日志实体 |
| `mapper/InternshipMapper.java` | 含 4 个统计 @Select |
| `mapper/InternshipLogMapper.java` | BaseMapper |
| `service/InternshipService.java` | 接口 |
| `service/impl/InternshipServiceImpl.java` | 实现（含注释） |
| `controller/InternshipController.java` | 8 个 REST 端点（含注释） |
| `views/Internship.vue` | 前端三角色实习管理页面 |
| `5.21修改记录.txt` | 当日修改记录 |

### 修改（11 个）

| 文件 | 改动 |
|------|------|
| `service/DeliveryService.java` | 加筛选参数 |
| `service/impl/DeliveryServiceImpl.java` | 加 gpa/major/skillTag 过滤逻辑 |
| `controller/DeliveryController.java` | 加筛选 @RequestParam |
| `service/AdminService.java` | 加 `internshipStats()` |
| `service/impl/AdminServiceImpl.java` | 注入 InternshipMapper，实现统计 |
| `controller/AdminController.java` | 加 `GET /api/admin/stats/internship` |
| `api/index.js` | 加 8 个 internship API + 筛选参数支持 |
| `router/index.js` | 加 `/home/internship` 路由 |
| `views/Home.vue` | 三角色菜单加"实习管理"入口 + Collection 图标 |
| `views/Deliveries.vue` | 筛选栏 + 录用/取消录用/重新打开按钮 |
| `views/DataScreen.vue` | 加实习统计卡片行 |

---

## 三、P2 待完成

| 编号 | 事项 | 说明 |
|------|------|------|
| 1.5 | 全局异常处理 | `@ControllerAdvice` 统一拦截返回 Result.error |
| 7.2 | 腾讯云 COS | 替换 FileController 本地存储 → COS |
| 10.3 | 接口压力测试 | JMeter 或脚本，50/100/200 并发 |

---

## 四、已知可优化项

| 优化点 | 现状 | 建议 |
|--------|------|------|
| 密码安全 | 双重 MD5 无 salt | 改为 BCrypt |
| 文件存储 | 本地磁盘 | 对接 COS（P2 计划内） |
| 分页准确性 | 智能初筛在内存中过滤，total 为过滤后大小而非数据库总数 | 可接受，数据量不大 |
| 实习证明 | JSON 格式，无 PDF 生成 | 后续可集成 PDF 模板引擎 |
| 教师账号 | 需手动 SQL 插入 | 可加初始化脚本或注册开关 |
| 实习日志附件 | 当前仅支持文本 | 可扩展支持图片上传 |
