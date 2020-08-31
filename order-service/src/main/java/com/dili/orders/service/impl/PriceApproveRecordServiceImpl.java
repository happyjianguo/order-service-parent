package com.dili.orders.service.impl;

import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.mapper.PriceApproveRecordMapper;
import com.dili.orders.service.PriceApproveRecordService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-08-30 14:29:35.
 */
@Service
public class PriceApproveRecordServiceImpl extends BaseServiceImpl<PriceApproveRecord, Long> implements PriceApproveRecordService {

    public PriceApproveRecordMapper getActualDao() {
        return (PriceApproveRecordMapper)getDao();
    }
}