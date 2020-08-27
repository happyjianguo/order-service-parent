package com.dili.orders.service.impl;

import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.enums.BusinessChargeItemEnum;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.mapper.TransitionDepartureApplyMapper;
import com.dili.orders.service.TransitionDepartureApplyService;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
@Service
public class TransitionDepartureApplyServiceImpl extends BaseServiceImpl<TransitionDepartureApply, Long> implements TransitionDepartureApplyService {

    @Autowired
    private BusinessChargeItemRpc businessChargeItemRpc;

    @Autowired
    private ChargeRuleRpc chargeRuleRpc;

    public TransitionDepartureApplyMapper getActualDao() {
        return (TransitionDepartureApplyMapper) getDao();
    }

    @Override
    public TransitionDepartureApply getOneByCustomerID(TransitionDepartureApply transitionDepartureApply, Long marketId, Long departmentId) {
        transitionDepartureApply.setBeginTime(LocalDate.now());
        transitionDepartureApply.setEndTime(LocalDate.now());
        TransitionDepartureApply oneByCustomerID = getActualDao().getOneByCustomerID(transitionDepartureApply);
        if (Objects.nonNull(oneByCustomerID.getTransitionDepartureSettlement()) && Objects.nonNull(oneByCustomerID.getTransitionDepartureSettlement().getNetWeight())) {
            oneByCustomerID.getTransitionDepartureSettlement().setChargeAmount(getFee(marketId, departmentId, oneByCustomerID, oneByCustomerID.getTransitionDepartureSettlement().getNetWeight()));
        }
        return oneByCustomerID;
    }

    @Override
    public PageOutput<List<TransitionDepartureApply>> listByQueryParams(TransitionDepartureApply transitionDepartureApply) {
        //需要默认为当天，如果时间不为空，则不设置
//        if (Objects.isNull(transitionDepartureApply.getBeginTime())) {
//            transitionDepartureApply.setBeginTime(LocalDate.now());
//        }
//        if (Objects.isNull(transitionDepartureApply.getEndTime())) {
//            transitionDepartureApply.setEndTime(LocalDate.now());
//        }
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

    private Long getFee(Long marketId, Long departmentId, TransitionDepartureApply transitionDepartureApply, Integer netWeight) {
        QueryFeeInput queryFeeInput = new QueryFeeInput();
        Map<String, Object> map = new HashMap<>();
        //设置市场id
        queryFeeInput.setMarketId(marketId);
        //判断是转场还是离场。收费项不同
        if (Objects.equals(transitionDepartureApply.getBizType(), 1)) {
            //设置业务类型
            queryFeeInput.setBusinessType("ZC_PAY");
            //根据业务类型获取收费项
            BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
            //业务类型
            businessChargeItemDto.setBusinessType("ZC_PAY");
            //是否必须
            businessChargeItemDto.setIsRequired(1);
            //收费
            businessChargeItemDto.setChargeType(BusinessChargeItemEnum.ChargeType.收费.getCode());
            //市场id
            businessChargeItemDto.setMarketId(marketId);
            BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
            //设置收费项id
            queryFeeInput.setChargeItem(listBaseOutput.getData().get(0).getId());
        } else if (Objects.equals(transitionDepartureApply.getBizType(), 2)) {
            //设置业务类型
            queryFeeInput.setBusinessType("LC_PAY");
            //根据业务类型获取收费项
            BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
            businessChargeItemDto.setBusinessType("LC_PAY");
            businessChargeItemDto.setIsRequired(1);
            businessChargeItemDto.setChargeType(BusinessChargeItemEnum.ChargeType.收费.getCode());
            businessChargeItemDto.setMarketId(marketId);
            BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
            //设置收费项id
            queryFeeInput.setChargeItem(listBaseOutput.getData().get(0).getId());
        }
        map.put("weight", netWeight);
        queryFeeInput.setCalcParams(map);
        //构建指标
        Map<String, Object> map2 = new HashMap();
        //设置部门信息
        map2.put("departmentId", departmentId);
        //设置市场信息
        map2.put("marketId", marketId);
        //设置客户信息
        map2.put("customerId", transitionDepartureApply.getCustomerId());
        //设置车辆类型，因为是可以变得，所以要从前台传
        map2.put("carTypeId", transitionDepartureApply.getCarTypeId());
        //设置交易类型
        map2.put("transTypeId", transitionDepartureApply.getTransTypeId());
        //设置商品id
        map2.put("categoryId", transitionDepartureApply.getCarTypeId());
        queryFeeInput.setConditionParams(map2);
        //保留两位小数后转成long类型
        BaseOutput<QueryFeeOutput> queryFeeOutputBaseOutput = chargeRuleRpc.queryFee(queryFeeInput);
        BigDecimal totalFee = queryFeeOutputBaseOutput.getData().getTotalFee();
        totalFee = totalFee.setScale(2, RoundingMode.HALF_UP);
        return totalFee.multiply(new BigDecimal(100)).longValue();
    }

}