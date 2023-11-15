ALTER TABLE `profit`.`bond_buy_log`
ADD COLUMN `financing` tinyint(2) NOT NULL DEFAULT 0 COMMENT '是否融资买入 0:不是 1：是' AFTER `remarks`;