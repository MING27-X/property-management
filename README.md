# 智慧物业社区管理系统

> 基于 Java 技术的智慧物业社区管理系统的设计与实现
> 技术栈：Java 8 + Spring 5 + SpringMVC + MyBatis + Druid + MySQL 8 + Thymeleaf + Apache Tomcat 9

一个面向小区物业日常业务的 B/S 管理系统，覆盖「小区基础档案 → 业主入住 → 收费 → 报修 → 投诉 → 公告 → 访客 → 停车」的完整业务闭环，
并按角色区分**系统管理员 / 物业员工 / 业主**三种使用视角，业主可以登录自助端查询账单、缴纳费用、提交报修与投诉。

---

## 一、功能模块

| 模块 | 主要功能 | 使用角色 |
| --- | --- | --- |
| 登录与权限 | 账号密码登录（MD5 加密）、会话拦截、按角色控制菜单与访问权限 | 全部 |
| 首页概览 | 楼栋/房屋/业主/报修/投诉/收费/访客/停车统计，近 6 个月收费柱状图、缴费完成率、工单状态分布、最新公告与报修时间线 | 全部（业主为个人视图） |
| 楼栋管理 | 楼栋档案增删改查、分页与关键字查询、下辖房屋数量统计、有房屋时禁止删除 | 管理员、物业员工 |
| 房屋管理 | 房号/楼层/面积/类型/入住状态维护，按楼栋与状态筛选；状态改为「空置」时自动解除业主绑定（业主档案保留） | 管理员、物业员工 |
| 业主管理 | 业主档案登记、与房屋一对一绑定（自动把房屋置为「已入住」）、解除绑定后房屋恢复「空置」、可关联业主登录账号 | 管理员、物业员工 |
| 收费管理 | 物业费/水费/电费/停车费账单，按周期与状态筛选，缴费核销（记录缴费时间与方式）、本月应收/实收/欠费统计 | 物业员工维护；业主可查询本人账单并缴费 |
| 报修管理 | 业主提交报修 → 物业派单（处理中）→ 处理完成（已完成），工单详情页含故障描述、处理说明与处理人 | 业主提交；物业派单处理 |
| 投诉建议 | 业主提交投诉/建议 → 物业回复，状态自动流转为「已回复」 | 业主提交；物业回复 |
| 公告通知 | 公告/通知/紧急三类，支持草稿与撤回、浏览量统计、公告详情与最新公告列表 | 物业发布；业主查看已发布公告 |
| 访客登记 | 访客登记、确认到访、登记离开、按状态筛选、今日访客统计 | 管理员、物业员工 |
| 停车位管理 | 车位档案、租用办理（自动记录租期一年）、退租办理、月租金收入统计 | 管理员、物业员工 |
| 用户管理 | 系统账号增删改、角色分配、重置密码为 123456、启用/禁用 | 仅系统管理员 |
| 个人中心 | 查看账号信息、修改姓名手机号、修改登录密码 | 全部 |

### 角色权限矩阵

| 功能 | ADMIN | STAFF | OWNER |
| --- | :---: | :---: | :---: |
| 首页概览 | 小区总览 | 小区总览 | 个人视图 |
| 用户管理（账号 / 角色 / 重置密码 / 启停） | ✅ | ❌ | ❌ |
| 楼栋 / 房屋 / 业主 / 停车位 | 新增 · 修改 · **删除** | 新增 · 修改（**不能删除**） | ❌ |
| 收费管理 | 账单新增 / 修改 / 删除 + 核销 | 查看全部账单 + **缴费核销**（不能新增、修改、删除） | 仅本人账单 + 在线缴费 |
| 报修管理 | 全部工单 + 派单 / 完成 / **删除** | 全部工单 + 派单 / 完成（不能删除） | 仅本人报修 + 提交 |
| 投诉建议 | 全部 + 回复 + **删除** | 全部 + 回复（不能删除） | 仅本人 + 提交 |
| 公告通知 | 发布 / 撤回 / 删除 | 发布 / 撤回 / 删除 | 仅查看已发布 |
| 访客登记 | 登记 / 到访 / 离开 | 登记 / 到访 / 离开 | ❌ |

> 权限设计原则：**物业员工只负责业务流转**（派单处理、回复诉求、访客登记、缴费核销），
> **涉及数据删除与账目增改的动作收归系统管理员**，避免误删与账目纠纷。
> 服务端在 Controller 中逐项校验角色，绕过界面直接发请求同样会被拦截并提示。

---

## 二、运行环境

本机已配置好的环境如下（脚本中的路径与之一致，换机器只需修改 `scripts\*.cmd` 里的路径变量）：

