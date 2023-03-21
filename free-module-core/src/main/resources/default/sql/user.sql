CREATE TABLE `user`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `username`    varchar(50)         NOT NULL DEFAULT '' COMMENT '用户名称',
    `password`    varchar(256)        NOT NULL DEFAULT '' COMMENT '密码',
    `uuid`        varchar(50)         NOT NULL DEFAULT '' COMMENT 'uuid',
    `enable`      tinyint(1)          NOT NULL DEFAULT '0' COMMENT '状态',
    `roles`       varchar(500)        NOT NULL DEFAULT '' COMMENT '角色',
    `create_time` int(11)             NOT NULL DEFAULT '0' COMMENT '创建时间',
    `update_time` int(11)             NOT NULL DEFAULT '0' COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_uuid` (`uuid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';