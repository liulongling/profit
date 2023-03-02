CREATE TABLE `sy_user` (
	`id` VARCHAR(36) NOT NULL COMMENT '主键' COLLATE 'utf8mb4_general_ci',
	`login_name` VARCHAR(100) NOT NULL COMMENT '登录名' COLLATE 'utf8mb4_general_ci',
	`password` VARCHAR(100) NOT NULL COMMENT '密码' COLLATE 'utf8mb4_general_ci',
	`create_time` DATETIME NULL DEFAULT NULL COMMENT '创建日期',
	`update_time` DATETIME NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
ROW_FORMAT=COMPACT
;

INSERT INTO `sy_user` (`id`, `login_name`, `password`, `create_time`, `update_time`) VALUES ('48d86796896411ea8fc654e1ad394a4a', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '2020-05-04 22:30:44', '2020-05-04 22:30:44');
