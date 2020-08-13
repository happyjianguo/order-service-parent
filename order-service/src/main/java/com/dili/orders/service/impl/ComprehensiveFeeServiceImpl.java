package com.dili.orders.service.impl;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.mapper.ComprehensiveFeeMapper;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.ComprehensiveFeeService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComprehensiveFeeServiceImpl extends BaseServiceImpl<ComprehensiveFee,Long> implements ComprehensiveFeeService {

    @Autowired
    private UidRpc uidRpc;

    public ComprehensiveFeeMapper getActualDao(){return (ComprehensiveFeeMapper)getDao();}

    @Override
    public PageOutput<List<ComprehensiveFee>> listByQueryParams(ComprehensiveFee comprehensiveFee) {
        Integer page = comprehensiveFee.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (comprehensiveFee.getRows() != null && comprehensiveFee.getRows() >= 1) {
            PageHelper.startPage(page, comprehensiveFee.getRows());
        }

        List<ComprehensiveFee> list = getActualDao().listByQueryParams(comprehensiveFee);
        Long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
        int totalPage = list instanceof Page ? ((Page) list).getPages() : 1;
        int pageNum = list instanceof Page ? ((Page) list).getPageNum() : 1;
        PageOutput<List<ComprehensiveFee>> output = PageOutput.success();
        output.setData(list).setPageNum(pageNum).setTotal(total.intValue()).setPageSize(comprehensiveFee.getPage()).setPages(totalPage);
        return output;
    }

    @Override
    public BaseOutput<ComprehensiveFee> insertComprehensiveFee(ComprehensiveFee comprehensiveFee) {
        //设置检查收费单为未结算
        comprehensiveFee.setOrderStatus(1);
        //设置单据类型为检测收费
        comprehensiveFee.setOrderType(1);
        //设置默认版本号为0
        comprehensiveFee.setVersion(0);
        //根据uid设置结算单的code
        comprehensiveFee.setCode(uidRpc.bizNumber("sg_comprehensive_fee").getData());
        int insert = getActualDao().insert(comprehensiveFee);
        if (insert <= 0) {
            throw new RuntimeException("检测收费新增-->创建检测收费单失败");
        }
        return BaseOutput.successData(comprehensiveFee);
    }
}
