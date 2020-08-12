package com.dili.orders.service;

import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
public interface TransitionDepartureApplyService extends BaseService<TransitionDepartureApply, Long> {
    /**
     * 根据客户id查询客户最新审批通过该的审批单，如果是未结算的，那带出结算单的相关信息，如果是已撤销，那就不带出
     *
     * @param transitionDepartureApply
     * @return
     */
    TransitionDepartureApply getOneByCustomerID(TransitionDepartureApply transitionDepartureApply, Long marketId, Long departmentId);

    /**
     * 根据页面上的参数查询
     *
     * @param transitionDepartureApply
     * @return
     */
    PageOutput<List<TransitionDepartureApply>> listByQueryParams(TransitionDepartureApply transitionDepartureApply);
}