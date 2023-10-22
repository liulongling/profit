ALTER TABLE `bond_buy_log`
ADD COLUMN `total_price` double DEFAULT NULL COMMENT '购买总价格' AFTER `count`,
ADD COLUMN `buy_cost` double DEFAULT NULL COMMENT '购买消耗佣金' AFTER `total_price`;

ALTER TABLE `bond_sell_log`
ADD COLUMN `total_price` double DEFAULT NULL COMMENT '出售总价格' AFTER `count`,
ADD COLUMN `realy_before` double DEFAULT NULL COMMENT '出售前现金' AFTER `surplus_count`,
ADD COLUMN `realy_after` double DEFAULT NULL COMMENT '出售后现金' AFTER `realy_before`,
ADD COLUMN `total_cost` double DEFAULT NULL COMMENT '总佣金税费，包含购买佣金' AFTER `cost`;