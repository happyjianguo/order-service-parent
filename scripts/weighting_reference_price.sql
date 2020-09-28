ALTER TABLE `dili_orders`.`weighing_reference_price`
    ADD COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id' FIRST,
    ADD COLUMN `settlement_day` varchar(20) NOT NULL COMMENT '结算日期，yyyy-MM-dd ex:2020-10-10' AFTER `settlement_time`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`id`) USING BTREE,
    ADD UNIQUE INDEX `middle_uni_inx`(`market_id`, `goods_id`, `trade_type`, `settlement_day`) USING BTREE COMMENT '确定一条唯一参考价数据';
