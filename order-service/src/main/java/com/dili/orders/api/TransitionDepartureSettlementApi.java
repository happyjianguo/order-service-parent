package com.dili.orders.api;

import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.dto.TradeTypeDto;
import com.dili.assets.sdk.enums.BusinessChargeItemEnum;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.assets.sdk.rpc.TradeTypeRpc;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.orders.dto.AccountSimpleResponseDto;
import com.dili.orders.dto.BalanceResponseDto;
import com.dili.orders.dto.MyBusinessType;
import com.dili.orders.glossary.BizTypeEnum;
import com.dili.orders.rpc.CardRpc;
import com.dili.orders.service.TransitionDepartureApplyService;
import com.dili.orders.service.TransitionDepartureSettlementService;
import com.dili.orders.utils.WebUtil;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@RestController
@RequestMapping("/api/transitionDepartureSettlement")
@Slf4j
public class TransitionDepartureSettlementApi {

    @Autowired
    TransitionDepartureSettlementService transitionDepartureSettlementService;

    @Autowired
    private ChargeRuleRpc chargeRuleRpc;

    @Autowired
    private TransitionDepartureApplyService transitionDepartureApplyService;

    @Autowired
    private BusinessChargeItemRpc businessChargeItemRpc;

    @Autowired
    private CardRpc cardRpc;

    @Autowired
    private TradeTypeRpc tradeTypeRpc;

