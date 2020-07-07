package com.dili.orders.service.impl;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.mapper.WeighingStatementMapper;
import com.dili.orders.service.WeighingStatementService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-19 14:43:53.
 */
@Service
public class WeighingStatementServiceImpl extends BaseServiceImpl<WeighingStatement, Long> implements WeighingStatementService {

    public WeighingStatementMapper getActualDao() {
        return (WeighingStatementMapper)getDao();
    }
}