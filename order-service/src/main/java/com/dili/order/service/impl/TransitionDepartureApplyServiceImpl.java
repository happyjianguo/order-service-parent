package com.dili.order.service.impl;

import com.dili.order.domain.TransitionDepartureApply;
import com.dili.order.mapper.TransitionDepartureApplyMapper;
import com.dili.order.service.TransitionDepartureApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
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
        transitionDepartureApply.setBeginTime(getTodayStart());
        transitionDepartureApply.setEndTime(getTodayEnd());
        return getActualDao().getOneByCustomerID(transitionDepartureApply);
    }

    @Override
    public PageOutput<List<TransitionDepartureApply>> listByQueryParams(TransitionDepartureApply transitionDepartureApply) {
        Integer page = transitionDepartureApply.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (transitionDepartureApply.getRows() != null && transitionDepartureApply.getRows() >= 1) {
            PageHelper.startPage(page, transitionDepartureApply.getRows());
        }
        List<TransitionDepartureApply> list = getActualDao().listByQueryParams(transitionDepartureApply);
        Long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
        int totalPage = list instanceof Page ? ((Page) list).getPages() : 1;
        int pageNum = list instanceof Page ? ((Page) list).getPageNum() : 1;
        PageOutput<List<TransitionDepartureApply>> output = PageOutput.success();
        output.setData(list).setPageNum(pageNum).setTotal(total.intValue()).setPageSize(transitionDepartureApply.getPage()).setPages(totalPage);
        return output;
    }

    /**
     * 获取一天的开始时间
     */
    public Date getTodayStart() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();

    }

    /**
     * 获取一天的结束时间
     */
    public Date getTodayEnd() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }


}