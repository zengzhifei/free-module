CREATE TABLE `operate_record`
(
    `id`      bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type`    smallint(4)         NOT NULL DEFAULT '0' COMMENT '业务类型',
    `main_id` varchar(50)         NOT NULL DEFAULT '' COMMENT '业务id',
    `action`  varchar(100)        NOT NULL DEFAULT '' COMMENT '操作行为',
    `content` varchar(5120)       NOT NULL DEFAULT '' COMMENT '操作内容',
    `ext`     text COMMENT '操作扩展',
    `user`    varchar(100)        NOT NULL DEFAULT '' COMMENT '操作人',
    `time`    bigint(13)          NOT NULL DEFAULT '0' COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_type_main_id` (`type`, `main_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='操作记录表';