ALTER TABLE `dili_orders`.`weighing_bill`
    ADD COLUMN `buyer_certificate_number` varchar(40) NULL COMMENT '买家证件号' AFTER `buyer_agent_name`,
    ADD COLUMN `seller_certificate_number` varchar(40) NULL COMMENT '卖家证件号' AFTER `seller_agent_name`;
