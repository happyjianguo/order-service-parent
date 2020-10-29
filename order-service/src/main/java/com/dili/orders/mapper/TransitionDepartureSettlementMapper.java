package com.dili.orders.mapper;

import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.ss.base.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;

public interface TransitionDepartureSettlementMapper extends MyMapper<TransitionDepartureSettlement> {
    List<TransitionDepartureSettlement> listByQueryParams(TransitionDepartureSettlement transitionDepartureSettlement);

    List<TransitionDepartureSettlement> scheduleUpdateSelect(TransitionDepartureSettlement transitionDepartureSettlement);

    void scheduleUpdate(@Param("set") HashSet<Long> settlementIds);

    TransitionDepartureSettlement getOneByCode(String code);
}