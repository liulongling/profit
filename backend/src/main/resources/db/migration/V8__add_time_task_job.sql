CREATE TABLE `time_task_job` (
  `inc_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_name`    varchar(64)  DEFAULT NULL COMMENT '任务名称',
  `sync_time`   datetime     NOT NULL COMMENT '同步时间',
  `sync_flag`   int(2)       DEFAULT NULL COMMENT '同步状态1:成功，0:失败',
  `create_time` datetime     NOT NULL COMMENT '创建时间',
  `update_time` datetime     NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`inc_id`)     USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务日志信息';

-- 定时任务日志类
CREATE TABLE `time_task_log`
(
  `inc_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_name`    varchar(64)  DEFAULT NULL COMMENT '任务名称',
  `sync_time`   datetime     NOT NULL COMMENT '同步时间',
  `sync_count`  int(64)      DEFAULT NULL COMMENT '同步数量',
  `sync_flag`   int(2)       DEFAULT NULL COMMENT '同步状态1:成功，0:失败',
  `error_msg`   varchar(256) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime     NOT NULL COMMENT '创建时间',
  `update_time` datetime     NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`inc_id`)     USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='定时任务日志信息';

CREATE TABLE `bond_statistics_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stock` double unsigned NOT NULL DEFAULT '0' COMMENT '持股市值',
  `ready` double NOT NULL DEFAULT '0' COMMENT '现金',
  `liability` double DEFAULT NULL COMMENT '负债',
  `profit` double DEFAULT NULL COMMENT '盈亏',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='股票信息';
