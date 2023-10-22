CREATE TABLE `bond_statistics` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `stock` double unsigned NOT NULL DEFAULT 0 COMMENT '持股市值',
  `ready` double NOT NULL DEFAULT 0 COMMENT '现金',
  `freeze` double DEFAULT NULL COMMENT '冻结费用',
  `position` double DEFAULT NULL COMMENT '仓位占比',
  `profit_number` int(11) DEFAULT NULL COMMENT '盈利笔数',
  `loss_number` int(11) DEFAULT NULL COMMENT '亏损笔数',
  `winning` varchar(255) DEFAULT NULL COMMENT '胜率',
  `profit` double DEFAULT NULL COMMENT '总收益',
  `buy_money` double DEFAULT NULL COMMENT '购买总金额',
  `sell_money` double DEFAULT NULL COMMENT '出售总金额',
  `loss_money` double DEFAULT NULL COMMENT '止损金额',
  `cost` double DEFAULT NULL COMMENT '消耗总佣金',
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股票信息';