package com.dili.orders.mapper;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.ss.base.MyMapper;

import java.util.List;

public interface ComprehensiveFeeMapper extends MyMapper<ComprehensiveFee> {

    List<ComprehensiveFee> listByQueryParams(ComprehensiveFee comprehensiveFee);
}