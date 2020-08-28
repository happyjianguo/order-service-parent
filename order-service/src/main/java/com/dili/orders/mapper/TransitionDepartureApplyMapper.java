package com.dili.orders.mapper;

import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.ss.base.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;

public interface TransitionDepartureApplyMapper extends MyMapper<TransitionDepartureApply> {
    TransitionDepartureApply getOneByCustomerID(TransitionDepartureApply transitionDepartureApply);

    List<TransitionDepartureApply> listByQueryParams(TransitionDepartureApply transitionDepartureApply);

    void scheduleUpdate(@Param("set") HashSet<Long> applyIds);

    List<TransitionDepartureApply> getListByCustomerId(TransitionDepartureApply transitionDepartureApply);
}