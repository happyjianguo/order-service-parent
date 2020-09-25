ALTER TABLE `dili_orders`.`weighing_settlement_bill_daily`
ADD COLUMN `settlement_day` varchar(20) NOT NULL COMMENT '结算日期，yyyy-MM-dd ex:2020-10-10' AFTER `settlement_time`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`market_id`, `goods_id`, `trade_type`, `settlement_day`) USING BTREE;

ALTER TABLE `dili_orders`.`weighing_settlement_bill_daily`
    ADD COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键' FIRST,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`id`) USING BTREE,
    ADD UNIQUE INDEX `middle_uni_inx`(`market_id`, `goods_id`, `trade_type`, `settlement_day`) USING BTREE;
