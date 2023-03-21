CREATE TABLE `bond_buy_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
  `gp_id` varchar(50) DEFAULT NULL COMMENT '股票代码',
  `price` double DEFAULT NULL COMMENT '购买价格',
  `count` int(10) DEFAULT NULL COMMENT '购买数量',
  `cost` double NOT NULL DEFAULT 0 COMMENT '税费',
  `type` tinyint(2) NOT NULL COMMENT 'type:0 网格 type:1短线',
  `sell_count` int(11) NOT NULL DEFAULT 0 COMMENT '已出售数量',
  `sell_income` double NOT NULL DEFAULT 0 COMMENT '出售收益',
  `buy_date` varchar(50) NOT NULL DEFAULT '' COMMENT '购买日期',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '0:未售完 1:已售完',
  `oper_time` datetime NOT NULL COMMENT '操作时间',
  `remarks` varchar(50) DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='股票购买日志';

CREATE TABLE `bond_info` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT '主键',
  `name` varchar(11) NOT NULL COMMENT '股票名称',
  `price` double unsigned NOT NULL DEFAULT 0 COMMENT '当前股价',
  `plate` varchar(50) NOT NULL DEFAULT '0' COMMENT '上市所属板块 0：上证 1:深圳 2：创业板 3：etf',
  `is_etf` tinyint(2) DEFAULT NULL COMMENT '是否ETF 1：etf',
  `position` double DEFAULT NULL COMMENT '仓位占比',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股票信息';

CREATE TABLE `bond_sell_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
  `gp_id` varchar(50) DEFAULT NULL,
  `buy_id` bigint(20) DEFAULT NULL,
  `price` double DEFAULT NULL COMMENT '出售价格',
  `count` int(10) DEFAULT NULL COMMENT '出售数量',
  `cost` double NOT NULL DEFAULT 0 COMMENT '佣金',
  `income` double NOT NULL DEFAULT 0 COMMENT '收益',
  `surplus_count` int(11) DEFAULT NULL COMMENT '出售后当前剩余数量',
  `create_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='股票出售日志';

CREATE TABLE `operating_log` (
  `id` varchar(50) NOT NULL COMMENT '唯一ID',
  `oper_method` varchar(500) DEFAULT NULL COMMENT '操作方法',
  `oper_user` varchar(50) DEFAULT NULL COMMENT '操作用户',
  `oper_type` varchar(100) DEFAULT NULL COMMENT '操作类型',
  `oper_module` varchar(100) DEFAULT NULL COMMENT '操作模块',
  `oper_title` varchar(6000) DEFAULT NULL COMMENT '操作标题',
  `oper_path` varchar(500) DEFAULT NULL COMMENT '操作路径',
  `oper_content` longtext DEFAULT NULL COMMENT '操作内容',
  `oper_params` longtext DEFAULT NULL COMMENT '操作参数',
  `oper_time` bigint(13) NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

CREATE TABLE `sy_user` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `login_name` varchar(100) NOT NULL COMMENT '登录名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;
