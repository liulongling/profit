CREATE TABLE `bond_buy_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
	`gp_id` VARCHAR(50) NULL DEFAULT NULL COMMENT '股票代码' COLLATE 'utf8mb4_general_ci',
	`price` DOUBLE NULL DEFAULT NULL COMMENT '购买价格',
	`count` INT(10) NULL DEFAULT NULL COMMENT '购买数量',
	`cost` DOUBLE NOT NULL DEFAULT '0' COMMENT '税费',
	`type` TINYINT(2) NOT NULL COMMENT 'type:0 网格 type:1短线',
	`sell_count` INT(11) NOT NULL DEFAULT '0' COMMENT '已出售数量',
	`sell_income` DOUBLE NOT NULL DEFAULT '0' COMMENT '出售收益',
	`buy_date` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '购买日期' COLLATE 'utf8mb4_general_ci',
	`status` TINYINT(2) NOT NULL DEFAULT '0' COMMENT '0:未售完 1:已售完',
	`oper_time` DATETIME NOT NULL COMMENT '操作时间',
	`create_time` DATETIME NOT NULL COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='股票购买日志'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;
CREATE TABLE `bond_info` (
	`id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '主键' COLLATE 'utf8mb4_general_ci',
	`name` VARCHAR(11) NOT NULL COMMENT '股票名称' COLLATE 'utf8mb4_general_ci',
	`price` DOUBLE UNSIGNED NOT NULL DEFAULT '0' COMMENT '当前股价',
	`plate` VARCHAR(50) NOT NULL DEFAULT '0' COMMENT '上市所属板块 0：上证 1:深圳 2：创业板 3：etf' COLLATE 'utf8mb4_general_ci',
	`is_etf` TINYINT(2) NULL DEFAULT NULL COMMENT '是否ETF 1：etf',
	`create_time` DATETIME NULL DEFAULT NULL,
	`update_time` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='股票信息'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;
CREATE TABLE `bond_sell_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
	`buy_id` BIGINT(20) NULL DEFAULT NULL,
	`price` DOUBLE NULL DEFAULT NULL COMMENT '出售价格',
	`count` INT(10) NULL DEFAULT NULL COMMENT '出售数量',
	`cost` DOUBLE NOT NULL DEFAULT '0' COMMENT '佣金',
	`income` DOUBLE NOT NULL DEFAULT '0' COMMENT '收益',
	`create_time` DATETIME NOT NULL COMMENT '操作时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='股票出售日志'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;
CREATE TABLE `operating_log` (
	`id` VARCHAR(50) NOT NULL COMMENT '唯一ID' COLLATE 'utf8mb4_general_ci',
	`oper_method` VARCHAR(500) NULL DEFAULT NULL COMMENT '操作方法' COLLATE 'utf8mb4_general_ci',
	`oper_user` VARCHAR(50) NULL DEFAULT NULL COMMENT '操作用户' COLLATE 'utf8mb4_general_ci',
	`oper_type` VARCHAR(100) NULL DEFAULT NULL COMMENT '操作类型' COLLATE 'utf8mb4_general_ci',
	`oper_module` VARCHAR(100) NULL DEFAULT NULL COMMENT '操作模块' COLLATE 'utf8mb4_general_ci',
	`oper_title` VARCHAR(6000) NULL DEFAULT NULL COMMENT '操作标题' COLLATE 'utf8mb4_general_ci',
	`oper_path` VARCHAR(500) NULL DEFAULT NULL COMMENT '操作路径' COLLATE 'utf8mb4_general_ci',
	`oper_content` LONGTEXT NULL DEFAULT NULL COMMENT '操作内容' COLLATE 'utf8mb4_general_ci',
	`oper_params` LONGTEXT NULL DEFAULT NULL COMMENT '操作参数' COLLATE 'utf8mb4_general_ci',
	`oper_time` BIGINT(13) NOT NULL COMMENT '操作时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='操作日志'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;
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
