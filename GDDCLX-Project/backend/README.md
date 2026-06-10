# GDDCLX 后端代码导航

> 想找某个功能改哪里？看这个文档就知道去哪些 Java 文件找。

---

## 目录

| 功能模块 | 说明 |
|---------|------|
| [考勤打卡](#考勤打卡) | 上下班打卡、日/周/月考勤统计 |
| [员工管理 & 登录注册](#员工管理--登录注册) | 登录、注册、员工 CRUD |
| [任务管理](#任务管理) | 任务发布、提交、审核、看板、文件上传 |
| [绩效考核](#绩效考核) | 绩效计算、迟到早退扣款 |
| [岗位管理](#岗位管理) | 岗位分配、统计 |
| [私信消息](#私信消息) | 员工间私信收发 |
| [AI 助手](#ai-助手) | Coze AI 对话代理 |
| [AI 数据接口](#ai-数据接口) | 为 AI Agent 提供 HR 数据 |
| [定时任务](#定时任务) | 自动标记早退 |
| [全局配置](#全局配置) | CORS、文件上传路径 |
| [已废弃](#已废弃) | 脚手架代码，不再使用 |

---

## 考勤打卡

**功能：** 员工上下班打卡、迟到/早退判断、日/周/月统计

| 文件 | 作用 |
|------|------|
| [controller/AttendanceController.java](demo/src/main/java/com/example/demo/controller/AttendanceController.java) | 6个接口：打卡签到、签退、今日公司概览、今日个人状态、周报、月报 |
| [service/AttendanceService.java](demo/src/main/java/com/example/demo/service/AttendanceService.java) | 接口定义 |
| [service/impl/AttendanceServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/AttendanceServiceImpl.java) | 核心逻辑：防重复打卡、迟到(>9:00)/早退(<17:00)判定、周连续天数/等级、月出勤率 |
| [domain/Attendance.java](demo/src/main/java/com/example/demo/domain/Attendance.java) | 实体：考勤记录表 |
| [mapper/AttendanceMapper.java](demo/src/main/java/com/example/demo/mapper/AttendanceMapper.java) | SQL 映射（XML方式） |
| [dto/CheckinRequest.java](demo/src/main/java/com/example/demo/dto/CheckinRequest.java) | 签到请求 |
| [dto/CheckinResponse.java](demo/src/main/java/com/example/demo/dto/CheckinResponse.java) | 签到响应 |
| [dto/CheckoutRequest.java](demo/src/main/java/com/example/demo/dto/CheckoutRequest.java) | 签退请求 |
| [dto/CheckoutResponse.java](demo/src/main/java/com/example/demo/dto/CheckoutResponse.java) | 签退响应 |
| [dto/TodayStatusResponse.java](demo/src/main/java/com/example/demo/dto/TodayStatusResponse.java) | 今日打卡状态 |
| [dto/WeekAttendanceResponse.java](demo/src/main/java/com/example/demo/dto/WeekAttendanceResponse.java) | 周统计 |
| [dto/MonthAttendanceResponse.java](demo/src/main/java/com/example/demo/dto/MonthAttendanceResponse.java) | 月统计 |
| [dto/CompanyAttendanceResponse.java](demo/src/main/java/com/example/demo/dto/CompanyAttendanceResponse.java) | 公司整体考勤 |

---

## 员工管理 & 登录注册

**功能：** 员工登录、注册、管理员管理员工档案

| 文件 | 作用 |
|------|------|
| [controller/EmployeeController.java](demo/src/main/java/com/example/demo/controller/EmployeeController.java) | `/api/login` 登录、`/api/register` 注册 |
| [controller/EmployeeArchiveController.java](demo/src/main/java/com/example/demo/controller/EmployeeArchiveController.java) | `/api/employee/list` 列表、创建、修改、删除员工 |
| [service/EmployeeService.java](demo/src/main/java/com/example/demo/service/EmployeeService.java) | 登录注册接口 |
| [service/impl/EmployeeServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/EmployeeServiceImpl.java) | 登录：明文密码比对 + Base64 Token；注册：默认部门/岗位 |
| [service/EmployeeArchiveService.java](demo/src/main/java/com/example/demo/service/EmployeeArchiveService.java) | 档案管理接口 |
| [service/impl/EmployeeArchiveServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/EmployeeArchiveServiceImpl.java) | 员工 CRUD，含参数校验 + 业务异常 |
| [domain/Employee.java](demo/src/main/java/com/example/demo/domain/Employee.java) | 实体：员工表（核心） |
| [mapper/EmployeeMapper.java](demo/src/main/java/com/example/demo/mapper/EmployeeMapper.java) | SQL 映射（XML方式） |
| [dto/LoginRequest.java](demo/src/main/java/com/example/demo/dto/LoginRequest.java) | 登录请求 |
| [dto/LoginResponse.java](demo/src/main/java/com/example/demo/dto/LoginResponse.java) | 登录响应（含 token + 用户信息） |
| [dto/RegisterRequest.java](demo/src/main/java/com/example/demo/dto/RegisterRequest.java) | 注册请求 |
| [dto/EmployeeCreateRequest.java](demo/src/main/java/com/example/demo/dto/EmployeeCreateRequest.java) | 创建员工请求 |
| [dto/EmployeeUpdateRequest.java](demo/src/main/java/com/example/demo/dto/EmployeeUpdateRequest.java) | 修改员工请求 |
| [dto/EmployeeDeleteRequest.java](demo/src/main/java/com/example/demo/dto/EmployeeDeleteRequest.java) | 删除员工请求 |
| [dto/EmployeeListResponse.java](demo/src/main/java/com/example/demo/dto/EmployeeListResponse.java) | 员工列表项 |

---

## 任务管理

**功能：** 任务发布、多栏看板、文件附件、员工提交、管理员审核、关闭/删除

| 文件 | 作用 |
|------|------|
| [controller/TaskController.java](demo/src/main/java/com/example/demo/controller/TaskController.java) | 12个接口：创建、列表、详情、关闭、删除、提交、审核、我的提交等 |
| [service/TaskService.java](demo/src/main/java/com/example/demo/service/TaskService.java) | 接口定义 |
| [service/impl/TaskServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/TaskServiceImpl.java) | 核心逻辑：文件上传(UUID重命名)、状态机(new→pending→approved/rejected)、级联删除、事务管理 |
| [domain/Task.java](demo/src/main/java/com/example/demo/domain/Task.java) | 实体：任务表 |
| [domain/TaskFile.java](demo/src/main/java/com/example/demo/domain/TaskFile.java) | 实体：任务附件表 |
| [domain/TaskSubmission.java](demo/src/main/java/com/example/demo/domain/TaskSubmission.java) | 实体：任务提交记录表 |
| [domain/SubmissionFile.java](demo/src/main/java/com/example/demo/domain/SubmissionFile.java) | 实体：提交附件表 |
| [mapper/TaskMapper.java](demo/src/main/java/com/example/demo/mapper/TaskMapper.java) | 任务 SQL（注解方式） |
| [mapper/TaskFileMapper.java](demo/src/main/java/com/example/demo/mapper/TaskFileMapper.java) | 任务附件 SQL |
| [mapper/TaskSubmissionMapper.java](demo/src/main/java/com/example/demo/mapper/TaskSubmissionMapper.java) | 任务提交 SQL |
| [mapper/SubmissionFileMapper.java](demo/src/main/java/com/example/demo/mapper/SubmissionFileMapper.java) | 提交附件 SQL |
| [dto/TaskResponse.java](demo/src/main/java/com/example/demo/dto/TaskResponse.java) | 任务详情响应 |
| [dto/SubmissionResponse.java](demo/src/main/java/com/example/demo/dto/SubmissionResponse.java) | 提交记录响应 |

---

## 绩效考核

**功能：** 绩效计算（底薪+提成+出勤率）、迟到早退扣款确认/豁免

| 文件 | 作用 |
|------|------|
| [controller/PerformanceController.java](demo/src/main/java/com/example/demo/controller/PerformanceController.java) | 5个接口：绩效列表、确认迟到扣款、确认早退扣款、豁免迟到、豁免早退 |
| [service/PerformanceService.java](demo/src/main/java/com/example/demo/service/PerformanceService.java) | 接口定义 |
| [service/impl/PerformanceServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/PerformanceServiceImpl.java) | 核心公式：`提成 = 底薪 × 岗位数 × 0.5 × (出勤率/100)`；底薪按工号分段(8000/6000/5000)；扣款+50/豁免-50 |
| [dto/PerformanceResponse.java](demo/src/main/java/com/example/demo/dto/PerformanceResponse.java) | 绩效数据响应 |
| [dto/ConfirmDeductionRequest.java](demo/src/main/java/com/example/demo/dto/ConfirmDeductionRequest.java) | 扣款确认/豁免请求 |

---

## 岗位管理

**功能：** 岗位分配、岗位统计、批量更新岗位

| 文件 | 作用 |
|------|------|
| [controller/PositionController.java](demo/src/main/java/com/example/demo/controller/PositionController.java) | 3个接口：分页列表(支持搜索)、岗位统计、批量更新 |
| [service/PositionService.java](demo/src/main/java/com/example/demo/service/PositionService.java) | 接口定义 |
| [service/impl/PositionServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/PositionServiceImpl.java) | 内存分页、岗位统计、JSON序列化岗位列表 |
| [dto/PositionListResponse.java](demo/src/main/java/com/example/demo/dto/PositionListResponse.java) | 岗位列表项 |
| [dto/PositionListPageResponse.java](demo/src/main/java/com/example/demo/dto/PositionListPageResponse.java) | 分页响应 |
| [dto/PositionStatsResponse.java](demo/src/main/java/com/example/demo/dto/PositionStatsResponse.java) | 岗位统计 |
| [dto/PositionUpdateRequest.java](demo/src/main/java/com/example/demo/dto/PositionUpdateRequest.java) | 岗位更新请求 |

---

## 私信消息

**功能：** 员工之间发送私信，已读/未读状态，红点提醒

| 文件 | 作用 |
|------|------|
| [controller/MessageController.java](demo/src/main/java/com/example/demo/controller/MessageController.java) | 7个接口：发送、收件箱、发件箱、详情、已读、未读数、删除 |
| [service/MessageService.java](demo/src/main/java/com/example/demo/service/MessageService.java) | 接口定义 |
| [service/impl/MessageServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/MessageServiceImpl.java) | 纯透传 Mapper，无额外逻辑 |
| [domain/PrivateMessage.java](demo/src/main/java/com/example/demo/domain/PrivateMessage.java) | 实体：私信表 |
| [mapper/PrivateMessageMapper.java](demo/src/main/java/com/example/demo/mapper/PrivateMessageMapper.java) | SQL（注解方式） |

---

## AI 助手

**功能：** 前端聊天请求 → 后端代理转发到 Coze AI API（SSE 流式响应）

| 文件 | 作用 |
|------|------|
| [controller/CozeController.java](demo/src/main/java/com/example/demo/controller/CozeController.java) | `/api/coze/chat` — 接收用户问题，返回 AI 回复 |
| [service/CozeService.java](demo/src/main/java/com/example/demo/service/CozeService.java) | 接口定义 |
| [service/impl/CozeServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/CozeServiceImpl.java) | HTTP 代理：调用 Coze API v3，解析 SSE 流 |
| [dto/CozeChatRequest.java](demo/src/main/java/com/example/demo/dto/CozeChatRequest.java) | 聊天请求 |

---

## AI 数据接口

**功能：** 为 Coze AI Agent 提供全公司 HR 数据（员工、考勤、绩效、岗位统计）

| 文件 | 作用 |
|------|------|
| [controller/AgentDataController.java](demo/src/main/java/com/example/demo/controller/AgentDataController.java) | `/api/agent/data` — 一次性返回公司概览、员工列表、今日考勤、绩效、岗位统计 |

---

## 定时任务

**功能：** 每日凌晨自动标记未签退员工为早退

| 文件 | 作用 |
|------|------|
| [task/AttendanceScheduler.java](demo/src/main/java/com/example/demo/task/AttendanceScheduler.java) | `@Scheduled(cron = "0 0 1 * * *")` — 每天凌晨1点执行 |

---

## 全局配置

**功能：** CORS 跨域、文件上传路径映射

| 文件 | 作用 |
|------|------|
| [config/CorsConfig.java](demo/src/main/java/com/example/demo/config/CorsConfig.java) | 全局 CORS：允许所有来源、所有方法、所有头 |
| [config/WebConfig.java](demo/src/main/java/com/example/demo/config/WebConfig.java) | 静态资源映射：`/uploads/**` → 本地文件目录 |
| [DemoApplication.java](demo/src/main/java/com/example/demo/DemoApplication.java) | Spring Boot 启动类（端口8080、Nacos、定时任务、Mapper扫描） |

---

## 已废弃

> ⚠️ 以下文件是 Spring Initializr 脚手架生成的老代码，**前端不再使用**，改功能不用看这些。

| 文件 | 说明 |
|------|------|
| [controller/UserController.java](demo/src/main/java/com/example/demo/controller/UserController.java) | 老用户 CRUD（被 EmployeeArchiveController 替代） |
| [service/UserService.java](demo/src/main/java/com/example/demo/service/UserService.java) | 老用户服务接口 |
| [service/impl/UserServiceImpl.java](demo/src/main/java/com/example/demo/service/impl/UserServiceImpl.java) | 老用户服务实现 |
| [domain/User.java](demo/src/main/java/com/example/demo/domain/User.java) | 老用户实体 |
| [mapper/UserMapper.java](demo/src/main/java/com/example/demo/mapper/UserMapper.java) | 老用户 Mapper |
| [demos/web/BasicController.java](demo/src/main/java/com/example/demo/demos/web/BasicController.java) | 脚手架 Demo |
| [demos/web/PathVariableController.java](demo/src/main/java/com/example/demo/demos/web/PathVariableController.java) | 脚手架 Demo |
| [demos/web/User.java](demo/src/main/java/com/example/demo/demos/web/User.java) | 脚手架 Demo POJO |

---

## 快速查找

按关键词快速定位：

| 你想改... | 去这些文件 |
|-----------|-----------|
| 打卡逻辑/迟到判断 | `AttendanceServiceImpl` → `AttendanceController` |
| 登录/注册/Token | `EmployeeServiceImpl` → `EmployeeController` |
| 员工增删改查 | `EmployeeArchiveServiceImpl` → `EmployeeArchiveController` |
| 任务创建/看板 | `TaskServiceImpl` → `TaskController` → `Task`/`TaskFile` domain |
| 任务提交/审核 | `TaskServiceImpl`（submitTask/reviewSubmission）→ `TaskSubmission`/`SubmissionFile` domain |
| 绩效公式/扣款 | `PerformanceServiceImpl` → `PerformanceController` |
| 岗位分配 | `PositionServiceImpl` → `PositionController` |
| 私信收发 | `MessageServiceImpl` → `MessageController` → `PrivateMessageMapper` |
| AI 对话代理 | `CozeServiceImpl`（SSE解析）→ `CozeController` |
| 给 AI 喂数据 | `AgentDataController` |
| 定时任务 | `AttendanceScheduler` |
| CORS 跨域 | `CorsConfig` |
| 文件上传路径 | `WebConfig` + `TaskServiceImpl`（uploadDir） |