| 组件 | 版本 | 本机路径 |
| --- | --- | --- |
| JDK | 1.8.0_181 | `D:\java\jdk1.8.0_181` |
| Maven | 3.9.9 | `D:\maven\apache-maven-3.9.9` |
| Tomcat | 9.0.118（HTTP 端口 **8888**） | `D:\tomcat9\apache-tomcat-9.0.118` |
| MySQL | 8.0.43（账号 `root` / `123456`） | 服务名 `MySQL80` |
| 构建产物 | `target/property-ms.war` | 部署名 `/property-ms` |

> 注意：Tomcat 的 `conf/server.xml` 中 HTTP 连接器端口为 **8888**（不是 8080），因此访问地址是 `http://localhost:8888/property-ms/`。

---

## 三、在 VSCode 中运行（推荐）

1. 用 VSCode 打开本项目文件夹：`文件 → 打开文件夹`，选择本项目根目录。
2. 首次打开时按提示安装推荐扩展（Java 扩展包、XML、Tomcat 等，见 `.vscode/extensions.json`）。
3. 按 `Ctrl + Shift + B` 或打开「终端 → 运行任务」，依次执行下列任务：

   | 任务名称 | 说明 |
   | --- | --- |
   | `1. 初始化数据库` | 建库建表 + 导入演示数据（会重建 `property_db`） |
   | `2. 构建 WAR 包` | 执行 `mvn -o clean package`，产物在 `target/property-ms.war` |
   | `3. 部署并重启 Tomcat` | 复制 WAR 到 Tomcat 并自动重启服务 |
   | `4. 启动 Tomcat（后台）` | 后台启动，日志写入 `Tomcat\logs\catalina-console.log` |
   | `5. 启动 Tomcat（前台，可看日志）` | 前台启动，关闭窗口即停止 |
   | `6. 停止 Tomcat` | 停止服务 |
   | `7. 页面冒烟测试` | 用三个角色自动访问所有页面并检查是否报错 |

4. 浏览器访问 <http://localhost:8888/property-ms/>

### 不使用 VSCode 任务时，手动命令如下

```bat
rem 1) 初始化数据库
scripts\init-db.cmd

rem 2) 打包（-o 表示离线构建，使用本地已缓存的依赖）
scripts\build.cmd -o clean package

rem 3) 部署并重启 Tomcat（等价于复制 war + 重启）
scripts\deploy.cmd
```

---

## 四、演示账号

| 角色 | 账号 | 密码 | 说明 |
| --- | --- | --- | --- |
| 系统管理员 | `admin` | `123456` | 拥有全部功能 |
| 物业员工 | `wuye01` | `123456` | 客服（王丽），可派单、回复诉求 |
| 物业员工 | `wuye02` | `123456` | 维修（李强） |
| 业主 | `zhangsan` | `123456` | 张三（1号楼 1-101），可查账单、缴费、报修 |
| 业主 | `lisi` / `wangwu` / `zhaoliu` / `sunqi` | `123456` | 其他业主账号 |

---

## 五、项目结构

```
property-ms/
├── db/
│   └── property_db.sql                 # 建库建表 + 演示数据
├── docs/screenshots/                   # 系统界面截图（可直接用于项目文档）
├── scripts/                            # 一键脚本（cmd 脚本保持纯 ASCII，避免 GBK 乱码）
│   ├── init-db.cmd                     # 初始化数据库
│   ├── build.cmd                       # Maven 构建（内部固定 JAVA_HOME）
│   ├── deploy.cmd                      # 构建产物复制到 Tomcat 并重启
│   ├── start-tomcat.cmd                # 后台启动 Tomcat
│   ├── start-tomcat-console.cmd        # 前台启动 Tomcat（看日志）
│   ├── start-tomcat-debug.cmd          # 以调试模式启动（远程调试 8000 端口）
│   ├── stop-tomcat.cmd                 # 停止 Tomcat
│   └── verify-pages.ps1                # 页面冒烟测试脚本
├── src/main/java/com/smartproperty/
│   ├── common/                         # 通用对象（分页 PageResult、图表 ChartItem）
│   ├── config/                         # 全局控制器增强、统一异常处理
│   ├── controller/                      # 控制器（每个业务模块一个）
│   ├── entity/                          # 实体类（与数据库表一一对应）
│   ├── interceptor/                     # 登录 + 角色权限拦截器
│   ├── mapper/                          # MyBatis Mapper 接口
│   ├── service/  + service/impl         # 业务层
│   └── util/                            # MD5、日期工具
├── src/main/resources/
│   ├── jdbc.properties                  # 数据库连接配置
│   ├── mybatis-config.xml               # MyBatis 全局配置
│   ├── spring/applicationContext.xml    # 数据源、MyBatis、事务
│   ├── spring/spring-mvc.xml            # 注解驱动、Thymeleaf、拦截器
│   └── mapper/*.xml                     # SQL 映射文件
├── src/main/webapp/
│   ├── WEB-INF/web.xml
│   ├── WEB-INF/templates/               # Thymeleaf 页面模板
│   └── static/                          # 自研 CSS / JS（不依赖任何 CDN）
└── pom.xml
```

