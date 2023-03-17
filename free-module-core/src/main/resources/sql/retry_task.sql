CREATE TABLE `retry_task`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `main_id`     varchar(50)         NOT NULL DEFAULT '' COMMENT '主id',
    `type`        smallint(4)         NOT NULL DEFAULT '0' COMMENT '补偿任务类型',
    `content`     text COMMENT '补偿任务内容',
    `max_times`   int(5)              NOT NULL DEFAULT '0' COMMENT '重试次数',
    `retry_times` int(5)              NOT NULL DEFAULT '0' COMMENT '重试次数',
    `status`      smallint(4)         NOT NULL DEFAULT '0' COMMENT '任务状态',
    `ext`         text COMMENT '扩展信息',
    `create_time` bigint(13)          NOT NULL DEFAULT '0' COMMENT '创建时间',
    `update_time` bigint(13)          NOT NULL DEFAULT '0' COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_main_id` (`main_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='补偿任务表';