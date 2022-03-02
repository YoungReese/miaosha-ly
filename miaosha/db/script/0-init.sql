create database if not exists miaosha default charset = utf8mb4;
use miaosha;

-- 测试数据库连接的表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `name` varchar(64) unsigned NOT NULL DEFAULT '' COMMENT '用户姓名',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




-- CREATE TABLE IF NOT EXISTS `user` (
--     `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
--     `tab_name` int(8) unsigned NOT NULL DEFAULT 0 COMMENT 'tab名字枚举，比如：2：有料',
--     `tip_msg` varchar(128) NOT NULL COMMENT 'tip信息',
--     `tip_jump_url` varchar(128) NOT NULL DEFAULT '' COMMENT 'tip跳转链接',
--     `status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '状态，1 生效，2 删除',
--     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;