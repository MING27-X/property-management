-- ============================================================
-- 智慧物业社区管理系统 —— 数据库初始化脚本
-- 数据库：MySQL 8.0   字符集：utf8mb4
-- 执行方式：
--   mysql -uroot -p123456 --default-character-set=utf8mb4 < db/property_db.sql
-- ============================================================

DROP DATABASE IF EXISTS property_db;
CREATE DATABASE property_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE property_db;

-- ------------------------------------------------------------
-- 1. 系统用户表（管理员 / 物业员工 / 业主）
-- ------------------------------------------------------------
CREATE TABLE t_user (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password    VARCHAR(64)  NOT NULL COMMENT '密码（MD5）',
    real_name   VARCHAR(50)  NOT NULL COMMENT '姓名',
    phone       VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    role        VARCHAR(20)  NOT NULL DEFAULT 'OWNER' COMMENT '角色 ADMIN/STAFF/OWNER',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '系统用户表';

-- ------------------------------------------------------------
-- 2. 楼栋表
-- ------------------------------------------------------------
CREATE TABLE t_building (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    building_no VARCHAR(20)  NOT NULL COMMENT '楼栋编号',
    name        VARCHAR(50)  NOT NULL COMMENT '楼栋名称',
    unit_count  INT          NOT NULL DEFAULT 1 COMMENT '单元数',
    floor_count INT          NOT NULL DEFAULT 1 COMMENT '层数',
    remark      VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_building_no (building_no)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '楼栋表';

-- ------------------------------------------------------------
-- 3. 房屋表
-- ------------------------------------------------------------
CREATE TABLE t_room (
    id          INT           NOT NULL AUTO_INCREMENT COMMENT '主键',
    building_id INT           NOT NULL COMMENT '所属楼栋',
    room_no     VARCHAR(30)   NOT NULL COMMENT '房号',
    floor       INT           NOT NULL DEFAULT 1 COMMENT '楼层',
    area        DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '建筑面积(㎡)',
    room_type   VARCHAR(20)   NOT NULL DEFAULT '住宅' COMMENT '房屋类型 住宅/商铺/车库',
    status      VARCHAR(20)   NOT NULL DEFAULT '空置' COMMENT '状态 已入住/空置/装修中',
    owner_id    INT                    DEFAULT NULL COMMENT '业主ID',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room (building_id, room_no)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '房屋表';

-- ------------------------------------------------------------
-- 4. 业主表
-- ------------------------------------------------------------
CREATE TABLE t_owner (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id      INT                   DEFAULT NULL COMMENT '关联登录账号',
    name         VARCHAR(50)  NOT NULL COMMENT '业主姓名',
    gender       VARCHAR(4)   NOT NULL DEFAULT '男' COMMENT '性别',
    phone        VARCHAR(20)  NOT NULL COMMENT '联系电话',
    id_card      VARCHAR(20)           DEFAULT NULL COMMENT '身份证号',
    room_id      INT          NOT NULL COMMENT '房屋ID',
    family_count INT          NOT NULL DEFAULT 1 COMMENT '家庭人数',
    move_in_date DATE                  DEFAULT NULL COMMENT '入住日期',
    remark       VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_owner_room (room_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '业主表';

-- ------------------------------------------------------------
-- 5. 费用账单表
-- ------------------------------------------------------------
CREATE TABLE t_fee_bill (
    id         INT           NOT NULL AUTO_INCREMENT COMMENT '主键',
    room_id    INT           NOT NULL COMMENT '房屋ID',
    owner_id   INT           NOT NULL COMMENT '业主ID',
    fee_type   VARCHAR(20)   NOT NULL COMMENT '费用类型 物业费/水费/电费/停车费',
    period     VARCHAR(20)   NOT NULL COMMENT '费用所属周期 如 2026-01',
    amount     DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '应缴金额',
    status     VARCHAR(10)   NOT NULL DEFAULT '未缴' COMMENT '缴费状态 未缴/已缴',
    due_date   DATE                   DEFAULT NULL COMMENT '缴费截止日期',
    pay_time   DATETIME               DEFAULT NULL COMMENT '缴费时间',
    pay_method VARCHAR(20)            DEFAULT NULL COMMENT '缴费方式 微信/支付宝/现金/银行转账',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_bill_owner (owner_id),
    KEY idx_bill_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '费用账单表';

-- ------------------------------------------------------------
-- 6. 报修工单表
-- ------------------------------------------------------------
CREATE TABLE t_repair (
    id            INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    room_id       INT          NOT NULL COMMENT '报修房屋',
    owner_id      INT          NOT NULL COMMENT '报修业主',
    category      VARCHAR(20)  NOT NULL DEFAULT '其他' COMMENT '报修类别 水电/门窗/电梯/公共设施/家电/其他',
    title         VARCHAR(100) NOT NULL COMMENT '报修标题',
    content       VARCHAR(500)          DEFAULT NULL COMMENT '故障描述',
    urgency       VARCHAR(10)  NOT NULL DEFAULT '普通' COMMENT '紧急程度 普通/紧急',
    status        VARCHAR(10)  NOT NULL DEFAULT '待处理' COMMENT '状态 待处理/处理中/已完成',
    handler_id    INT                   DEFAULT NULL COMMENT '处理人ID',
    handle_remark VARCHAR(500)          DEFAULT NULL COMMENT '处理说明',
    handle_time   DATETIME              DEFAULT NULL COMMENT '处理完成时间',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
    PRIMARY KEY (id),
    KEY idx_repair_status (status),
    KEY idx_repair_owner (owner_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '报修工单表';

-- ------------------------------------------------------------
-- 7. 投诉建议表
-- ------------------------------------------------------------
CREATE TABLE t_complaint (
    id            INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    owner_id      INT          NOT NULL COMMENT '提交业主',
    type          VARCHAR(10)  NOT NULL DEFAULT '投诉' COMMENT '类型 投诉/建议',
    title         VARCHAR(100) NOT NULL COMMENT '标题',
    content       VARCHAR(500)          DEFAULT NULL COMMENT '内容',
    status        VARCHAR(10)  NOT NULL DEFAULT '待处理' COMMENT '状态 待处理/已回复',
    reply_content VARCHAR(500)          DEFAULT NULL COMMENT '回复内容',
    reply_by      INT                   DEFAULT NULL COMMENT '回复人ID',
    reply_time    DATETIME              DEFAULT NULL COMMENT '回复时间',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    PRIMARY KEY (id),
    KEY idx_complaint_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '投诉建议表';

-- ------------------------------------------------------------
-- 8. 公告通知表
-- ------------------------------------------------------------
CREATE TABLE t_notice (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    title        VARCHAR(100) NOT NULL COMMENT '标题',
    content      VARCHAR(2000)         DEFAULT NULL COMMENT '内容',
    type         VARCHAR(10)  NOT NULL DEFAULT '公告' COMMENT '类型 公告/通知/紧急',
    publisher_id INT                   DEFAULT NULL COMMENT '发布人ID',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1 已发布 0 草稿',
    view_count   INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    publish_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (id),
    KEY idx_notice_type (type)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '公告通知表';

-- ------------------------------------------------------------
-- 9. 访客登记表
-- ------------------------------------------------------------
CREATE TABLE t_visitor (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    visitor_name VARCHAR(50)  NOT NULL COMMENT '访客姓名',
    phone        VARCHAR(20)           DEFAULT NULL COMMENT '访客电话',
    room_id      INT          NOT NULL COMMENT '访问房屋',
    visit_time   DATETIME     NOT NULL COMMENT '到访时间',
    leave_time   DATETIME              DEFAULT NULL COMMENT '离开时间',
    purpose      VARCHAR(100)          DEFAULT NULL COMMENT '来访事由',
    status       VARCHAR(10)  NOT NULL DEFAULT '待到访' COMMENT '状态 待到访/已到访/已离开',
    register_by  INT                   DEFAULT NULL COMMENT '登记人ID',
    remark       VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
    PRIMARY KEY (id),
    KEY idx_visitor_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '访客登记表';

-- ------------------------------------------------------------
-- 10. 停车位表
-- ------------------------------------------------------------
CREATE TABLE t_parking (
    id          INT           NOT NULL AUTO_INCREMENT COMMENT '主键',
    space_no    VARCHAR(20)   NOT NULL COMMENT '车位编号',
    area        VARCHAR(50)   NOT NULL DEFAULT '地下车库' COMMENT '所在区域',
    type        VARCHAR(10)   NOT NULL DEFAULT '固定' COMMENT '类型 固定/临时',
    status      VARCHAR(10)   NOT NULL DEFAULT '空闲' COMMENT '状态 空闲/已租用/临时占用',
    owner_id    INT                    DEFAULT NULL COMMENT '租用业主',
    plate_no    VARCHAR(20)            DEFAULT NULL COMMENT '车牌号',
    monthly_fee DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '月租金',
    rent_start  DATE                   DEFAULT NULL COMMENT '租用开始日期',
    rent_end    DATE                   DEFAULT NULL COMMENT '租用结束日期',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_space_no (space_no)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '停车位表';


-- ============================================================
-- 初始化数据
-- ============================================================

-- 用户（密码均为 123456，MD5 加密后 e10adc3949ba59abbe56e057f20f883e）
INSERT INTO t_user (username, password, real_name, phone, role, status, create_time) VALUES
('admin',   'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000000', 'ADMIN', 1, '2026-01-01 09:00:00'),
('wuye01',  'e10adc3949ba59abbe56e057f20f883e', '王丽',       '13800000001', 'STAFF', 1, '2026-01-02 09:10:00'),
('wuye02',  'e10adc3949ba59abbe56e057f20f883e', '李强',       '13800000002', 'STAFF', 1, '2026-01-02 09:20:00'),
('wuye03',  'e10adc3949ba59abbe56e057f20f883e', '赵敏',       '13800000003', 'STAFF', 1, '2026-01-03 10:00:00'),
('zhangsan','e10adc3949ba59abbe56e057f20f883e', '张三',       '13900000001', 'OWNER', 1, '2026-01-05 14:00:00'),
('lisi',    'e10adc3949ba59abbe56e057f20f883e', '李四',       '13900000002', 'OWNER', 1, '2026-01-05 14:05:00'),
('wangwu',  'e10adc3949ba59abbe56e057f20f883e', '王五',       '13900000003', 'OWNER', 1, '2026-01-06 15:00:00'),
('zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六',       '13900000004', 'OWNER', 1, '2026-01-06 15:10:00'),
('sunqi',   'e10adc3949ba59abbe56e057f20f883e', '孙七',       '13900000005', 'OWNER', 1, '2026-01-07 16:00:00'),
('zhouba',  'e10adc3949ba59abbe56e057f20f883e', '周八',       '13900000006', 'OWNER', 0, '2026-01-07 16:20:00');

-- 楼栋
INSERT INTO t_building (building_no, name, unit_count, floor_count, remark, create_time) VALUES
('A1', '1号楼', 2, 18, '临街住宅楼，底层为商铺', '2026-01-01 09:30:00'),
('A2', '2号楼', 2, 18, '标准住宅楼',             '2026-01-01 09:30:00'),
('A3', '3号楼', 1, 11, '小高层',                 '2026-01-01 09:30:00'),
('B1', '4号楼', 2, 26, '高层住宅',               '2026-01-01 09:30:00'),
('B2', '5号楼', 2, 26, '高层住宅',               '2026-01-01 09:30:00'),
('C1', '6号楼', 1, 6,  '多层洋房',               '2026-01-01 09:30:00');

-- 房屋（owner_id 在业主数据插入后统一回填）
INSERT INTO t_room (building_id, room_no, floor, area, room_type, status, owner_id, create_time) VALUES
(1, '1-101', 1,  89.50, '住宅', '已入住', NULL, '2026-01-01 10:00:00'),
(1, '1-102', 1,  89.50, '住宅', '已入住', NULL, '2026-01-01 10:00:00'),
(1, '1-201', 2, 108.20, '住宅', '已入住', NULL, '2026-01-01 10:00:00'),
(1, '1-202', 2, 108.20, '住宅', '空置',   NULL, '2026-01-01 10:00:00'),
(2, '2-101', 1,  92.00, '住宅', '已入住', NULL, '2026-01-01 10:05:00'),
(2, '2-102', 1,  92.00, '住宅', '已入住', NULL, '2026-01-01 10:05:00'),
(2, '2-301', 3, 130.60, '住宅', '装修中', NULL, '2026-01-01 10:05:00'),
(3, '3-101', 1,  85.30, '住宅', '已入住', NULL, '2026-01-01 10:10:00'),
(3, '3-102', 1,  85.30, '住宅', '空置',   NULL, '2026-01-01 10:10:00'),
(4, '4-1801', 18, 143.80, '住宅', '已入住', NULL, '2026-01-01 10:15:00'),
(4, '4-1802', 18, 143.80, '住宅', '已入住', NULL, '2026-01-01 10:15:00'),
(5, '5-0901', 9,  118.40, '住宅', '已入住', NULL, '2026-01-01 10:20:00'),
(5, '5-0902', 9,  118.40, '住宅', '空置',   NULL, '2026-01-01 10:20:00'),
(6, '6-101', 1,  156.00, '住宅', '已入住', NULL, '2026-01-01 10:25:00'),
(1, 'A1-S01', 1, 45.00, '商铺', '已入住', NULL, '2026-01-01 10:30:00'),
(1, 'A1-S02', 1, 52.00, '商铺', '空置',   NULL, '2026-01-01 10:30:00');

-- 业主
INSERT INTO t_owner (user_id, name, gender, phone, id_card, room_id, family_count, move_in_date, remark, create_time) VALUES
(5, '张三', '男', '13900000001', '310101198801011234', 1,  3, '2026-01-10', '常住业主',       '2026-01-05 14:10:00'),
(6, '李四', '女', '13900000002', '310101199003052345', 2,  2, '2026-01-12', '常住业主',       '2026-01-05 14:15:00'),
(7, '王五', '男', '13900000003', '310101197805123456', 3,  4, '2026-01-15', '有私家车',       '2026-01-06 15:20:00'),
(8, '赵六', '女', '13900000004', '310101199211234567', 5,  1, '2026-01-20', '房屋为出租房',   '2026-01-06 15:30:00'),
(9, '孙七', '男', '13900000005', '310101198504304567', 8,  2, '2026-02-01', '养宠物',         '2026-01-07 16:10:00'),
(NULL,'陈九', '女', '13900000007', '310101199607185678', 10, 3, '2026-02-05', '已缴纳全年物业费','2026-02-05 09:30:00'),
(NULL,'吴十', '男', '13900000008', '310101198809226789', 14, 5, '2026-02-08', '洋房业主',       '2026-02-08 10:10:00'),
(NULL,'郑十一','女','13900000009','310101199501157890', 15, 1, '2026-02-10', '商铺租户',       '2026-02-10 11:00:00');

-- 回填业主与房屋的绑定关系
UPDATE t_room r JOIN t_owner o ON o.room_id = r.id SET r.owner_id = o.id;

-- 费用账单
INSERT INTO t_fee_bill (room_id, owner_id, fee_type, period, amount, status, due_date, pay_time, pay_method, create_time) VALUES
(1,  1, '物业费', '2026-01', 178.00, '已缴', '2026-01-31', '2026-01-08 10:12:00', '微信',     '2026-01-01 08:00:00'),
(1,  1, '物业费', '2026-02', 178.00, '已缴', '2026-02-28', '2026-02-06 09:20:00', '微信',     '2026-02-01 08:00:00'),
(1,  1, '物业费', '2026-03', 178.00, '未缴', '2026-03-31', NULL,                  NULL,       '2026-03-01 08:00:00'),
(1,  1, '水费',   '2026-02',  62.50, '已缴', '2026-02-28', '2026-02-25 19:40:00', '支付宝',   '2026-02-01 08:00:00'),
(1,  1, '电费',   '2026-02', 156.80, '未缴', '2026-03-10', NULL,                  NULL,       '2026-02-01 08:00:00'),
(2,  2, '物业费', '2026-02', 178.00, '已缴', '2026-02-28', '2026-02-11 15:00:00', '支付宝',   '2026-02-01 08:00:00'),
(2,  2, '物业费', '2026-03', 178.00, '未缴', '2026-03-31', NULL,                  NULL,       '2026-03-01 08:00:00'),
(3,  3, '物业费', '2026-03', 216.40, '未缴', '2026-03-31', NULL,                  NULL,       '2026-03-01 08:00:00'),
(3,  3, '停车费', '2026-03', 300.00, '已缴', '2026-03-05', '2026-03-02 08:30:00', '银行转账', '2026-03-01 08:00:00'),
(5,  4, '物业费', '2026-03', 184.00, '未缴', '2026-03-31', NULL,                  NULL,       '2026-03-01 08:00:00'),
(5,  4, '水费',   '2026-03',  48.20, '未缴', '2026-03-31', NULL,                  NULL,       '2026-03-01 08:00:00'),
(8,  5, '物业费', '2026-03', 170.60, '已缴', '2026-03-31', '2026-03-15 20:10:00', '微信',     '2026-03-01 08:00:00'),
(10, 6, '物业费', '2026-03', 287.60, '已缴', '2026-03-31', '2026-03-04 11:00:00', '支付宝',   '2026-03-01 08:00:00'),
(14, 7, '物业费', '2026-03', 312.00, '未缴', '2026-03-31', NULL,                  NULL,       '2026-03-01 08:00:00'),
(15, 8, '物业费', '2026-03', 260.00, '已缴', '2026-03-31', '2026-03-08 09:00:00', '银行转账', '2026-03-01 08:00:00'),
(15, 8, '电费',   '2026-03', 420.00, '未缴', '2026-03-31', NULL,                  NULL,       '2026-03-01 08:00:00'),
(14, 7, '停车费', '2026-02', 300.00, '已缴', '2026-02-05', '2026-02-03 10:00:00', '微信',     '2026-02-01 08:00:00');

-- 报修工单
INSERT INTO t_repair (room_id, owner_id, category, title, content, urgency, status, handler_id, handle_remark, handle_time, create_time) VALUES
(1,  1, '水电',     '厨房水管漏水',   '厨房水槽下方水管接口持续渗水，地面已积水。', '紧急', '已完成', 3, '更换水管接头并做防水处理，已恢复正常。', '2026-03-02 16:30:00', '2026-03-02 08:20:00'),
(1,  1, '门窗',     '阳台推拉门卡顿', '推拉门滑轨生锈，开关困难。',                 '普通', '已完成', 3, '清理滑轨并加注润滑油。',                 '2026-03-06 11:00:00', '2026-03-05 19:10:00'),
(2,  2, '家电',     '热水器无法点火', '燃气热水器打不着火，已检查燃气正常。',       '紧急', '处理中', 3, NULL, NULL, '2026-03-12 07:50:00'),
(3,  3, '电梯',     '电梯运行有异响', '2号楼1单元电梯上行时出现金属摩擦声。',       '紧急', '处理中', 3, NULL, NULL, '2026-03-13 09:15:00'),
(5,  4, '公共设施', '楼道感应灯不亮', '5号楼2单元3层楼道感应灯不亮。',              '普通', '待处理', NULL, NULL, NULL, '2026-03-15 21:30:00'),
(8,  5, '水电',     '卫生间下水缓慢', '卫生间地漏排水缓慢，怀疑管道堵塞。',         '普通', '待处理', NULL, NULL, NULL, '2026-03-16 08:40:00'),
(14, 7, '其他',     '门禁卡失灵',     '小区门禁卡无法刷卡进入。',                   '普通', '已完成', 2, '重新写卡并升级门禁系统。',               '2026-03-10 15:20:00', '2026-03-09 17:00:00'),
(15, 8, '水电',     '商铺电路跳闸',   '商铺空调开启后频繁跳闸。',                   '紧急', '待处理', NULL, NULL, NULL, '2026-03-17 10:05:00');

-- 投诉建议
INSERT INTO t_complaint (owner_id, type, title, content, status, reply_content, reply_by, reply_time, create_time) VALUES
(1, '投诉', '楼下商铺夜间噪音扰民', 'A1-S01商铺每晚营业至23点后，音响声音过大，影响休息。', '已回复', '已与商铺负责人沟通，要求22点后关闭外放音响，物业将加强巡查。', 2, '2026-03-05 10:20:00', '2026-03-04 22:40:00'),
(3, '建议', '建议增加小区儿童游乐设施', '建议在中心花园增设儿童滑梯和健身器材。',               '已回复', '建议已提交业委会讨论，预计第二季度纳入改造计划。',           2, '2026-03-07 09:00:00', '2026-03-06 18:30:00'),
(4, '投诉', '垃圾清运不及时',           '2号楼垃圾投放点经常满溢，异味较大。',                 '待处理', NULL, NULL, NULL, '2026-03-16 07:20:00'),
(5, '建议', '建议地下车库增加监控',     '地下车库B区部分区域无监控覆盖。',                     '待处理', NULL, NULL, NULL, '2026-03-17 12:10:00');

-- 公告通知
INSERT INTO t_notice (title, content, type, publisher_id, status, view_count, publish_time) VALUES
('关于2026年3月物业费缴纳的通知', '各位业主：3月物业费已开始收缴，请于3月31日前通过物业APP或物业服务中心缴纳，逾期将按合同约定收取滞纳金。', '通知', 1, 1, 156, '2026-03-01 09:00:00'),
('小区停水通知',                   '因供水管网检修，3月18日 09:00-17:00 全小区停水，请各业主提前储水，给您带来不便敬请谅解。',        '紧急', 2, 1, 342, '2026-03-16 16:30:00'),
('春季消防安全演练公告',           '定于3月25日上午10点在中心广场举行消防演练，欢迎业主参加，现场讲解灭火器使用方法。',                  '公告', 2, 1, 98,  '2026-03-14 10:00:00'),
('地下车库车位租赁办理指南',       '地下车库尚有少量固定车位可租，月租金300元，请携带身份证及行驶证到物业服务中心办理。',                 '公告', 1, 1, 87,  '2026-03-10 14:00:00'),
('小区绿化养护作业安排',           '3月20日-3月22日进行绿化修剪与喷药作业，请业主看护好儿童和宠物，避免接触绿植。',                    '通知', 3, 1, 64,  '2026-03-18 09:30:00'),
('电梯年度检修计划（草稿）',       '各楼栋电梯将于4月进行年度检修，具体时间待定。',                                                    '通知', 1, 0, 0,   '2026-03-19 11:00:00');

-- 访客登记
INSERT INTO t_visitor (visitor_name, phone, room_id, visit_time, leave_time, purpose, status, register_by, remark, create_time) VALUES
('刘先生', '13700000001', 1,  '2026-03-17 09:30:00', '2026-03-17 11:00:00', '亲友拜访',   '已离开', 2, '已核实业主同意',   '2026-03-17 09:25:00'),
('快递员', '13700000002', 1,  '2026-03-18 14:00:00', '2026-03-18 14:20:00', '送快递',     '已离开', 2, '临时通行',         '2026-03-18 13:58:00'),
('维修师傅','13700000003',2,  '2026-03-18 15:00:00', NULL,                  '上门维修',   '已到访', 3, '携带工具箱登记',   '2026-03-18 14:55:00'),
('陈女士', '13700000004', 3,  '2026-03-19 10:00:00', NULL,                  '亲友拜访',   '待到访', 2, '预约今日到访',     '2026-03-19 08:30:00'),
('搬家公司','13700000005', 5,  '2026-03-19 13:00:00', NULL,                  '搬家',       '待到访', 2, '需占用地面临时车位','2026-03-19 09:10:00');

-- 停车位
INSERT INTO t_parking (space_no, area, type, status, owner_id, plate_no, monthly_fee, rent_start, rent_end, create_time) VALUES
('B1-001', '地下车库A区', '固定', '已租用', 1, '沪A12345', 300.00, '2026-01-10', '2026-12-31', '2026-01-08 09:00:00'),
('B1-002', '地下车库A区', '固定', '已租用', 3, '沪B88888', 300.00, '2026-01-15', '2026-12-31', '2026-01-08 09:00:00'),
('B1-003', '地下车库A区', '固定', '空闲',   NULL, NULL,     300.00, NULL, NULL, '2026-01-08 09:00:00'),
('B1-004', '地下车库A区', '固定', '已租用', 7, '沪C66666', 300.00, '2026-02-05', '2027-02-04', '2026-01-08 09:00:00'),
('B1-005', '地下车库B区', '固定', '空闲',   NULL, NULL,     300.00, NULL, NULL, '2026-01-08 09:00:00'),
('B1-006', '地下车库B区', '固定', '已租用', 8, '沪D22222', 300.00, '2026-02-10', '2027-02-09', '2026-01-08 09:00:00'),
('B1-007', '地下车库B区', '固定', '空闲',   NULL, NULL,     300.00, NULL, NULL, '2026-01-08 09:00:00'),
('B1-008', '地下车库B区', '临时', '临时占用', NULL, '沪E33333', 150.00, NULL, NULL, '2026-01-08 09:00:00'),
('G-001',  '地面停车场',   '临时', '空闲',   NULL, NULL,     150.00, NULL, NULL, '2026-01-08 09:00:00'),
('G-002',  '地面停车场',   '临时', '空闲',   NULL, NULL,     150.00, NULL, NULL, '2026-01-08 09:00:00'),
('G-003',  '地面停车场',   '固定', '已租用', 4, '沪F55555', 200.00, '2026-01-20', '2026-12-31', '2026-01-08 09:00:00'),
('G-004',  '地面停车场',   '临时', '空闲',   NULL, NULL,     150.00, NULL, NULL, '2026-01-08 09:00:00');

-- 让自增主键从当前最大值继续
ALTER TABLE t_user AUTO_INCREMENT = 100;
ALTER TABLE t_owner AUTO_INCREMENT = 100;

SELECT '数据库 property_db 初始化完成' AS message;
