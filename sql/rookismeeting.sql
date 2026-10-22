-- 会议管理系统：建表语句和示例数据
-- 原仓库没有附带 SQL，下面的表结构是按后端实体类、Mapper XML 和 DTO 反推出来的。
-- 用法：
--   CREATE DATABASE rookismeeting DEFAULT CHARSET utf8mb4;
--   mysql -uroot -p rookismeeting < sql/rookismeeting.sql
-- 示例账号的密码都是 123456。会议时间按导入当天计算，导入后就能看到"今天"和"未来几天"的会议。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 部门
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `departmentid` int NOT NULL AUTO_INCREMENT COMMENT '部门编号',
  `departmentname` varchar(50) NOT NULL COMMENT '部门名称',
  PRIMARY KEY (`departmentid`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '部门';

INSERT INTO `department` VALUES (1, '技术部'), (2, '市场部'), (3, '人事部'), (4, '行政部');

-- ----------------------------
-- 员工
-- status：0 待审批，1 正常，2 审批未通过
-- role：对应 sys_role.flag
-- password：md5(明文密码 + Constants.SALT)
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `employeeid` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `employeename` varchar(50) DEFAULT NULL COMMENT '姓名',
  `username` varchar(50) NOT NULL COMMENT '登录账号',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` int NOT NULL DEFAULT 0 COMMENT '账号状态',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `departmentid` int DEFAULT NULL COMMENT '所属部门',
  `password` varchar(64) NOT NULL COMMENT '登录密码',
  `role` varchar(50) NOT NULL DEFAULT 'ROLE_NORMAL' COMMENT '角色标识',
  `faceimg` varchar(255) DEFAULT NULL COMMENT '人脸照片文件名',
  `is_delete` int NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
  PRIMARY KEY (`employeeid`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '员工';

INSERT INTO `employee` VALUES
  (1, '管理员', 'admin', '13800000000', 1, 'admin@example.com', 1, '20a95ceec8c7c414a890eedf942afcc8', 'ROLE_ADMIN', NULL, 0),
  (2, '张三', 'zhangsan', '13800000001', 1, 'zhangsan@example.com', 1, '20a95ceec8c7c414a890eedf942afcc8', 'ROLE_NORMAL', NULL, 0),
  (3, '李四', 'lisi', '13800000002', 1, 'lisi@example.com', 2, '20a95ceec8c7c414a890eedf942afcc8', 'ROLE_NORMAL', NULL, 0),
  (4, '王五', 'wangwu', '13800000003', 1, 'wangwu@example.com', 3, '20a95ceec8c7c414a890eedf942afcc8', 'ROLE_NORMAL', NULL, 0),
  (5, '赵六', 'zhaoliu', '13800000004', 0, 'zhaoliu@example.com', 4, '20a95ceec8c7c414a890eedf942afcc8', 'ROLE_NORMAL', NULL, 0),
  (6, '孙七', 'sunqi', '13800000005', 2, 'sunqi@example.com', 2, '20a95ceec8c7c414a890eedf942afcc8', 'ROLE_NORMAL', NULL, 0);

-- ----------------------------
-- 角色
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `flag` varchar(50) NOT NULL COMMENT '唯一标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_flag` (`flag`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色';

INSERT INTO `sys_role` VALUES
  (1, '管理员', '可以使用全部功能', 'ROLE_ADMIN'),
  (2, '普通用户', '预定会议、参加会议、签到', 'ROLE_NORMAL');

-- ----------------------------
-- 菜单
-- path 是前端路由地址，page_path 是 meeting/src/views 下的页面文件（不带 .vue）
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `path` varchar(100) DEFAULT NULL COMMENT '路由地址',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `pid` int DEFAULT NULL COMMENT '父级ID',
  `page_path` varchar(100) DEFAULT NULL COMMENT '页面路径',
  `sort_num` int DEFAULT NULL COMMENT '顺序',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜单';

INSERT INTO `sys_menu` VALUES
  (1, '主页', '/home', 'el-icon-house', '会议日历和近期会议', NULL, 'Home', 1),
  (2, '数据报表', '/echarts', 'el-icon-s-data', '账号和会议统计', NULL, 'Echarts', 2),
  (3, '个人中心', NULL, 'el-icon-user', NULL, NULL, NULL, 3),
  (4, '我的会议', '/myMeeting', 'el-icon-date', '我参加的会议，在这里签到', 3, 'Meeting/MyMeeting', 1),
  (5, '我的预定', '/myBooking', 'el-icon-tickets', '我预定的会议', 3, 'Meeting/MyBooking', 2),
  (6, '已取消的会议', '/canceledMeeting', 'el-icon-circle-close', NULL, 3, 'Meeting/CanceledMeeting', 3),
  (7, '我的消息', '/myMessage', 'el-icon-message', NULL, 3, 'Message/MyMessage', 4),
  (8, '会议预定', NULL, 'el-icon-s-claim', NULL, NULL, NULL, 4),
  (9, '预定会议', '/bookMeeting', 'el-icon-edit-outline', NULL, 8, 'Meeting/BookMeeting', 1),
  (10, '会议室查询', '/roomSearch', 'el-icon-office-building', NULL, 8, 'Room/RoomSearch', 2),
  (11, '系统管理员', NULL, 'el-icon-s-custom', NULL, NULL, NULL, 5),
  (12, '员工管理', '/employee', 'el-icon-user-solid', NULL, 11, 'Admin/Employee', 1),
  (13, '部门管理', '/department', 'el-icon-s-cooperation', NULL, 11, 'Admin/Department', 2),
  (14, '账号审批', '/approve', 'el-icon-s-check', NULL, 11, 'Admin/Approve', 3),
  (15, '会议室管理', '/room', 'el-icon-s-home', NULL, 11, 'Admin/Room', 4),
  (16, '会议管理', '/meeting', 'el-icon-notebook-2', NULL, 11, 'Admin/Meeting', 5),
  (17, '公告管理', '/news', 'el-icon-bell', NULL, 11, 'Admin/News', 6),
  (18, '消息管理', '/message', 'el-icon-chat-dot-round', NULL, 11, 'Admin/Message', 7),
  (19, '权限管理', NULL, 'el-icon-s-tools', NULL, NULL, NULL, 6),
  (20, '角色管理', '/role', 'el-icon-s-custom', NULL, 19, 'Admin/Role', 1),
  (21, '菜单管理', '/menu', 'el-icon-menu', NULL, 19, 'Admin/Menu', 2);

-- ----------------------------
-- 角色和菜单的对应关系
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` int NOT NULL COMMENT '角色ID',
  `menu_id` int NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色菜单';

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) SELECT 1, `id` FROM `sys_menu`;
INSERT INTO `sys_role_menu` VALUES (2, 1), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10);

-- ----------------------------
-- 字典（菜单管理里可选的图标）
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `value` varchar(255) DEFAULT NULL COMMENT '内容',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '字典';

INSERT INTO `sys_dict` (`name`, `value`, `type`) VALUES
  ('house', 'el-icon-house', 'icon'), ('s-data', 'el-icon-s-data', 'icon'),
  ('user', 'el-icon-user', 'icon'), ('user-solid', 'el-icon-user-solid', 'icon'),
  ('date', 'el-icon-date', 'icon'), ('tickets', 'el-icon-tickets', 'icon'),
  ('circle-close', 'el-icon-circle-close', 'icon'), ('message', 'el-icon-message', 'icon'),
  ('s-claim', 'el-icon-s-claim', 'icon'), ('edit-outline', 'el-icon-edit-outline', 'icon'),
  ('office-building', 'el-icon-office-building', 'icon'), ('s-custom', 'el-icon-s-custom', 'icon'),
  ('s-cooperation', 'el-icon-s-cooperation', 'icon'), ('s-check', 'el-icon-s-check', 'icon'),
  ('s-home', 'el-icon-s-home', 'icon'), ('notebook-2', 'el-icon-notebook-2', 'icon'),
  ('bell', 'el-icon-bell', 'icon'), ('chat-dot-round', 'el-icon-chat-dot-round', 'icon'),
  ('s-tools', 'el-icon-s-tools', 'icon'), ('menu', 'el-icon-menu', 'icon'),
  ('setting', 'el-icon-setting', 'icon'), ('document', 'el-icon-document', 'icon');

-- ----------------------------
-- 公告（所有人可见）
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '编号',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `message` text COMMENT '内容',
  `employee_ids` varchar(255) DEFAULT NULL COMMENT '接收人（公告不用）',
  `addtime` varchar(30) DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '公告';

INSERT INTO `news` (`title`, `message`, `addtime`) VALUES
  ('欢迎使用会议管理系统', '预定会议后，参会人可以在"我的会议"里刷脸签到。', DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')),
  ('大会议室设备维护', '本周五下午大会议室投影仪维护，请改用第二会议室。', DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'));

-- ----------------------------
-- 消息（发给指定员工，employee_ids 是逗号分隔的员工ID）
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '编号',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `message` text COMMENT '内容',
  `employee_ids` varchar(255) DEFAULT NULL COMMENT '接收人',
  `addtime` varchar(30) DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '消息';

INSERT INTO `message` (`title`, `message`, `employee_ids`, `addtime`) VALUES
  ('请录入人脸照片', '签到需要人脸识别，请到"个人信息"页面上传一张正脸照片。', '2,3,4', DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'));

-- ----------------------------
-- 会议室
-- status：0 可用，1 停用
-- ----------------------------
DROP TABLE IF EXISTS `meetingroom`;
CREATE TABLE `meetingroom` (
  `roomid` int NOT NULL AUTO_INCREMENT COMMENT '会议室编号',
  `roomname` varchar(50) NOT NULL COMMENT '名称',
  `roomnum` int NOT NULL DEFAULT 10 COMMENT '容纳人数',
  `location` varchar(100) DEFAULT NULL COMMENT '位置',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`roomid`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '会议室';

INSERT INTO `meetingroom` VALUES
  (1, '第一会议室', 10, '3 楼 301', 0, '有电视'),
  (2, '第二会议室', 20, '3 楼 305', 0, '有投影仪'),
  (3, '大会议室', 50, '5 楼 501', 0, '有投影仪和视频会议设备'),
  (4, '洽谈室', 6, '1 楼大厅旁', 1, '装修中');

-- ----------------------------
-- 会议
-- status：0 正常，1 已取消
-- ----------------------------
DROP TABLE IF EXISTS `meeting`;
CREATE TABLE `meeting` (
  `meetingid` int NOT NULL AUTO_INCREMENT COMMENT '会议编号',
  `meetingname` varchar(100) NOT NULL COMMENT '会议名称',
  `roomid` int NOT NULL COMMENT '会议室',
  `reservationistid` bigint NOT NULL COMMENT '预定人',
  `numberofparticipants` int NOT NULL DEFAULT 0 COMMENT '参会人数',
  `starttime` datetime NOT NULL COMMENT '开始时间',
  `endtime` datetime NOT NULL COMMENT '结束时间',
  `signinstarttime` datetime NOT NULL COMMENT '签到开始时间',
  `signinendtime` datetime NOT NULL COMMENT '签到结束时间',
  `reservationtime` datetime NOT NULL COMMENT '预定时间',
  `canceledtime` datetime DEFAULT NULL COMMENT '取消时间',
  `canceledreason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `description` varchar(500) DEFAULT NULL COMMENT '会议说明',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态',
  PRIMARY KEY (`meetingid`),
  KEY `idx_room_time` (`roomid`, `starttime`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '会议';

-- 第 1 个会议的签到时间从"导入前 30 分钟"到"导入后 2 小时"，导入后马上就能试签到
INSERT INTO `meeting` VALUES
  (1, '项目周会', 2, 2, 3,
   NOW() + INTERVAL 1 HOUR, NOW() + INTERVAL 2 HOUR,
   NOW() - INTERVAL 30 MINUTE, NOW() + INTERVAL 2 HOUR,
   NOW() - INTERVAL 1 DAY, NULL, NULL, '同步本周进度', 0),
  (2, '产品需求评审', 3, 3, 4,
   CURDATE() + INTERVAL 2 DAY + INTERVAL 14 HOUR, CURDATE() + INTERVAL 2 DAY + INTERVAL 16 HOUR,
   CURDATE() + INTERVAL 2 DAY + INTERVAL 13 HOUR + INTERVAL 45 MINUTE, CURDATE() + INTERVAL 2 DAY + INTERVAL 14 HOUR + INTERVAL 15 MINUTE,
   NOW() - INTERVAL 2 HOUR, NULL, NULL, '评审下个版本的需求', 0),
  (3, '部门月度总结', 1, 1, 3,
   CURDATE() + INTERVAL 5 DAY + INTERVAL 10 HOUR, CURDATE() + INTERVAL 5 DAY + INTERVAL 11 HOUR,
   CURDATE() + INTERVAL 5 DAY + INTERVAL 9 HOUR + INTERVAL 50 MINUTE, CURDATE() + INTERVAL 5 DAY + INTERVAL 10 HOUR + INTERVAL 10 MINUTE,
   NOW() - INTERVAL 3 HOUR, NULL, NULL, NULL, 0),
  (4, '客户方案沟通', 1, 2, 2,
   CURDATE() + INTERVAL 3 DAY + INTERVAL 15 HOUR, CURDATE() + INTERVAL 3 DAY + INTERVAL 16 HOUR,
   CURDATE() + INTERVAL 3 DAY + INTERVAL 14 HOUR + INTERVAL 50 MINUTE, CURDATE() + INTERVAL 3 DAY + INTERVAL 15 HOUR + INTERVAL 10 MINUTE,
   NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 1 HOUR, '客户改期', NULL, 1),
  (5, '季度复盘', 3, 1, 4,
   CURDATE() - INTERVAL 20 DAY + INTERVAL 9 HOUR, CURDATE() - INTERVAL 20 DAY + INTERVAL 11 HOUR,
   CURDATE() - INTERVAL 20 DAY + INTERVAL 8 HOUR + INTERVAL 50 MINUTE, CURDATE() - INTERVAL 20 DAY + INTERVAL 9 HOUR + INTERVAL 10 MINUTE,
   CURDATE() - INTERVAL 25 DAY, NULL, NULL, NULL, 0);

-- 往年和今年各季度的历史预定记录，给"数据报表"里的图表用
INSERT INTO `meeting` (`meetingname`, `roomid`, `reservationistid`, `numberofparticipants`, `starttime`, `endtime`,
                       `signinstarttime`, `signinendtime`, `reservationtime`, `status`)
SELECT CONCAT('历史会议 ', n), 1 + n % 3, 1 + n % 4, 0,
       d + INTERVAL 10 HOUR, d + INTERVAL 11 HOUR, d + INTERVAL 9 HOUR + INTERVAL 50 MINUTE, d + INTERVAL 10 HOUR + INTERVAL 10 MINUTE,
       d - INTERVAL 1 DAY, 0
FROM (SELECT 1 AS n, MAKEDATE(YEAR(CURDATE()), 20) AS d
      UNION ALL SELECT 2, MAKEDATE(YEAR(CURDATE()), 100)
      UNION ALL SELECT 3, MAKEDATE(YEAR(CURDATE()), 110)
      UNION ALL SELECT 4, MAKEDATE(YEAR(CURDATE()), 120)
      UNION ALL SELECT 5, MAKEDATE(YEAR(CURDATE()), 190)
      UNION ALL SELECT 6, MAKEDATE(YEAR(CURDATE()), 200)
      UNION ALL SELECT 7, MAKEDATE(YEAR(CURDATE()), 210)
      UNION ALL SELECT 8, MAKEDATE(YEAR(CURDATE()), 220)
      UNION ALL SELECT 9, MAKEDATE(YEAR(CURDATE()), 280)) AS t
WHERE d < CURDATE();

-- ----------------------------
-- 参会人员和签到记录
-- status：0 未签到，1 已签到
-- ----------------------------
DROP TABLE IF EXISTS `meetingparticipants`;
CREATE TABLE `meetingparticipants` (
  `meetingid` int NOT NULL COMMENT '会议编号',
  `employeeid` bigint NOT NULL COMMENT '员工ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '签到状态',
  `checkintime` datetime DEFAULT NULL COMMENT '签到时间',
  `location` varchar(255) DEFAULT NULL COMMENT '签到地址',
  PRIMARY KEY (`meetingid`, `employeeid`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '参会人员';

INSERT INTO `meetingparticipants` VALUES
  (1, 1, 0, NULL, NULL), (1, 2, 0, NULL, NULL), (1, 3, 0, NULL, NULL),
  (2, 1, 0, NULL, NULL), (2, 2, 0, NULL, NULL), (2, 3, 0, NULL, NULL), (2, 4, 0, NULL, NULL),
  (3, 1, 0, NULL, NULL), (3, 2, 0, NULL, NULL), (3, 4, 0, NULL, NULL),
  (4, 2, 0, NULL, NULL), (4, 3, 0, NULL, NULL),
  (5, 1, 1, CURDATE() - INTERVAL 20 DAY + INTERVAL 8 HOUR + INTERVAL 55 MINUTE, '3 楼 305'),
  (5, 2, 1, CURDATE() - INTERVAL 20 DAY + INTERVAL 9 HOUR + INTERVAL 2 MINUTE, '3 楼 305'),
  (5, 3, 0, NULL, NULL), (5, 4, 1, CURDATE() - INTERVAL 20 DAY + INTERVAL 9 HOUR, '3 楼 305');

SET FOREIGN_KEY_CHECKS = 1;
