package com.dili.order.service.impl;

import com.dili.order.domain.TransitionDepartureSettlement;
import com.dili.order.mapper.TransitionDepartureApplyMapper;
import com.dili.order.mapper.TransitionDepartureSettlementMapper;
import com.dili.order.service.TransitionDepartureSettlementService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@Service
public class TransitionDepartureSettlementServiceImpl extends BaseServiceImpl<TransitionDepartureSettlement, Long> implements TransitionDepartureSettlementService {


    public TransitionDepartureSettlementMapper getActualDao() {
        return (TransitionDepartureSettlementMapper) getDao();
    }

    @Autowired
    private TransitionDepartureApplyMapper transitionDepartureApplyMapper;

    @Override
    public PageOutput<List<TransitionDepartureSettlement>> listByQueryParams(TransitionDepartureSettlement transitionDepartureSettlement) {
        Integer page = transitionDepartureSettlement.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (transitionDepartureSettlement.getRows() != null && transitionDepartureSettlement.getRows() >= 1) {
            PageHelper.startPage(page, transitionDepartureSettlement.getRows());
        }
        List<TransitionDepartureSettlement> list = getActualDao().listByQueryParams(transitionDepartureSettlement);
        Long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
        int totalPage = list instanceof Page ? ((Page) list).getPages() : 1;
        int pageNum = list instanceof Page ? ((Page) list).getPageNum() : 1;
        PageOutput<List<TransitionDepartureSettlement>> output = PageOutput.success();
        output.setData(list).setPageNum(pageNum).setTotal(total.intValue()).setPageSize(transitionDepartureSettlement.getPage()).setPages(totalPage);
        return output;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void scheduleUpdate(TransitionDepartureSettlement transitionDepartureSettlement) throws ParseException {
        //拿到前一天的0时和23:59:59时
        Map<String, String> beforeDate = getBeforeDate();
        //设置查询参数
        //查询日期使用的是Date类型
        transitionDepartureSettlement.setBeginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beforeDate.get("beginTime")));
        transitionDepartureSettlement.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beforeDate.get("endTime")));
        //根据日期筛选出前一天的所有未结算的单子
        List<TransitionDepartureSettlement> list = getActualDao().scheduleUpdateSelect(transitionDepartureSettlement);
        HashSet<Long> applyIds = new HashSet<>();
        HashSet<Long> settlementIds = new HashSet<>();
        //循环遍历，获取申请单id，更新拿到申请id和结算单id
        for (int i = 0; i < list.size(); i++) {
            TransitionDepartureSettlement transitionDepartureSettlement1 = list.get(i);
            applyIds.add(transitionDepartureSettlement1.getApplyId());
            settlementIds.add(transitionDepartureSettlement1.getId());
        }
        //如果申请单id集合和结算单id集合不为空则更新
        if (CollectionUtils.isNotEmpty(applyIds) && CollectionUtils.isNotEmpty(settlementIds)) {
            transitionDepartureApplyMapper.scheduleUpdate(applyIds);
            getActualDao().scheduleUpdate(settlementIds);
        }
    }

    /**
     * 定时任务在次日的凌晨五分执行，拿到前一天的日期
     *
     * @return
     */
    private Map<String, String> getBeforeDate() {
        Map<String, String> map = new HashMap<>();
        //格式化一下时间
        DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //当前时间
        Date dNow = new Date();

        //得到日历
        Calendar calendar = Calendar.getInstance();

        //把当前时间赋给日历
        calendar.setTime(dNow);

        //设置为前一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        //得到前一天的时间
        Date dBefore = calendar.getTime();

        //格式化前一天
        String defaultStartDate = dateFmt.format(dBefore);

        //前一天的0时
        defaultStartDate = defaultStartDate.substring(0, 10) + " 00:00:00";
        map.put("beginTime", defaultStartDate);

        //前一天的24时
        String defaultEndDate = defaultStartDate.substring(0, 10) + " 23:59:59";

        map.put("endTime", defaultEndDate);

        return map;
    }

}