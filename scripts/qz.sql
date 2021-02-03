ALTER TABLE `dili_orders`.`goods_reference_price_setting`
ADD COLUMN `trade_type` tinyint(2) NOT NULL DEFAULT 1 COMMENT '交易规则类型，见枚举 ReferencePriceSettingTradeType' AFTER `goods_name`;
