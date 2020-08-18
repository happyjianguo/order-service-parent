package com.dili.orders.mapper;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.ss.base.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;

public interface ComprehensiveFeeMapper extends MyMapper<ComprehensiveFee> {

    List<ComprehensiveFee> listByQueryParams(ComprehensiveFee comprehensiveFee);

    List<ComprehensiveFee> scheduleUpdateSelect(ComprehensiveFee comprehensiveFee);

    void scheduleUpdate(@Param("set") HashSet<Long> cfIds);
}