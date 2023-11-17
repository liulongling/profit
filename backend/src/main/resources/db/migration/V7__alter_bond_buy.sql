ALTER TABLE `bond_buy_log`
ADD COLUMN `back_money` double DEFAULT 0 COMMENT '融资归还金额' AFTER `total_price`;
ALTER TABLE `bond_buy_log`
ADD COLUMN `back_time` datetime(0) COMMENT '融资归还时间' AFTER `back_money`;
ALTER TABLE `bond_buy_log`
MODIFY COLUMN `remarks` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注' AFTER `oper_time`;