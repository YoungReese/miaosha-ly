------------------------------
------- 秒杀数据库设计 ---------
------------------------------

-------------------------------
-- Table structure for goods
-------------------------------
CREATE TABLE IF NOT EXISTS `goods` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
    `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
    `goods_img` varchar(64) DEFAULT NULL COMMENT '商品的图片',
    `goods_detail` longtext COMMENT '商品的详情介绍',
    `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
    `goods_stock` int(11) DEFAULT '0' COMMENT '商品库存，-1表示没有限制',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-------------------------------
-- Records of goods
-------------------------------
INSERT INTO `goods` VALUES ('1', 'iphoneX', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphonex.png', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', '8765.00', '10000');
INSERT INTO `goods` VALUES ('2', '华为Meta9', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/meta10.png', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '3212.00', '-1');
INSERT INTO `goods` VALUES ('3', 'iphone8', 'Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphone8.png', 'Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', '5589.00', '10000');
INSERT INTO `goods` VALUES ('4', '小米6', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/mi6.png', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '3212.00', '10000');


-------------------------------
-- Table of miaosha_goods
-------------------------------
CREATE TABLE IF NOT EXISTS `miaosha_goods` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
    `goods_id` bigint(20) DEFAULT NULL COMMENT '商品Id',
    `miaosha_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
    `stock_count` int(11) DEFAULT NULL COMMENT '库存数量',
    `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
    `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-------------------------------
-- Records of miaosha_goods
-------------------------------
INSERT INTO `miaosha_goods` VALUES ('1', '1', '0.01', '9', '2022-03-01 21:51:23', '2022-03-15 21:51:27');
INSERT INTO `miaosha_goods` VALUES ('2', '2', '0.01', '9', '2022-03-01 21:40:14', '2022-03-15 14:00:24');
INSERT INTO `miaosha_goods` VALUES ('3', '3', '0.01', '9', '2022-03-01 21:40:14', '2022-03-15 14:00:24');
INSERT INTO `miaosha_goods` VALUES ('4', '4', '0.01', '9', '2022-03-01 21:40:14', '2022-03-15 14:00:24');


-------------------------------
-- Table of order_info
-------------------------------
CREATE TABLE IF NOT EXISTS `order_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
    `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
    `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '收获地址ID',
    `goods_name` varchar(16) DEFAULT NULL COMMENT '冗余过来的商品名称',
    `goods_count` int(11) DEFAULT '0' COMMENT '商品数量',
    `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
    `order_channel` tinyint(4) DEFAULT '0' COMMENT '1pc，2android，3ios',
    `status` tinyint(4) DEFAULT '0' COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
    `create_date` datetime DEFAULT NULL COMMENT '订单的创建时间',
    `pay_date` datetime DEFAULT NULL COMMENT '支付时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1565 DEFAULT CHARSET=utf8mb4;


-------------------------------
-- Table of miaosha_order
-------------------------------
CREATE TABLE IF NOT EXISTS `miaosha_order` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
    `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
    `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `u_uid_gid` (`user_id`,`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1551 DEFAULT CHARSET=utf8mb4;

