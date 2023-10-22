ALTER TABLE `bond_info`
ADD COLUMN `old_price` double unsigned COMMENT '昨日收盘价' AFTER `price`,
ADD COLUMN `status` tinyint(0) UNSIGNED DEFAULT NULL COMMENT '0:上架 -1：下架' AFTER `position`;