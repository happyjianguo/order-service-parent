package com.dili.order.service.impl;

import com.dili.order.domain.WeighingBillOperationRecord;
import com.dili.order.mapper.WeighingBillOperationRecordMapper;
import com.dili.order.service.WeighingBillOperationRecordService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-19 14:44:24.
 */
@Service
public class WeighingBillOperationRecordServiceImpl extends BaseServiceImpl<WeighingBillOperationRecord, Long> implements WeighingBillOperationRecordService {

    public WeighingBillOperationRecordMapper getActualDao() {
        return (WeighingBillOperationRecordMapper)getDao();
    }
}