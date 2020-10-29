package com.dili.orders.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.WeighingStatementAppletDto;
import com.dili.orders.dto.WeighingStatementAppletQuery;
import com.dili.orders.dto.WeighingStatementAppletStateCountDto;
import com.dili.orders.mapper.WeighingStatementMapper;
import com.dili.orders.service.WeighingStatementService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:43:53.
 */
@Service
public class WeighingStatementServiceImpl extends BaseServiceImpl<WeighingStatement, Long> implements WeighingStatementService {

    public WeighingStatementMapper getActualDao() {
        return (WeighingStatementMapper) getDao();
    }

    @Override
    public PageOutput<List<WeighingStatementAppletDto>> listApplet(WeighingStatementAppletQuery query) {
        Integer page = query.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (query.getRows() != null && query.getRows() >= 1) {
            // 为了线程安全,请勿改动下面两行代码的顺序
            PageHelper.startPage(page, query.getRows());
        }
        List<WeighingStatementAppletDto> list = this.getActualDao().listApplet(query);
        Page<WeighingStatementAppletDto> pageList = (Page<WeighingStatementAppletDto>) list;
        long total = pageList.getTotal();
        return PageOutput.success().setData(list).setTotal(total).setPageNum(pageList.getPageNum()).setPageSize(pageList.getPageSize());
    }

    @Override
    public List<WeighingStatementAppletStateCountDto> stateCountStatistics(WeighingStatementAppletQuery query) {
        return this.getActualDao().selectStateCount(query);
    }
}