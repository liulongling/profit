CREATE TABLE `bond_info` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID',
	`name` VARCHAR(11) NOT NULL COMMENT '股票名称' COLLATE 'utf8mb4_general_ci',
	`code` INT(11) NOT NULL DEFAULT '0' COMMENT '股票代码',
	`cur_price` DOUBLE UNSIGNED NOT NULL COMMENT '当前股价',
	`plate` TINYINT(2) UNSIGNED NOT NULL DEFAULT '0' COMMENT '上市所属板块 0：上证 1:深圳 2：创业板 3：etf',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='股票信息'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `bond_buy_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
	`gupiao_id` INT(12) NULL DEFAULT NULL COMMENT '股票代码',
	`price` DECIMAL(20,6) NULL DEFAULT NULL COMMENT '购买价格',
	`count` INT(10) NULL DEFAULT NULL COMMENT '购买数量',
	`cost` INT(11) NOT NULL DEFAULT '0' COMMENT '佣金',
	`oper_time` BIGINT(13) NOT NULL COMMENT '操作时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='股票购买日志'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `bond_sell_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
	`buy_id` BIGINT(20) NULL DEFAULT NULL,
	`price` DECIMAL(20,6) NULL DEFAULT NULL COMMENT '出售价格',
	`time` VARCHAR(10) NULL DEFAULT NULL COMMENT '购买时间' COLLATE 'utf8mb4_general_ci',
	`count` INT(10) NULL DEFAULT NULL COMMENT '出售数量',
	`cost` INT(10) NOT NULL DEFAULT '0' COMMENT '佣金',
	`oper_time` BIGINT(13) NOT NULL COMMENT '操作时间',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='股票出售日志'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

#非功能需求-操作日志
CREATE TABLE `operating_log`
(
    `id`                   varchar(50)               NOT NULL COMMENT '唯一ID',
    `oper_method`          varchar(500)              DEFAULT NULL COMMENT '操作方法',
    `oper_user`            varchar(50)               DEFAULT NULL COMMENT '操作用户',
    `oper_type`            varchar(100)              DEFAULT NULL COMMENT '操作类型',
    `oper_module`          varchar(100)              DEFAULT NULL COMMENT '操作模块',
    `oper_title`           varchar(6000)             DEFAULT NULL COMMENT '操作标题',
    `oper_path`            varchar(500)              DEFAULT NULL COMMENT '操作路径',
    `oper_content`         longtext                  COMMENT '操作内容',
    `oper_params`          longtext                  COMMENT '操作参数',
    `oper_time`            bigint(13)                NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci COMMENT='操作日志';