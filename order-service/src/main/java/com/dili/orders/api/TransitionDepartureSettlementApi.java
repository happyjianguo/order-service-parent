package com.dili.orders.api;

import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.orders.service.TransitionDepartureApplyService;
import com.dili.orders.service.TransitionDepartureSettlementService;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@RestController
@RequestMapping("/api/transitionDepartureSettlement")
public class TransitionDepartureSettlementApi {

    @Autowired
    TransitionDepartureSettlementService transitionDepartureSettlementService;

    @Autowired
    private ChargeRuleRpc chargeRuleRpc;

    @Autowired
    private TransitionDepartureApplyService transitionDepartureApplyService;


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
        if (Objects.isNull(transitionDepartureSettlement.getBeginTime())) {
            transitionDepartureSettlement.setBeginTime(new Date());
        }
        //设置结束时间
        if (Objects.isNull(transitionDepartureSettlement.getEndTime())) {
            transitionDepartureSettlement.setEndTime(new Date());
        }
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
        return BaseOutput.successData(transitionDepartureSettlementService.get(id));
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
            e.printStackTrace();
            return BaseOutput.failure("新增失败" + e.getMessage());
        }
    }

    /**
     * 修改TransitionDepartureSettlement
     *
     * @param transitionDepartureSettlement
     * @return BaseOutput
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public BaseOutput update(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        try {
            transitionDepartureSettlementService.updateSelective(transitionDepartureSettlement);
            return BaseOutput.successData(transitionDepartureSettlement);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("修改失败" + e.getMessage());
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
            e.printStackTrace();
            return BaseOutput.failure("删除失败" + e.getMessage());
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
            e.printStackTrace();
        }
    }

    /**
     * 新增一个结算单
     *
     * @param transitionDepartureSettlement
     * @return
     */
    @RequestMapping(value = "/insertTransitionDepartureSettlement", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> insertTransitionDepartureSettlement(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        return transitionDepartureSettlementService.insertTransitionDepartureSettlement(transitionDepartureSettlement);
    }

    /**
     * 结算单支付
     *
     * @return
     */
    @RequestMapping(value = "/pay", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> pay(@RequestParam(value = "id") Long id, @RequestParam(value = "password") String password, @RequestParam(value = "marketId") Long marketId, @RequestParam(value = "departmentId") Long departmentId, @RequestParam(value = "operatorCode") String operatorCode, @RequestParam(value = "operatorId") Long operatorId, @RequestParam(value = "operatorName") String operatorName) {
        return transitionDepartureSettlementService.pay(id, password, marketId, departmentId, operatorCode, operatorId, operatorName);
    }

    /**
     * 撤销交易
     *
     * @return
     */
    @RequestMapping(value = "/revocator", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> revocator(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        return transitionDepartureSettlementService.revocator(transitionDepartureSettlement);
    }


    /**
     * 获取计费规则所得到的的金额
     *
     * @param netWeight    净重
     * @param marketId     市场id
     * @param departmentId 部门id
     * @param id           申请单id
     * @return
     */
    @RequestMapping(value = "/fee", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput getFee(BigDecimal netWeight, Long marketId, Long departmentId, Long id) {
        if (Objects.isNull(id)) {
            return BaseOutput.failure("申请单id不能为空");
        }
        //必须根据申请单来判定是转场还是离场，因为在新增结算单的时候，可能结算单还没有insert，所有是不存在的
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(id);
        if (Objects.isNull(transitionDepartureApply)) {
            return BaseOutput.failure("未能找到对应结算单");
        }
        QueryFeeInput queryFeeInput = new QueryFeeInput();
        Map<String, Object> map = new HashMap<>();
        //设置市场id
        queryFeeInput.setMarketId(marketId);
        //设置业务类型
        queryFeeInput.setBusinessType("ZLC_PAY");
        //判断是转场还是离场。收费项不同
        if (Objects.equals(transitionDepartureApply.getBizType(), 1)) {
            //设置收费项id
            queryFeeInput.setChargeItem(58L);
        } else if (Objects.equals(transitionDepartureApply.getBizType(), 2)) {
            queryFeeInput.setChargeItem(59L);
        }
        map.put("weight", netWeight);
        queryFeeInput.setCalcParams(map);
        //构建指标
        Map<String, Object> map2 = new HashMap();
        map2.put("id", departmentId);
        map2.put("marketId", marketId);
        queryFeeInput.setConditionParams(map2);
        return chargeRuleRpc.queryFee(queryFeeInput);
    }
}