---

## 六、数据库设计

数据库名 `property_db`，字符集 `utf8mb4`，共 10 张业务表：

| 表名 | 说明 | 关键字段 |
| --- | --- | --- |
| `t_user` | 系统用户 | username、password(MD5)、role(ADMIN/STAFF/OWNER)、status |
| `t_building` | 楼栋 | building_no、name、unit_count、floor_count |
| `t_room` | 房屋 | building_id、room_no、floor、area、room_type、status、owner_id |
| `t_owner` | 业主档案 | user_id、name、phone、id_card、room_id（与房屋一对一） |
| `t_fee_bill` | 费用账单 | room_id、owner_id、fee_type、period、amount、status、pay_time、pay_method |
| `t_repair` | 报修工单 | room_id、owner_id、category、title、urgency、status、handler_id、handle_remark |
| `t_complaint` | 投诉建议 | owner_id、type、title、content、status、reply_content、reply_by |
| `t_notice` | 公告通知 | title、content、type、publisher_id、status、view_count |
| `t_visitor` | 访客登记 | visitor_name、phone、room_id、visit_time、leave_time、status |
| `t_parking` | 停车位 | space_no、area、type、status、owner_id、plate_no、monthly_fee、rent_start/end |

表间关系（简化）：

```
t_building 1 ── n t_room 1 ── 1 t_owner        （房屋与业主一对一绑定）
t_owner    1 ── n t_fee_bill / t_repair / t_complaint
t_user     n ── 1 t_owner                       （业主登录账号 ↔ 业主档案）
t_room     1 ── n t_visitor                     （访客访问某套房屋）
t_owner    1 ── n t_parking                     （业主租用车位）
```

---

## 七、关键页面地址

| 功能 | 地址 |
| --- | --- |
| 登录 | `/property-ms/login` |
| 首页概览 | `/property-ms/dashboard` |
| 楼栋 / 房屋 / 业主 | `/building/list`、`/room/list`、`/owner/list` |
| 我的房屋（业主） | `/owner/my` |
| 收费管理 | `/fee/list` |
| 报修管理 / 工单详情 | `/repair/list`、`/repair/detail?id=1` |
| 投诉建议 / 详情 | `/complaint/list`、`/complaint/detail?id=1` |
| 公告通知 / 详情 | `/notice/list`、`/notice/detail?id=1` |
| 访客登记 / 停车位 | `/visitor/list`、`/parking/list` |
| 用户管理（仅管理员） | `/user/list` |
| 个人中心 | `/profile` |

---

## 八、实现要点说明

1. **分层架构**：Controller（接收请求、组织视图数据）→ Service（业务规则、事务）→ Mapper（MyBatis 数据库访问），
   Spring 容器分两个：`applicationContext.xml` 管理 Service/数据源/事务，`spring-mvc.xml` 管理 Controller 与拦截器。
2. **权限控制**：登录成功后用户对象保存在 Session；`LoginInterceptor` 统一拦截未登录请求，
   并按路径前缀限制角色（如 `/user/**` 仅管理员），业务数据范围在各 Controller 中按业主档案过滤。
3. **密码安全**：用户密码以 MD5 存储，工具类 `MD5Util` 负责加密与校验，新增账号与重置密码统一初始化为 `123456`。
4. **业务联动**：业主与房屋绑定/解绑时同步维护房屋入住状态；房屋状态改为「空置」时自动解除该房屋的业主绑定
   （仅清空房屋侧 owner_id，业主档案与登录账号保留，业主管理中「所在房屋」列显示「未绑定」，房屋可重新登记新业主）；
   **姓名双向同步**：登录账号与业主档案关联后，在「用户管理」修改账号姓名会同步更新业主档案，
   在「业主管理」修改档案姓名也会同步更新关联登录账号；
   账单归属业主随所选房屋自动确定；
   报修工单按「待处理 → 处理中 → 已完成」流转并记录处理人与完成时间。
