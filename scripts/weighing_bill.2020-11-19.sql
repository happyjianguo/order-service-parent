ALTER TABLE `dili_orders`.`weighing_bill`
    DROP INDEX `idx_serial_no`,
    ADD INDEX `idx_serial_no`(`serial_no`) USING BTREE;
