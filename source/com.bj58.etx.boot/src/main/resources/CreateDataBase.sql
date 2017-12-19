CREATE DATABASE /*!32312 IF NOT EXISTS*/`dbwww58com_zpetx` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */;

USE `dbwww58com_zpetx`;

DROP TABLE IF EXISTS `t_async_log`;

CREATE TABLE `t_async_log` (
  `id` bigint(20) DEFAULT NULL COMMENT '主键',
  `txid` bigint(20) DEFAULT NULL COMMENT '事务组id',
  `componet` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件名称',
  `state` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
  `addtime` bigint(20) DEFAULT NULL COMMENT '添加时间',
  `modifytime` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `data` blob COMMENT '二进制上下文',
  `count` int(11) DEFAULT NULL COMMENT '执行次数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `t_sync_log`;

CREATE TABLE `t_sync_log` (
  `id` bigint(20) DEFAULT NULL COMMENT '主键',
  `txid` bigint(20) DEFAULT NULL COMMENT '事务组id',
  `componet` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件名称',
  `state` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
  `addtime` bigint(20) DEFAULT NULL COMMENT '添加时间',
  `modifytime` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `data` blob COMMENT '二进制上文',
  `cancelcount` int(11) DEFAULT NULL COMMENT '取消操作执行次数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `t_tx`;

CREATE TABLE `t_tx` (
  `id` bigint(20) DEFAULT NULL COMMENT '主键',
  `flowtype` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
  `state` int(11) DEFAULT NULL COMMENT '状态',
  `addtime` bigint(20) DEFAULT NULL COMMENT '添加时间',
  `modifytime` bigint(20) DEFAULT NULL COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;