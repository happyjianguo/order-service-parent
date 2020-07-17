package com.dili.orders.service;

import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;

import java.text.ParseException;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
public interface TransitionDepartureSettlementService extends BaseService<TransitionDepartureSettlement, Long> {
    /**
     * 根据页面参数查询
     *
     * @param transitionDepartureSettlement
     * @return
     */
    PageOutput<List<TransitionDepartureSettlement>> listByQueryParams(TransitionDepartureSettlement transitionDepartureSettlement);

    /**
     * 定时任务，每天凌晨12点更新当天为结算的单子，支付状态更改为已关闭状态
     */
    void scheduleUpdate() throws ParseException;

    /**
     * 新增
     */
    BaseOutput<TransitionDepartureSettlement> insertTransitionDepartureSettlement(TransitionDepartureSettlement transitionDepartureSettlement);

    /**
     * 支付
     */
    BaseOutput pay(Long id, String password, Long marketId, Long departmentId, String operatorCode, Long operatorId, String operatorName);

    /**
     * 撤销
     */
    BaseOutput<TransitionDepartureSettlement> revocator(com.dili.orders.domain.TransitionDepartureSettlement transitionDepartureSettlement);
}