    /**
     * @param transitionDepartureSettlement
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/listPage", method = {RequestMethod.POST})
    public String listPage(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) throws Exception {
        return transitionDepartureSettlementService.listEasyuiPageByExample(transitionDepartureSettlement, true).toString();

    }


    /**
     * 根据参数查询数据
     *
     * @param transitionDepartureSettlement
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/listByQueryParams", method = {RequestMethod.POST})
    public PageOutput<List<TransitionDepartureSettlement>> listByQueryParams(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        //如果没有传入时间范围，那默认展示当天的数据
        //设置开始时间
//        if (Objects.isNull(transitionDepartureSettlement.getBeginTime())) {
//            transitionDepartureSettlement.setBeginTime(getBeginDate());
//        }
//        //设置结束时间
//        if (Objects.isNull(transitionDepartureSettlement.getEndTime())) {
//            transitionDepartureSettlement.setEndTime(getEndDate());
//        }
        return transitionDepartureSettlementService.listByQueryParams(transitionDepartureSettlement);
    }

    /**
     * 根据id查询结算单信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getOneById/{id}", method = {RequestMethod.GET})
    BaseOutput<TransitionDepartureSettlement> getOneById(@PathVariable(value = "id") Long id) {
        TransitionDepartureSettlement transitionDepartureSettlement = transitionDepartureSettlementService.get(id);
        //获取余额返回，类型为元
        BaseOutput<AccountSimpleResponseDto> oneAccountCard1 = cardRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
        if (oneAccountCard1.isSuccess()) {
            //获取账户资金信息
            BalanceResponseDto accountFund = oneAccountCard1.getData().getAccountFund();
            //设置余额，并且返回为元
            transitionDepartureSettlement.setCustomerBalance(String.format("%.2f", accountFund.getBalance().doubleValue() / 100));
        } else {
            transitionDepartureSettlement.setCustomerBalance("0");
        }
        BaseOutput<TradeTypeDto> tradeTypeDtoBaseOutput = this.tradeTypeRpc.get(Long.valueOf(transitionDepartureSettlement.getTransTypeId()));
        if (!tradeTypeDtoBaseOutput.isSuccess()) {
            return BaseOutput.failure(tradeTypeDtoBaseOutput.getMessage());
        }
        transitionDepartureSettlement.setTransTypeName(tradeTypeDtoBaseOutput.getData().getName());
        return BaseOutput.successData(transitionDepartureSettlement);
    }

    /**
     * 新增TransitionDepartureSettlement
     *
     * @param transitionDepartureSettlement
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> insert(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        try {
            if (transitionDepartureSettlement.getCreateTime() == null) {
                transitionDepartureSettlement.setCreateTime(LocalDateTime.now());
            }
            transitionDepartureSettlementService.insertSelective(transitionDepartureSettlement);
            return BaseOutput.successData(transitionDepartureSettlement);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure("新增失败");
        }
    }

    /**
     * 修改TransitionDepartureSettlement
     *
     * @param transitionDepartureSettlement
     * @return BaseOutput
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public BaseOutput update(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement, @RequestParam(value = "marketId") Long marketId) {
        try {
//            transitionDepartureSettlementService.updateSelective(transitionDepartureSettlement);
            transitionDepartureSettlementService.updateSettlementAndApply(transitionDepartureSettlement, marketId);
            return BaseOutput.successData(transitionDepartureSettlement);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure(e.getMessage());
        }

    }

    /**
     * 删除TransitionDepartureSettlement
     *
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    public BaseOutput delete(Long id) {
        if (id == null) {
            return BaseOutput.failure("删除失败，id不能为空");
        }
        try {
            transitionDepartureSettlementService.delete(id);
            return BaseOutput.success("删除成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure("删除失败");
        }
    }

    /**
     * 定时任务，每天凌晨12点更新当天为结算的单子，支付状态更改为已关闭状态
     *
     * @return
     */
    @RequestMapping(value = "/scheduleUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public void scheduleUpdate() {
        try {
            transitionDepartureSettlementService.scheduleUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 新增一个结算单
     *
     * @param transitionDepartureSettlement
     * @return
     */
    @RequestMapping(value = "/insertTransitionDepartureSettlement", method = {RequestMethod.POST})
    @BusinessLogger(businessType = "trading_orders", content = "转离场缴费新增,转离场申请单号：${businessCode},结算单号：${statementSerialNo},所属市场id：${marketId}，操作员id:${operatorId}", operationType = "edit", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput<TransitionDepartureSettlement> insertTransitionDepartureSettlement(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement, Long marketId, HttpServletRequest request) {
        try {
            LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
            return transitionDepartureSettlementService.insertTransitionDepartureSettlement(transitionDepartureSettlement, marketId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 结算单支付
     *
     * @return
     */
    @RequestMapping(value = "/pay", method = {RequestMethod.POST})
    @BusinessLogger(businessType = "trading_orders", content = "转离场缴费,转离场申请单号：${businessCode},结算单号：${statementSerialNo},所属市场id：${marketId}，操作员id:${operatorId}", operationType = "edit", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput<TransitionDepartureSettlement> pay(@RequestParam(value = "id") Long id, @RequestParam(value = "password") String password, @RequestParam(value = "marketId") Long marketId, @RequestParam(value = "departmentId") Long departmentId, @RequestParam(value = "operatorCode") String operatorCode, @RequestParam(value = "operatorId") Long operatorId, @RequestParam(value = "operatorName") String operatorName, @RequestParam(value = "operatorUserName") String operatorUserName, HttpServletRequest request) {
        try {
            LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
            return transitionDepartureSettlementService.pay(id, password, marketId, departmentId, operatorCode, operatorId, operatorName, operatorUserName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 撤销交易
     *
     * @return
     */
    @RequestMapping(value = "/revocator", method = {RequestMethod.POST})
    @BusinessLogger(businessType = "trading_orders", content = "转离场撤销缴费,转离场申请单号：${businessCode},结算单号：${statementSerialNo},所属市场id：${marketId}，操作员id:${operatorId}", operationType = "edit", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput<TransitionDepartureSettlement> revocator(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement, @RequestParam(value = "revocatorId") Long revocatorId, @RequestParam(value = "revocatorPassword") String revocatorPassword, HttpServletRequest request) {
        try {
            LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
            return transitionDepartureSettlementService.revocator(transitionDepartureSettlement, revocatorId, revocatorPassword);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 根据code获取一条缴费记录
     *
     * @return
     */
    @RequestMapping(value = "/getOneByCode", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> getOneByCode(@RequestParam String code) {
        try {
            return transitionDepartureSettlementService.getOneByCode(code);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure(e.getMessage());
        }
    }


    /**
     * 获取计费规则所得到的的金额
     * 目前设计的是，一个业务类型只对应一个收费项，将转场，离场分开的
     *
     * @param netWeight    净重
     * @param marketId     市场id
     * @param departmentId 部门id
     * @param id           申请单id
     * @return
     */
    @RequestMapping(value = "/fee", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput getFee(BigDecimal netWeight, Long marketId, Long departmentId, Long id, Long carTypeId) {
        if (Objects.isNull(id)) {
            return BaseOutput.failure("申请单id不能为空");
        }
        //必须根据申请单来判定是转场还是离场，因为在新增结算单的时候，可能结算单还没有insert，所有是不存在的
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(id);
        if (Objects.isNull(transitionDepartureApply)) {
            return BaseOutput.failure("未能找到对应申请单");
        }
        QueryFeeInput queryFeeInput = new QueryFeeInput();
        Map<String, Object> map = new HashMap<>();
        //设置市场id
        queryFeeInput.setMarketId(marketId);
        //判断是转场还是离场。收费项不同
//        if (Objects.equals(transitionDepartureApply.getBizType(), 1)) {
        if (Objects.equals(transitionDepartureApply.getBizType(), BizTypeEnum.TRANSITION.getCode())) {
            //设置业务类型
//            queryFeeInput.setBusinessType("ZC_PAY");
            queryFeeInput.setBusinessType(MyBusinessType.ZCPAY.getCode());
            //根据业务类型获取收费项
            BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
            //业务类型
//            businessChargeItemDto.setBusinessType("ZC_PAY");
            businessChargeItemDto.setBusinessType(MyBusinessType.ZCPAY.getCode());
            //是否必须
            businessChargeItemDto.setIsRequired(YesOrNoEnum.YES.getCode());
            //收费
            businessChargeItemDto.setChargeType(BusinessChargeItemEnum.ChargeType.收费.getCode());
            //市场id
            businessChargeItemDto.setMarketId(marketId);
            //是否启用
            businessChargeItemDto.setIsEnable(YesOrNoEnum.YES.getCode());
            BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
            //判断是否成功
            if (!listBaseOutput.isSuccess()) {
                return listBaseOutput;
            }
            //设置收费项id
            queryFeeInput.setChargeItem(listBaseOutput.getData().get(0).getId());
        } else if (Objects.equals(transitionDepartureApply.getBizType(), BizTypeEnum.DEPARTURE.getCode())) {
            //设置业务类型
//            queryFeeInput.setBusinessType("LC_PAY");
            queryFeeInput.setBusinessType(MyBusinessType.LCPAY.getCode());
            //根据业务类型获取收费项
            BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
//            businessChargeItemDto.setBusinessType("LC_PAY");
            businessChargeItemDto.setBusinessType(MyBusinessType.LCPAY.getCode());
            businessChargeItemDto.setIsRequired(YesOrNoEnum.YES.getCode());
            businessChargeItemDto.setChargeType(BusinessChargeItemEnum.ChargeType.收费.getCode());
            businessChargeItemDto.setMarketId(marketId);
            //是否启用
            businessChargeItemDto.setIsEnable(YesOrNoEnum.YES.getCode());
            BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
            //判断是否成功
            if (!listBaseOutput.isSuccess()) {
                return listBaseOutput;
            }
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
        map2.put("carTypeId", carTypeId);
        //设置交易类型
        map2.put("transTypeId", transitionDepartureApply.getTransTypeId());
        //设置商品id
        map2.put("categoryId", transitionDepartureApply.getCategoryId());
        //设置净重指标
        map2.put("weight", netWeight);
        queryFeeInput.setConditionParams(map2);
        BaseOutput<QueryFeeOutput> queryFeeOutputBaseOutput = chargeRuleRpc.queryFee(queryFeeInput);
        if (queryFeeOutputBaseOutput.isSuccess()) {
            //获取之后保留两位小数
            BigDecimal totalFee = queryFeeOutputBaseOutput.getData().getTotalFee();
            totalFee = totalFee.setScale(2, RoundingMode.HALF_UP);
            queryFeeOutputBaseOutput.getData().setTotalFee(totalFee);
        }
        return queryFeeOutputBaseOutput;
    }

    /**
     * 获取当天开始时间
     *
     * @return
     */
    private Date getBeginDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero;
    }

    /**
     * 获取当天结束时间
     *
     * @return
     */
    private Date getEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date zero = calendar.getTime();
        return zero;
    }
}