5. **分页查询**：`PageResult` 封装当前页、总条数、总页数与页码列表，Mapper 使用 `LIMIT offset, size` 实现物理分页。
6. **前端实现**：Thymeleaf 模板 + 公共布局片段（`templates/common/layout.html`），样式与脚本全部自研（`static/css`、`static/js`），
   不依赖任何外部 CDN，离线环境也能正常显示；列表页新增/编辑复用同一个滑出式表单面板。

---

## 九、常见问题

**1. 访问 8080 打不开？**
本机 Tomcat 使用的是 8888 端口，请访问 `http://localhost:8888/property-ms/`；端口配置见 `Tomcat/conf/server.xml`。

**2. 启动后页面报数据库连接错误？**
确认 MySQL80 服务已启动，且 `src/main/resources/jdbc.properties` 中的账号密码与本机一致（默认 `root / 123456`）。

**3. Maven 构建失败，提示无法解析依赖？**
本机 Maven 本地仓库已经缓存了项目所需的全部依赖，请使用离线构建：`scripts\build.cmd -o clean package`。

**4. 修改了 `*.cmd` 脚本后出现奇怪的命令报错？**
Windows 的 cmd 按 GBK 解析批处理文件，如果脚本里写入中文会导致乱码并执行异常，因此 `scripts` 下的 `.cmd` 一律保持英文（ASCII）。
如需中文说明，请写在 README 或代码注释中。

**5. 想重置演示数据？**
执行 `scripts\init-db.cmd`（会删除并重建 `property_db`，恢复初始演示数据）。

---

## 十、已验证内容

本项目在交付前已实际运行验证：

- Maven 离线构建成功，WAR 部署到 Tomcat 9 并正常启动；
- `admin` / `wuye01` / `zhangsan` 三种角色登录成功，所有功能页面返回 200 且无模板报错；
- 业务流转实测通过：业主提交报修 → 物业派单 → 处理完成；业主缴费核销；访客登记 → 到访 → 离开；
  车位租用 → 退租；投诉提交 → 物业回复；公告发布 → 撤回 → 删除；账号新增 → 重置密码 → 禁用 → 删除；
  楼栋/房屋新增删除、业主登记后房屋自动置为「已入住」、删除业主后房屋恢复「空置」。
- 房屋「空置」自动解绑联动实测通过（管理员与物业员工触发均验证）：房屋状态改为空置后，
  房屋列表「业主 / 业主电话」列显示为空（仅解除房屋侧绑定），业主档案与登录账号保留、
  业主管理中「所在房屋」列显示「未绑定」；解绑后的房屋可重新登记业主，重新绑定后状态恢复「已入住」；
  三个角色全页面冒烟回归全部通过。

### 自检发现并修复的问题

| # | 类型 | 问题 | 处理 |
| --- | --- | --- | --- |
| 1 | 数据丢失 | 编辑已租用车位时，租用人 / 车牌 / 租期被清空（编辑表单不含这些字段，SQL 却无条件覆盖为 NULL） | 编辑时按状态保留或清除租用信息 |
| 2 | 数据丢失 | 编辑已离开的访客记录时，离开时间被清空 | 按状态维护离开时间 |
| 3 | 数据丢失 | 编辑已缴费账单时，缴费时间被刷新成当前时间 | 保存前取回原缴费时间与方式 |
| 4 | 数据完整性 | 删除房屋 / 业主时未校验关联数据，会产生孤儿记录 | 删除前统计关联的账单、工单、投诉、车位，存在则拒绝并提示明细 |
| 5 | 数据完整性 | 删除系统账号后，业主档案中的绑定账号悬空 | 删除账号时自动解除业主绑定 |
| 6 | 越权 | 业主可直接访问 `/visitor/list`（菜单隐藏但 URL 可达） | 拦截器将 `/visitor/` 限定为 ADMIN、STAFF |
| 7 | 提示错误 | 被禁用的账号登录时提示「账号或密码错误」 | 区分提示「该账号已被禁用，请联系系统管理员」 |
| 8 | 异常体验 | 日期等格式填错时会跳到「系统出现异常」页 | 新增绑定异常处理，友好提示并返回对应列表页 |
| 9 | 统计口径 | 业主访问未发布的草稿公告也会计入浏览量 | 先校验权限，再统计浏览量 |
| 10 | 边界 | 页码超出范围（如 `pageNum=999`）会显示空白页 | 自动夹紧到最后一页 |

安全检查结论：MyBatis 映射全部使用 `#{}` 预编译参数（无 SQL 拼接）；模板全部使用 `th:text`
输出（无 `th:utext`，不存在 XSS）；业主账号对楼栋/房屋/业主/车位/访客/账号管理等写接口的越权请求
全部被拦截，实测前后数据快照一致。
