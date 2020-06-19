package com.dili.order.service.impl;

import com.dili.order.domain.TransitionDepartureApply;
import com.dili.order.mapper.TransitionDepartureApplyMapper;
import com.dili.order.service.TransitionDepartureApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
@Service
public class TransitionDepartureApplyServiceImpl extends BaseServiceImpl<TransitionDepartureApply, Long> implements TransitionDepartureApplyService {

    public TransitionDepartureApplyMapper getActualDao() {
        return (TransitionDepartureApplyMapper) getDao();
    }

    @Override
    public TransitionDepartureApply getOneByCustomerID(TransitionDepartureApply transitionDepartureApply) {
        return getActualDao().getOneByCustomerID(transitionDepartureApply);
    }

    @Override
    public List<TransitionDepartureApply> listByQueryParams(TransitionDepartureApply transitionDepartureApply) {
        PageHelper.startPage(transitionDepartureApply.getPage() == null ? 1 : transitionDepartureApply.getPage(), transitionDepartureApply.getRows() == null ? 10 : transitionDepartureApply.getRows());
        return getActualDao().listByQueryParams(transitionDepartureApply);
    }
}